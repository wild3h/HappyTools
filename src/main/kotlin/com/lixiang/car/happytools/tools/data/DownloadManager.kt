package com.lixiang.car.happytools.tools.data

import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.intellij.openapi.project.Project
import com.lixiang.car.happytools.tools.entity.SequenceDiagramElement
import com.lixiang.car.happytools.tools.util.NotifyUtil
import com.lixiang.car.happytools.tools.view.SequenceDiagramPanel
import kotlinx.coroutines.*
import org.apache.commons.io.FileUtils
import org.apache.http.HttpEntity
import org.apache.http.HttpResponse
import org.apache.http.StatusLine
import org.apache.http.client.HttpClient
import org.apache.http.client.methods.HttpGet
import org.apache.http.impl.client.DefaultHttpClient
import org.apache.http.util.EntityUtils
import java.io.*
import java.lang.reflect.Type
import java.net.URL
import java.text.SimpleDateFormat
import java.util.zip.ZipEntry
import java.util.zip.ZipInputStream
import kotlin.collections.ArrayList


object DownloadManager {
    @Volatile
    var downloading = false
    fun download(
        project: Project,
        config: LogConfigBeans,
        pageNo: Int,
        sequenceDiagramPanel: SequenceDiagramPanel,
        onSuccess: () -> Unit,
        onProgress: ((Int) -> Unit)? = null
    ) {
        if (downloading) {
            NotifyUtil.notifyMessage("别催了~已经开始下载了QAQ")
            return
        }
        NotifyUtil.notifyMessage("开始下载！")
        downloading = true
        GlobalScope.launch {
            val res = withContext(Dispatchers.IO) {
                val listData: ArrayList<SequenceDiagramElement> = arrayListOf()
                val url = "https://dip-data-msg-parsing-service.prod.k8s.chehejia.com/v1-0/msg-parsing/hu-log-files/pagination?collectTimeMax=${config.time_end_date}&collectTimeMin=${config.time_start_date}&logClass=${config.log_type}&vin=${config.vin}&remoteMode=false&pageNo=${pageNo}&pageSize=100&logLevel="
                val content: String = requestUrl(url)
                val logData = Gson().fromJson<BaseResp<LogItem>>(content, object : TypeToken<BaseResp<LogItem>>() {}.type)
                val totalLength = logData?.data?.total ?: 0
                val pageNum = totalLength / 100 + 1
                var addFile = 0
                for (i in 1 until pageNum + 1) {
                    val requestUrl = "https://dip-data-msg-parsing-service.prod.k8s.chehejia.com/v1-0/msg-parsing/hu-log-files/pagination?collectTimeMax=${config.time_end_date}&collectTimeMin=${config.time_start_date}&logClass=${config.log_type}&vin=${config.vin}&remoteMode=false&pageNo=${i}&pageSize=100&logLevel="
                    requestUrl<LogItem>(requestUrl, object : TypeToken<BaseResp<LogItem>>() {}.type) {
                        it?.data?.list?.forEachIndexed { index, logItem ->
                            try {
                                val logFiles = arrayListOf<String>()
                                val url = URL(logItem.downloadURL)
                                val zipFile = File(com.lixiang.car.happytools.tools.util.FileUtils.defaultFileFolder() + logItem.fileName)
                                if (!zipFile.exists()) {
                                    FileUtils.copyURLToFile(url, zipFile)
                                }
                                val fis = FileInputStream(zipFile)
                                val zis = ZipInputStream(fis)
                                var ze: ZipEntry? = zis.nextEntry
                                while (ze != null) {
                                    val fileName: String = com.lixiang.car.happytools.tools.util.FileUtils.defaultFileFolder() + ze.name
                                    if (fileName.endsWith(".log")) {
                                        logFiles.add(fileName)
                                        // create a new file to extract the entry
                                        val newFile = File(fileName)
                                        val fos = FileOutputStream(newFile)
                                        val buffer = ByteArray(1024)
                                        var len: Int
                                        while (zis.read(buffer).also { len = it } > 0) {
                                            fos.write(buffer, 0, len)
                                        }
                                        fos.close()
                                    }
                                    ze = zis.nextEntry
                                }
                                zis.closeEntry()
                                zis.close()
                                fis.close()

                                for (logFile in logFiles) {
                                    val br = BufferedReader(FileReader(logFile))
                                    var line: String? = null
                                    while (br.readLine()?.also {
                                            line = it
                                        } != null) {
                                        config.key_word.forEach { keyWord ->
                                            if (keyWord.isBlank()) {
                                                return@forEach
                                            }
                                            val lineStr = line.toString()
                                            if (lineStr.contains(keyWord)) {
                                                val pattern = Regex("\\d{4}-\\d{2}-\\d{2}\\ \\d{2}:\\d{2}:\\d{2}.\\d{3}")
                                                val matches = pattern.findAll(lineStr).map { it.value }.toList()
                                                matches.firstOrNull()?.let { time ->
                                                    val timePattern = "yyyy-MM-dd HH:mm:ss.SSS"
                                                    val dateFormat = SimpleDateFormat(timePattern)
                                                    val date = dateFormat.parse(time)
                                                    val timestamp = date?.time ?: 0
                                                    val charAt = lineStr.indexOf(':', 38)
                                                    listData.add(
                                                        SequenceDiagramElement(
                                                            timestamp,
                                                            time,
                                                            lineStr.substring(24, 30),
                                                            lineStr.substring(30, 36),
                                                            lineStr.substring(38, charAt.coerceAtLeast(38)),
                                                            lineStr.substring(charAt.coerceAtLeast(38) + 1), lineStr)
                                                    )
                                                }
                                            }
                                        }
                                    }
                                    br.close()
                                }
                                withContext(Dispatchers.Default) {
                                    onProgress?.invoke(((addFile++) * 100) / totalLength)
                                }
                            } catch (ex: Exception) {
                                ex.printStackTrace()
                                NotifyUtil.notifyMessage(ex.stackTraceToString())
                            }
                        } ?: kotlin.run {
                            NotifyUtil.notifyMessage("数据为空")
                        }
                    }
                }

                listData.sortBy {
                    it.time
                }
                return@withContext listData
            }
            sequenceDiagramPanel.setElements(res)
            downloading = false
            onSuccess()
            NotifyUtil.notifyMessage("分析完成！")
        }
    }

