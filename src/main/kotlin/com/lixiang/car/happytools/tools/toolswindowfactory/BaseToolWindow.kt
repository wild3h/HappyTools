package com.lixiang.car.happytools.tools.toolswindowfactory

import com.intellij.openapi.project.Project
import com.intellij.openapi.wm.ToolWindow
import com.intellij.openapi.wm.ToolWindowFactory
import com.intellij.ui.content.ContentFactory
import javax.swing.JComponent
import javax.swing.JPanel
import javax.swing.SpringLayout

abstract class BaseToolWindow: ToolWindowFactory {

    protected val rootView = JPanel()
    override fun createToolWindowContent(project: Project, toolWindow: ToolWindow) {
        val contentFactory = ContentFactory.SERVICE.getInstance()
        initRootView()
        initView(project)
        val content = contentFactory.createContent(getRootView(), "", false)
        toolWindow.contentManager.addContent(content)
    }

    abstract fun getDisplayName(): String

    open fun getRootView(): JComponent {
        return rootView
    }

    open fun initRootView() {
    }
    abstract fun initView(project: Project)
}