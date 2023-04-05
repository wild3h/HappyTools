package com.lixiang.car.happytools.tools.toolswindowfactory

import com.google.gson.Gson
import com.intellij.openapi.project.Project
import com.intellij.openapi.wm.ToolWindow
import com.intellij.openapi.wm.ToolWindowFactory
import com.intellij.ui.EditorComboBox
import com.intellij.ui.content.ContentFactory
import com.lixiang.car.happytools.tools.data.DownloadManager
import com.lixiang.car.happytools.tools.data.LogConfigBeans
import com.lixiang.car.happytools.tools.entity.SequenceDiagramElement
import com.lixiang.car.happytools.tools.util.*
import com.lixiang.car.happytools.tools.util.FileUtils
import com.lixiang.car.happytools.tools.view.MultiComboBox
import com.lixiang.car.happytools.tools.view.SequenceDiagramPanel
import java.awt.Button
import java.awt.Dimension
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
    val lifecycleSelector  by lazy{
        MultiComboBox(){
            sequenceDiagramPanel.drawLifecycleList = it.toList()
            sequenceDiagramPanel.repaint()
        }
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
                        DownloadManager.download(project, config, 1,sequenceDiagramPanel){
                            lifecycleSelector.setValues(arrayListOf<String>().apply {
                                add("全选")
                                addAll(sequenceDiagramPanel.lifecycleList)
                            }.toTypedArray())
                        }
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
        rootView.add(lifecycleSelector)
        lifecycleSelector.preferredSize = Dimension(200,30)
        val seqCons = springLayout.getConstraints(sequenceDiagramPanel)
        val editCons = springLayout.getConstraints(editConfig)
        val runCons = springLayout.getConstraints(run)

        seqCons.x = Spring.constant(20)
        seqCons.y = Spring.constant(50)

        editConfig.bottomToTop(sequenceDiagramPanel,10)
        editConfig.leftToLeft(sequenceDiagramPanel)
        run.bottomToBottom(editConfig)
        run.leftToRight(editConfig,5)
        lifecycleSelector.leftToRight(run,5)
        lifecycleSelector.topToTop(editConfig)
    }

    private fun initConfigFile(project: Project) {
        CodeManager.onlyOpenCode(project, "log_config.json", """{
        "vin": "LW433B127N1008126",
        "time_start_date": "2023-04-05",
        "time_start_time": "13:04:50",
        "time_end_date": "2023-04-05",
        "time_end_time": "23:10:00",
        "log_type":"log_HUF_8155_android",
        "key_word":["kg"]
}""")
    }

}