package com.lixiang.car.happytools.tools.diagram

import com.intellij.ui.components.JBScrollBar
import com.lixiang.car.happytools.tools.entity.SeqLifecycle
import com.lixiang.car.happytools.tools.entity.SequenceDiagramElement
import java.awt.Graphics
import java.awt.event.MouseEvent
import java.awt.event.MouseWheelEvent

abstract class DiagramDelegate {

    protected val scrollListener: ArrayList<(scrollTotal: Int) -> Unit> = arrayListOf()
    protected var scrollBar :JBScrollBar? = null
    abstract fun repaint()
    abstract fun onMeasure(g: Graphics)

    abstract fun onDraw(g: Graphics)

    abstract fun mouseWheelMoved(e: MouseWheelEvent)

    abstract fun mouseDragged(e: MouseEvent?)

    abstract fun mouseMoved(e: MouseEvent?)

    abstract fun mousePressed(e: MouseEvent?)

    abstract fun mouseReleased(e: MouseEvent?)

    abstract fun mouseExited(e: MouseEvent?)

    abstract fun setElement(data: List<SequenceDiagramElement>)

    abstract fun getSelectedElement(): List<SequenceDiagramElement>

    abstract fun getDrawLifecycles(): List<SeqLifecycle>

    abstract fun setDrawLifecycles(data: List<String>)

    open fun onScrolling(changeValue: Int) {
    }

    fun addScrollListener(listener: (scrollTotal: Int) -> Unit) {
        scrollListener.add(listener)
    }

    fun addScrollBar(scrollBar: JBScrollBar){
        this.scrollBar = scrollBar
    }
}