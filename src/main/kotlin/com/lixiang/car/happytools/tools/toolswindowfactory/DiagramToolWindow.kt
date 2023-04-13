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
import org.jdesktop.swingx.JXComboBox
import org.jdesktop.swingx.JXDatePicker
import wu.seal.jsontokotlin.ui.jHorizontalLinearLayout
import java.awt.BorderLayout
import java.awt.Button
import java.awt.Dimension
import java.io.BufferedReader
import java.io.File
import java.io.FileReader
import java.io.IOException
import javax.swing.JButton
import javax.swing.JPanel
import javax.swing.Spring
import javax.swing.SpringLayout
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

    override fun createToolWindowContent(project: Project, toolWindow: ToolWindow) {
        val contentFactory = ContentFactory.SERVICE.getInstance()
        initRootView(project)
        val content = contentFactory.createContent(rootView, "", false)
        toolWindow.contentManager.addContent(content)
    }

    private fun initRootView(project: Project) {
        val run = JButton("run").apply {
            this.addActionListener {
                val config = LogConfigBeans(arrayListOf("Kg"), "logTypeComboBox.editor.", endJXDatePicker.getFormatDate(format), startJXDatePicker.getFormatDate(format), vinConfigPanel.text)
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
        rootView.add(sequenceDiagramPanel, run, lifecycleSelector, vinTitle, vinConfigPanel, timeLine, logTypeTitle,logTypeComboBox)

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

        run.topToBottom(timeLine)
        run.leftToLeft(vinTitle)
        lifecycleSelector.leftToRight(run, 5)
        lifecycleSelector.topToTop(run)

        sequenceDiagramPanel.topToBottom(run, 20)
        sequenceDiagramPanel.leftToLeft(vinTitle)
    }

}