    suspend fun <T> requestUrl(url: String, type: Type, onSuccess: suspend (BaseResp<T>?) -> Unit) {
        val fromJson = try {
            val client: HttpClient = DefaultHttpClient()
            // 创建HttpGet对象
            val request = HttpGet(url)
            println(url)

            // 发起请求并获取响应对象
            val response: HttpResponse = client.execute(request)
            // 获取响应状态码
            val statusLine: StatusLine = response.statusLine
            val statusCode: Int = statusLine.statusCode
            // 获取响应内容
            val entity: HttpEntity = response.entity
            val content: String = EntityUtils.toString(entity)
            // 处理响应数据
            println("Status code: $statusCode")
            println("Response content: $content")
            Gson().fromJson<BaseResp<T>>(content, type)

        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
        withContext(Dispatchers.Default) {
            try {
                onSuccess(fromJson)
            } catch (ex: Exception) {
                ex.printStackTrace()
            }
        }
    }

    suspend fun requestUrl(url: String): String {
        try {
            val client: HttpClient = DefaultHttpClient()
            // 创建HttpGet对象
            val request = HttpGet(url)
            println(url)

            // 发起请求并获取响应对象
            val response: HttpResponse = client.execute(request)
            // 获取响应状态码
            val statusLine: StatusLine = response.statusLine
            val statusCode: Int = statusLine.statusCode
            // 获取响应内容
            val entity: HttpEntity = response.entity
            val content: String = EntityUtils.toString(entity)
            // 处理响应数据
            println("Status code: $statusCode")
            println("Response content: $content")
            return content
        } catch (e: Exception) {
            e.printStackTrace()
            return ""
        }
    }
}