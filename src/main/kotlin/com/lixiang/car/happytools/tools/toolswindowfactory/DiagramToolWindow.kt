package com.lixiang.car.happytools.tools.toolswindowfactory

import com.google.gson.Gson
import com.intellij.openapi.project.Project
import com.intellij.openapi.wm.ToolWindow
import com.intellij.openapi.wm.ToolWindowFactory
import com.intellij.ui.content.ContentFactory
import com.lixiang.car.happytools.tools.data.DownloadManager
import com.lixiang.car.happytools.tools.data.LogConfigBeans
import com.lixiang.car.happytools.tools.entity.SequenceDiagramElement
import com.lixiang.car.happytools.tools.util.*
import com.lixiang.car.happytools.tools.util.FileUtils
import com.lixiang.car.happytools.tools.view.SequenceDiagramPanel
import java.awt.Button
import java.io.BufferedReader
import java.io.File
import java.io.FileReader
import java.io.IOException
import javax.swing.JPanel
import javax.swing.Spring
import javax.swing.SpringLayout
import kotlin.concurrent.thread


class DiagramToolWindow : ToolWindowFactory {

    val rootView by lazy {
        JPanel()
    }
    val sequenceDiagramPanel by lazy {
        SequenceDiagramPanel()
    }

    override fun createToolWindowContent(project: Project, toolWindow: ToolWindow) {
        val contentFactory = ContentFactory.SERVICE.getInstance()
        initRootView(project)
        val content = contentFactory.createContent(rootView, "", false)
        toolWindow.contentManager.addContent(content)
    }

    private fun initRootView(project: Project) {
        val run = Button("run").apply {
            this.addActionListener {
                val gson = Gson()

                try {
                    val defaultFileFolder = FileUtils.defaultFileFolder()
                    BufferedReader(FileReader(File(defaultFileFolder + "log_config.json"))).use { reader ->
                        // 读取json文件内容
                        val jsonStr = StringBuilder()
                        var line: String?
                        while (reader.readLine().also { line = it } != null) {
                            jsonStr.append(line)
                        }

                        // 将json字符串转换为Java对象
                        val config: LogConfigBeans = gson.fromJson(jsonStr.toString(), LogConfigBeans::class.java)
                        DownloadManager.download(project, config.time_end_date, config.time_start_date, config.log_type, config.vin, 1,config.key_word,sequenceDiagramPanel)
                    }
                } catch (e: IOException) {
                    e.printStackTrace()
                }

            }
        }
        val editConfig = Button("编辑配置").apply {
            this.addActionListener {
                initConfigFile(project)
            }
        }
        val springLayout = SpringLayout()
        rootView.layout = springLayout
        rootView.add(sequenceDiagramPanel)
        rootView.add(editConfig)
        rootView.add(run)
        val seqCons = springLayout.getConstraints(sequenceDiagramPanel)
        val editCons = springLayout.getConstraints(editConfig)
        val runCons = springLayout.getConstraints(run)

        seqCons.x = Spring.constant(20)
        seqCons.y = Spring.constant(40)

        editConfig.bottomToTop(sequenceDiagramPanel)
        editConfig.leftToLeft(sequenceDiagramPanel)
        run.bottomToBottom(editConfig)
        run.leftToRight(editConfig)
    }

    private fun initConfigFile(project: Project) {
        CodeManager.onlyOpenCode(project, "log_config.json", """{
        "vin": "LW433B128N1602854",
        "time_start_date": "2023-03-24",
        "time_start_time": "13:04:50",
        "time_end_date": "2023-03-24",
        "time_end_time": "13:10:00",
        "log_type":"log_HUF_8155_android",
        "key_word":["Kg_C_MvRockNRollActivity","Kg_C_LyricLineUi","Kg_C_MvViewModel"]
    }""")
    }

}