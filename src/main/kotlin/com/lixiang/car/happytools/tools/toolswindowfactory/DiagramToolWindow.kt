package com.lixiang.car.happytools.tools.toolswindowfactory

import com.google.gson.Gson
import com.intellij.openapi.project.Project
import com.intellij.openapi.ui.JBPopupMenu
import com.intellij.openapi.wm.ToolWindow
import com.intellij.openapi.wm.ToolWindowFactory
import com.intellij.ui.EditorComboBox
import com.intellij.ui.components.JBLabel
import com.intellij.ui.components.JBPanel
import com.intellij.ui.components.JBTextField
import com.intellij.ui.content.ContentFactory
import com.lixiang.car.happytools.tools.data.DownloadManager
import com.lixiang.car.happytools.tools.data.LogConfigBeans
import com.lixiang.car.happytools.tools.entity.SequenceDiagramElement
import com.lixiang.car.happytools.tools.util.*
import com.lixiang.car.happytools.tools.util.FileUtils
import com.lixiang.car.happytools.tools.view.MultiComboBox
import com.lixiang.car.happytools.tools.view.SequenceDiagramPanel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.apache.http.HttpEntity
import org.apache.http.HttpResponse
import org.apache.http.StatusLine
import org.apache.http.client.HttpClient
import org.apache.http.client.methods.HttpGet
import org.apache.http.impl.client.DefaultHttpClient
import org.apache.http.util.EntityUtils
import org.jdesktop.swingx.JXComboBox
import org.jdesktop.swingx.JXDatePicker
import wu.seal.jsontokotlin.ui.jHorizontalLinearLayout
import java.awt.BorderLayout
import java.awt.Button
import java.awt.Dimension
import java.awt.FlowLayout
import java.io.BufferedReader
import java.io.File
import java.io.FileReader
import java.io.IOException
import javax.swing.JButton
import javax.swing.JPanel
import javax.swing.Spring
import javax.swing.SpringLayout
import javax.swing.event.DocumentEvent
import javax.swing.event.DocumentListener
import javax.swing.plaf.basic.BasicArrowButton
import kotlin.concurrent.thread


class DiagramToolWindow : ToolWindowFactory {

    private val rootView by lazy {
        JPanel()
    }
    private val sequenceDiagramPanel by lazy {
        SequenceDiagramPanel()
    }
    private val lifecycleSelector by lazy {
        MultiComboBox() {
            sequenceDiagramPanel.diagramDelegate.setDrawLifecycles(it.toList())
            sequenceDiagramPanel.repaint()
        }.apply {
            preferredSize = Dimension(200, 30)
        }
    }
    private val vinConfigPanel by lazy {
        JBTextField().apply {
            preferredSize = Dimension(150, 30)
            var lastUpdateTime:Long? = null
            document.addDocumentListener(object :DocumentListener{
                override fun insertUpdate(e: DocumentEvent?) {
                }

                override fun removeUpdate(e: DocumentEvent?) {
                }

                override fun changedUpdate(e: DocumentEvent?) {
                    GlobalScope.launch {
                        withContext(Dispatchers.IO){
                            val vin = text
                            val url = "https://dip-data-msg-parsing-service.prod.k8s.chehejia.com/v1-0/msg-parsing/common/vehicles/pagination?pageNum=1&pageSize=100&vinContains=LW433B120N1024099"
                            val request = HttpGet(url)
                            println(url)
                            val client: HttpClient = DefaultHttpClient()
                            // 发起请求并获取响应对象
                            val response: HttpResponse = client.execute(request)
                            // 获取响应状态码
                            val statusLine: StatusLine = response.statusLine
                            val statusCode: Int = statusLine.statusCode
                            // 获取响应内容
                            val entity: HttpEntity = response.entity
                            val content: String = EntityUtils.toString(entity)
                        }
                    }
                }
            })
        }
    }

