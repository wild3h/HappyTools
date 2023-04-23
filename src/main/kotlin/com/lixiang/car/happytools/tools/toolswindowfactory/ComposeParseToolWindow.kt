package com.lixiang.car.happytools.tools.toolswindowfactory

import com.intellij.openapi.project.Project
import com.intellij.openapi.wm.ToolWindow
import com.intellij.openapi.wm.ToolWindowFactory
import com.intellij.ui.content.ContentFactory
import javax.swing.JComponent
import javax.swing.JPanel
import javax.swing.SpringLayout

class ComposeParseToolWindow: BaseToolWindow() {

    override fun getDisplayName(): String {
        return "ComposeParse"
    }

    override fun initView(project: Project) {

        val springLayout = SpringLayout()
        rootView.layout = springLayout

    }
}