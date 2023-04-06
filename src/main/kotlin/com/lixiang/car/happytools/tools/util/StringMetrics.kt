package com.lixiang.car.happytools.tools.util

import java.awt.Font
import java.awt.Graphics

object StringMetrics {
    fun getStringWidth(text: String?, font: Font?, graphics: Graphics): Int {
        val metrics = graphics.getFontMetrics(font)
        return metrics.stringWidth(text)
    }

    fun getStringHeight(text: String?, font: Font?, graphics: Graphics): Int {
        val metrics = graphics.getFontMetrics(font)
        return metrics.height
    }
}