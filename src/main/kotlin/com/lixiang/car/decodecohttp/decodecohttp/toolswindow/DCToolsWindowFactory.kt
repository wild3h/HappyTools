package com.lixiang.car.decodecohttp.decodecohttp.toolswindow

import com.intellij.openapi.project.Project
import com.intellij.openapi.wm.RegisterToolWindowTask
import com.intellij.openapi.wm.ToolWindow
import com.intellij.openapi.wm.ToolWindowFactory
import com.intellij.openapi.wm.ToolWindowManager
import com.intellij.ui.content.Content
import com.intellij.ui.content.ContentFactory
import com.lixiang.car.decodecohttp.decodecohttp.util.PluginConstant
import com.lixiang.car.decodecohttp.toolswindow.DCToolWindows

class DCToolsWindowFactory : ToolWindowFactory {

    companion object{
        var ID: String = "DecodeCohttp"
    }
    override fun createToolWindowContent(project: Project, toolWindow: ToolWindow) {
        val content = createContent(project, toolWindow)
        toolWindow.contentManager.addContent(content)
    }

    private fun createContent(project: Project,toolWindow: ToolWindow): Content {
        val contentFactory = ContentFactory.SERVICE.getInstance()
        val content = contentFactory.createContent(DCToolWindows(project,toolWindow).contentPanel, "", false)
        return content
    }
}