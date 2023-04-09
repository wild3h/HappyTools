package com.lixiang.car.happytools.tools.view

import com.intellij.ui.Gray
import com.intellij.ui.JBColor
import com.intellij.ui.components.JBScrollPane
import com.lixiang.car.happytools.tools.constants.DiagramConstants
import com.lixiang.car.happytools.tools.constants.DiagramConstants.OPERATION_HEIGHT
import com.lixiang.car.happytools.tools.constants.DiagramConstants.OPERATION_SPLIT_Y
import com.lixiang.car.happytools.tools.constants.DiagramConstants.OPERATION_WIDTH
import com.lixiang.car.happytools.tools.constants.DiagramConstants.SEQ_HEIGHT
import com.lixiang.car.happytools.tools.constants.DiagramConstants.SEQ_WIDTH
import com.lixiang.car.happytools.tools.diagram.CustomDelegate
import com.lixiang.car.happytools.tools.diagram.DiagramDelegate
import com.lixiang.car.happytools.tools.entity.SeqLifecycle
import com.lixiang.car.happytools.tools.entity.SequenceDiagramElement
import com.lixiang.car.happytools.tools.util.StringMetrics
import java.awt.Color
import java.awt.Cursor
import java.awt.Dimension
import java.awt.Graphics
import java.awt.event.MouseEvent
import java.awt.event.MouseListener
import java.awt.event.MouseMotionListener
import javax.swing.JPanel
import kotlin.math.abs

class SequenceDiagramPanel : JPanel() {
    companion object {
        const val MAX_WIDTH = 800
        const val MAX_HEIGHT = 700
        const val DRAW_START_X = 150
        const val DRAW_START_Y = 20
    }

    var diagramDelegate: DiagramDelegate = CustomDelegate(this)


    init {
        addMouseWheelListener {
            diagramDelegate.mouseWheelMoved(it)
        }
        addMouseMotionListener(object : MouseMotionListener {
            override fun mouseDragged(e: MouseEvent?) {
                diagramDelegate.mouseDragged(e)
            }

            override fun mouseMoved(e: MouseEvent?) {

            }

        })

        addMouseListener(object : MouseListener {
            override fun mouseClicked(e: MouseEvent?) {

            }

            override fun mousePressed(e: MouseEvent?) {
                diagramDelegate.mousePressed(e)
            }

            override fun mouseReleased(e: MouseEvent?) {
                diagramDelegate.mouseReleased(e)
            }

            override fun mouseEntered(e: MouseEvent?) {
            }

            override fun mouseExited(e: MouseEvent?) {
                diagramDelegate.mouseExited(e)
            }

        })
    }


    public override fun paintComponent(g: Graphics) {
        var lastTime = System.currentTimeMillis()
        super.paintComponent(g)
        diagramDelegate?.repaint()
        diagramDelegate.onMeasure(g)
        diagramDelegate.onDraw(g)

        //println("paintComponent ${System.currentTimeMillis() - lastTime}")
//        drawAtLast(g)
    }


//    private fun drawAtLast(g: Graphics) {
//        drawVerticalScrollBar(g)
//    }

//    private fun drawVerticalScrollBar(g: Graphics) {
//        val scrollBarHeight =
//            MAX_HEIGHT * MAX_HEIGHT / (elements.size * (OPERATION_HEIGHT + OPERATION_SPLIT_Y) + SEQ_HEIGHT + 50).toDouble()
//        val paddingTop = (MAX_HEIGHT - scrollBarHeight) * scrollNum * 20 / (elements.size * OPERATION_HEIGHT).toDouble()
//        g.drawRoundRect(MAX_WIDTH - 20, paddingTop.toInt(), 18, scrollBarHeight.toInt(), 5, 5)
//        val oldColor = g.color
//        g.color = JBColor(Color.yellow, Gray._80);
//        g.fillRoundRect(MAX_WIDTH - 20 + 1, paddingTop.toInt() + 1, 18 - 1, scrollBarHeight.toInt() - 1, 5, 5)
//        g.color = oldColor
//    }

    fun setElements(elements: ArrayList<SequenceDiagramElement>) {
        diagramDelegate.setElement(elements)
    }

    override fun getPreferredSize(): Dimension {
        return Dimension(MAX_WIDTH, MAX_HEIGHT)
    }
}

