package com.lixiang.car.decodecohttp.decodecohttp.toolswindow

import com.lixiang.car.decodecohttp.toolswindow.DCToolWindows
import com.intellij.openapi.project.Project
import com.intellij.openapi.wm.ToolWindow
import com.intellij.openapi.wm.ToolWindowFactory
import com.intellij.ui.content.Content
import com.intellij.ui.content.ContentFactory

class DCToolsWindowFactory : ToolWindowFactory {
    override fun createToolWindowContent(project: Project, toolWindow: ToolWindow) {
        toolWindow.contentManager.addContent(createContent(project,toolWindow))
    }

    private fun createContent(project: Project,toolWindow: ToolWindow): Content {
        val contentFactory = ContentFactory.SERVICE.getInstance()
        val content = contentFactory.createContent(DCToolWindows(project,toolWindow).contentPanel, "", false)
        return content
    }
}