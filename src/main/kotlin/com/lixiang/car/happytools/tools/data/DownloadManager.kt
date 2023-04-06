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
import java.net.URL
import java.text.SimpleDateFormat
import java.util.*
import java.util.zip.ZipEntry
import java.util.zip.ZipInputStream


object DownloadManager {
    @Volatile
    var downloading = false
    fun download(project: Project, config: LogConfigBeans, pageNo: Int, sequenceDiagramPanel: SequenceDiagramPanel, onSuccess: () -> Unit) {
        if (downloading) {
            NotifyUtil.notifyMessage("别催了~已经开始下载了QAQ")
            return
        }
        NotifyUtil.notifyMessage("开始下载！")
        downloading = true
        GlobalScope.launch {
            val listData = arrayListOf<SequenceDiagramElement>()
            withContext(Dispatchers.IO) {
                val client: HttpClient = DefaultHttpClient()
                // 创建HttpGet对象
                val url =
                    "https://dip-data-msg-parsing-service.prod.k8s.chehejia.com/v1-0/msg-parsing/hu-log-files/pagination?collectTimeMax=${config.time_end_date}+${config.time_end_time}&collectTimeMin=${config.time_start_date}+${config.time_start_time}&logClass=${config.log_type}&vin=${config.vin}&remoteMode=false&pageNo=${pageNo}&pageSize=100&logLevel="
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
                val logData = Gson().fromJson<LogData>(content, object : TypeToken<LogData>() {}.type)
                val logFiles = arrayListOf<String>()
                logData.data.list.forEach {
                    try {
                        val url = URL(it.downloadURL)
                        val zipFile = File(com.lixiang.car.happytools.tools.util.FileUtils.defaultFileFolder() + it.fileName)
                        if (!zipFile.exists()) {
                            FileUtils.copyURLToFile(url, zipFile)
                        }
                        val fis = FileInputStream(zipFile)
                        val zis = ZipInputStream(fis)
                        var ze: ZipEntry? = zis.nextEntry
                        while (ze != null) {
                            val fileName: String = ze.name
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
                                    if (line.toString().contains(keyWord)) {
                                        val pattern = Regex("\\d{4}-\\d{2}-\\d{2}\\ \\d{2}:\\d{2}:\\d{2}.\\d{3}")
                                        val matches = pattern.findAll(line.toString()).map { it.value }.toList()
                                        matches.firstOrNull()?.let { time ->
                                            val timePattern = "yyyy-MM-dd HH:mm:ss.SSS"
                                            val dateFormat = SimpleDateFormat(timePattern)
                                            val date = dateFormat.parse(time)
                                            val timestamp = date?.time ?: 0
                                            val charAt = line.toString().indexOf(':', 38)
                                            listData.add(SequenceDiagramElement(timestamp,time, line.toString().substring(24, 30), line.toString().substring(38, charAt), line.toString().substring(charAt + 1)))
                                        }
                                    }
                                }
                            }
                            br.close()
                        }
                    } catch (ex: Exception) {
                        ex.printStackTrace()
                        downloading = false
                    }
                }
                listData.sortBy {
                    it.time
                }
            }
            sequenceDiagramPanel.setElements(listData)
            downloading = false
            onSuccess()
            NotifyUtil.notifyMessage("分析完成！")
        }
    }
}