    val format = "yyyy-MM-dd+HH:mm:ss.SSS"
    private val startJXDatePicker by lazy {
        JXDatePicker().apply {
            setFormats(format)
            preferredSize = Dimension(250, 30)
        }
    }

    private val endJXDatePicker by lazy {
        JXDatePicker().apply {
            setFormats(format)
            preferredSize = Dimension(250, 30)
        }
    }

    private val logTypeComboBox by lazy {
        JXComboBox(arrayOf("log_HUF_8155_android"))
    }

    private val wordsTextArea = JBTextField().apply {
        preferredSize = Dimension(300, 30)
    }

    private val wordsLine by lazy {
        jHorizontalLinearLayout {
            add(JBLabel("过滤词：(支持多个过滤词，需英文逗号隔开)").apply {
                minimumSize = Dimension(10, 30)
            })
            add(wordsTextArea)
        }
    }

    override fun createToolWindowContent(project: Project, toolWindow: ToolWindow) {
        val contentFactory = ContentFactory.SERVICE.getInstance()
        initRootView(project)
        val content = contentFactory.createContent(rootView, "", false)
        toolWindow.contentManager.addContent(content)
        GlobalScope.launch {
            val logTypeUrl = "https://dip-data-msg-parsing-service.prod.k8s.chehejia.com/v1-0/msg-parsing/hu-log-files/log-classes?tag=log"
        }
    }

    private fun initRootView(project: Project) {
        val run = JButton("run").apply {
            this.addActionListener {
                val config = LogConfigBeans(wordsTextArea.text.split(','), logTypeComboBox.selectedItem.toString(), endJXDatePicker.getFormatDate(format), startJXDatePicker.getFormatDate(format), vinConfigPanel.text)
                DownloadManager.download(project, config, 1, sequenceDiagramPanel) {
                    lifecycleSelector.setValues(arrayListOf<String>().apply {
                        add("全选")
                        addAll(sequenceDiagramPanel.diagramDelegate.getDrawLifecycles().map { it.element.className })
                    }.toTypedArray())
                }

            }
        }
        val springLayout = SpringLayout()
        rootView.layout = springLayout
        val vinTitle = JBLabel("VIN").apply {
            preferredSize = Dimension(40, 30)
        }
        val startTitle = JBLabel("开始时间：").apply {
            minimumSize = Dimension(60, 30)
        }
        val endTitle = JBLabel("  结束时间：").apply {
            minimumSize = Dimension(60, 30)
        }
        val timeLine = jHorizontalLinearLayout {
            add(startTitle)
            add(startJXDatePicker)
            add(endTitle)
            add(endJXDatePicker)
        }
        val logTypeTitle = JBLabel("业务类型：").apply {
            preferredSize = Dimension(80, 30)
        }
        rootView.add(sequenceDiagramPanel, run, lifecycleSelector, vinTitle, vinConfigPanel, timeLine, logTypeTitle,logTypeComboBox, wordsLine)

        val vinCons = springLayout.getConstraints(vinTitle)
        vinCons.x = Spring.constant(20)
        vinCons.y = Spring.constant(20)

        vinConfigPanel.leftToRight(vinTitle)
        vinConfigPanel.topToTop(vinTitle)
        logTypeTitle.leftToRight(vinConfigPanel)
        logTypeTitle.topToTop(vinConfigPanel)
        logTypeComboBox.leftToRight(logTypeTitle)
        logTypeComboBox.topToTop(logTypeTitle)

        timeLine.topToBottom(vinTitle)
        timeLine.leftToLeft(vinTitle)

        wordsLine.topToBottom(timeLine)
        wordsLine.leftToLeft(timeLine)

        run.topToBottom(wordsLine)
        run.leftToLeft(vinTitle)
        lifecycleSelector.leftToRight(run, 5)
        lifecycleSelector.topToTop(run)

        sequenceDiagramPanel.topToBottom(run, 20)
        sequenceDiagramPanel.leftToLeft(vinTitle)
    }

}