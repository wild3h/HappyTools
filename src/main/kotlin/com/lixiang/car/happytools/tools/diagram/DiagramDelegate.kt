package com.lixiang.car.happytools.tools.diagram

import com.lixiang.car.happytools.tools.entity.SeqLifecycle
import com.lixiang.car.happytools.tools.entity.SequenceDiagramElement
import java.awt.Graphics
import java.awt.event.MouseEvent
import java.awt.event.MouseWheelEvent

interface DiagramDelegate {

    fun repaint()
    fun onMeasure(g:Graphics)

    fun onDraw(g:Graphics)

    fun mouseWheelMoved(e: MouseWheelEvent)

    fun mouseDragged(e: MouseEvent?)

    fun mouseMoved(e: MouseEvent?)

    fun mousePressed(e: MouseEvent?)

    fun mouseReleased(e: MouseEvent?)

    fun mouseExited(e: MouseEvent?)

    fun setElement(data:List<SequenceDiagramElement>)

    fun getDrawLifecycles():List<SeqLifecycle>

    fun setDrawLifecycles(data:List<String>)
}