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
import com.lixiang.car.happytools.tools.entity.SeqLifecycle
import com.lixiang.car.happytools.tools.entity.SequenceDiagramElement
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
    }

    private val drawYStart = 20
    private var drawXStart = 0
    private var elements = ArrayList<SequenceDiagramElement>()
    private var drawElements = ArrayList<SeqLifecycle>()
    private var lastLifecycle: String? = null
    private var scrollNum: Double = 0.0
    private var lastY: Int = OPERATION_SPLIT_Y + SEQ_HEIGHT + 20 - (scrollNum * 20).toInt()

    private var lastClickX: Int? = null
    private var lastClickY: Int? = null

    init {
        //https://www.runoob.com/manual/jdk1.6/java.desktop/java/awt/event/MouseWheelEvent.html
        addMouseWheelListener {
            val isScrollEvent = JBScrollPane.isScrollEvent(it)
            val isFromSeqPanel = it.source is SequenceDiagramPanel
            if (isScrollEvent && isFromSeqPanel) {
                scrollNum = (scrollNum + it.preciseWheelRotation).coerceAtLeast(0.0).coerceAtMost(getMaxScrollNum().toDouble())
                repaint()
            }
        }
        addMouseMotionListener(object : MouseMotionListener {
            override fun mouseDragged(e: MouseEvent?) {
                if (lastClickX != null && lastClickY != null && e != null) {
                    val currentX = e.x
                    val currentY = e.y
                    val moveY = lastClickY!! - currentY
                    val moveX = currentX - lastClickX!!
                    drawXStart = (drawXStart + moveX).coerceAtMost(0)
                    println("$scrollNum，${getMaxScrollNum()}")
                    if (scrollNum < 0 || scrollNum > getMaxScrollNum()) {
                        return
                    }
                    scrollNum = (scrollNum + moveY / 20.0).coerceAtLeast(0.0).coerceAtMost(getMaxScrollNum().toDouble())
                    repaint()
                    lastClickX = currentX
                    lastClickY = currentY
                }
            }

            override fun mouseMoved(e: MouseEvent?) {

            }

        })

        addMouseListener(object : MouseListener {
            override fun mouseClicked(e: MouseEvent?) {

            }

            override fun mousePressed(e: MouseEvent?) {
                println("mousePressed")
                lastClickX = e?.x
                lastClickY = e?.y
                cursor = Cursor.getPredefinedCursor(Cursor.MOVE_CURSOR);
            }

            override fun mouseReleased(e: MouseEvent?) {
                println("mouseReleased")
                lastClickX = null
                lastClickY = null
            }

            override fun mouseEntered(e: MouseEvent?) {
                println("mouseEntered")
            }

            override fun mouseExited(e: MouseEvent?) {
                println("mouseExited")
                lastClickX = null
                lastClickY = null
            }

        })
    }

    public override fun paintComponent(g: Graphics) {
        super.paintComponent(g)
        drawElements.clear()
        lastLifecycle = null
        lastY = OPERATION_SPLIT_Y + SEQ_HEIGHT + 20 - (scrollNum * 20).toInt()
        for (element in elements) {
            if (element.isActivity()) {
                drawLifecycle(g, element)
                drawArrow(g, element.className)
                lastLifecycle = element.className
                drawOperation(g, element)
            } else {
                drawArrow(g, lastLifecycle.toString())
                drawOperation(g, element)
            }
        }
        drawVerticalScrollBar(g)
    }

    private fun drawVerticalScrollBar(g: Graphics) {
        val scrollBarHeight = MAX_HEIGHT * MAX_HEIGHT / (elements.size * (OPERATION_HEIGHT + OPERATION_SPLIT_Y) + SEQ_HEIGHT + 50).toDouble()
        val paddingTop = (MAX_HEIGHT - scrollBarHeight) * scrollNum * 20 / (elements.size * OPERATION_HEIGHT).toDouble()
        g.drawRoundRect(MAX_WIDTH - 20, paddingTop.toInt(), 18, scrollBarHeight.toInt(), 5, 5)
        val oldColor = g.color
        g.color = JBColor(Color.yellow, Gray._80);
        g.fillRoundRect(MAX_WIDTH - 20 + 1, paddingTop.toInt() + 1, 18 - 1, scrollBarHeight.toInt() - 1, 5, 5)
        g.color = oldColor
    }

    private fun drawOperation(g: Graphics, drawOperation: SequenceDiagramElement) {
        lastLifecycle?.let {
            val lastLifecycle = drawElements.find(it)!!
            val width = OPERATION_WIDTH
            val height = OPERATION_HEIGHT
            val drawOperationStartX = lastLifecycle.getMiddleX()
            val drawOperationStartY = lastY
            if ((lastY) in -200..1000) {
                drawRect(g, drawOperationStartX, lastY, width, height)
                g.drawString(
                        drawOperation.className + ": " + drawOperation.operation,
                        drawOperationStartX + 10,
                        drawOperationStartY + 30
                )
            }

            lastY = drawOperationStartY + height + OPERATION_SPLIT_Y
        } ?: kotlin.run {
            drawLifecycle(g, SequenceDiagramElement(drawOperation.time, "default", "default"))
            lastLifecycle = "default"
            drawOperation(g, drawOperation)
        }
    }

    private fun drawRect(g: Graphics, startX: Int, startY: Int, width: Int, height: Int) {
        if ((startX) in -200..1000 && (startY) in -200..1000) {
            g.drawRect(startX, startY, width, height)
            val oldColor = g.color
            g.color = JBColor(Color.yellow, Gray._80);
            g.fillRect(startX + 1, startY + 1, width - 1, height - 1)
            g.color = oldColor
        }
    }

    private fun drawLifecycle(g: Graphics, drawElement: SequenceDiagramElement) {
        if (drawElements.has(drawElement.className)) {
            return
        }
        val drawXStart = if (drawElements.size > 0) {
            val element = drawElements.last()
            element.drawX + element.width + DiagramConstants.SEQ_SPLIT_X
        } else {
            20 + drawXStart
        }
        val width = SEQ_WIDTH
        val height = SEQ_HEIGHT
        if (drawXStart in -200..1000) {
            drawRect(g, drawXStart, drawYStart, width, height)
            g.drawString(drawElement.className, drawXStart + 10, drawYStart + 30)
        }
        val seqLifecycle = SeqLifecycle(
                drawElement.className,
                drawXStart,
                drawYStart + height,
                width,
                height
        )
        drawElements.add(
                seqLifecycle
        )
        if ((seqLifecycle.getMiddleX()) in -200..1000) {
            g.drawLine(
                    seqLifecycle.getMiddleX(),
                    drawYStart + height,
                    seqLifecycle.getMiddleX(),
                    drawYStart + height + 2000
            )
        }
    }

    private fun drawArrow(g: Graphics, className: String) {
        if (lastLifecycle != null) {
            if (lastLifecycle == className) {
                val lastLifecycle = drawElements.find(lastLifecycle!!)!!
                if ((lastLifecycle.getMiddleX() + OPERATION_WIDTH - 20) in -200..1000 && (lastY - OPERATION_SPLIT_Y) in -200..1000) {
                    g.drawLine(
                            lastLifecycle.getMiddleX() + OPERATION_WIDTH - 20,
                            lastY - OPERATION_SPLIT_Y,
                            lastLifecycle.getMiddleX() + OPERATION_WIDTH - 20,
                            lastY
                    )
                }

            } else {
                val lastLifecycle = drawElements.find(lastLifecycle!!)!!
                val nextLifecycle = drawElements.find(className)!!
                if (lastLifecycle.drawX > nextLifecycle.drawX) {
                    if ((lastLifecycle.getMiddleX()) in -200..1000 && (lastY + 10) in -200..1000) {
                        g.drawLine(
                                lastLifecycle.getMiddleX(),
                                lastY + 10,
                                nextLifecycle.getMiddleX() + OPERATION_WIDTH,
                                lastY + 10
                        )
                    }
                } else {
                    if ((lastLifecycle.getMiddleX() + OPERATION_WIDTH - 20) in -200..1000 && (lastY - OPERATION_SPLIT_Y) in -200..1000) {
                        g.drawLine(
                                lastLifecycle.getMiddleX() + OPERATION_WIDTH - 20,
                                lastY - OPERATION_SPLIT_Y,
                                lastLifecycle.getMiddleX() + OPERATION_WIDTH - 20,
                                lastY + 10
                        )
                    }
                    if ((lastLifecycle.getMiddleX() + OPERATION_WIDTH - 20) in -200..1000 && (lastY + 10) in -200..1000) {
                        g.drawLine(
                                lastLifecycle.getMiddleX() + OPERATION_WIDTH - 20,
                                lastY + 10,
                                nextLifecycle.getMiddleX(),
                                lastY + 10
                        )
                    }
                }

            }
        }
    }

    fun setElements(elements: ArrayList<SequenceDiagramElement>) {
        this.elements = elements

        // 更新时序图
        repaint()
    }

    override fun getPreferredSize(): Dimension {
        return Dimension(MAX_WIDTH, MAX_HEIGHT)
    }

    private fun getMaxScrollNum(): Int {
        return abs((elements.size * (OPERATION_HEIGHT + OPERATION_SPLIT_Y) + SEQ_HEIGHT + 50 - MAX_HEIGHT) / 20)
    }
}

private fun ArrayList<SeqLifecycle>.has(className: String): Boolean {
    return this.any {
        it.className == className
    }
}

private fun ArrayList<SeqLifecycle>.find(className: String): SeqLifecycle? {
    return this.find {
        it.className == className
    }
}

