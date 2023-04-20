package com.lixiang.car.happytools.tools.view

import com.intellij.ui.components.JBTextField
import java.awt.Dimension

class ToolTextField(private val width:Int):JBTextField() {
    override fun getPreferredSize(): Dimension {
        return Dimension(width,super.getPreferredSize().height)
    }
}