package com.lixiang.car.happytools.tools.diagram

import com.intellij.ui.Gray
import com.intellij.ui.JBColor
import com.intellij.ui.components.JBScrollPane
import com.lixiang.car.happytools.tools.constants.DiagramConstants
import com.lixiang.car.happytools.tools.constants.DiagramConstants.MAX_CHAR_ONE_LINE
import com.lixiang.car.happytools.tools.constants.DiagramConstants.OPERATION_PADDING_HORIZONTAL
import com.lixiang.car.happytools.tools.constants.DiagramConstants.OPERATION_PADDING_VERTICAL
import com.lixiang.car.happytools.tools.constants.DiagramConstants.OPERATION_SPLIT_Y
import com.lixiang.car.happytools.tools.constants.DiagramConstants.OPERATION_WIDTH
import com.lixiang.car.happytools.tools.constants.DiagramConstants.SEQ_HEIGHT
import com.lixiang.car.happytools.tools.constants.DiagramConstants.SEQ_SPLIT_X
import com.lixiang.car.happytools.tools.constants.DiagramConstants.SEQ_WIDTH
import com.lixiang.car.happytools.tools.entity.SeqLifecycle
import com.lixiang.car.happytools.tools.entity.SequenceDiagramElement
import com.lixiang.car.happytools.tools.util.StringMetrics
import com.lixiang.car.happytools.tools.view.SequenceDiagramPanel
import java.awt.Color
import java.awt.Graphics
import java.awt.event.MouseEvent
import java.awt.event.MouseWheelEvent
import kotlin.math.abs
import kotlin.math.ceil
import kotlin.properties.Delegates

class CustomDelegate(private val mParent: SequenceDiagramPanel) : DiagramDelegate {

    //所有elements，包含筛选前的数据
    private var elements: List<SequenceDiagramElement> = ArrayList()

    //筛选后的elements
    private var selectedElements: List<SequenceDiagramElement> = ArrayList()

    //所有需要绘制的lifecycle
    private var drawLifecycles: List<SeqLifecycle> = ArrayList()

    //所有lifecycle
    var lifecycleList: List<SeqLifecycle> = ArrayList()

    val drewLifecycle = HashMap<String, SeqLifecycle>()

    private var lastClickX: Int? = null
    private var lastClickY: Int? = null

    //画布内容的左上角X坐标
    private var drawXStart = SequenceDiagramPanel.DRAW_START_X

    //画布内容的左上角Y坐标
    private var drawYStart = SequenceDiagramPanel.DRAW_START_Y

    private var movedYTotal = 0
    private var movedXTotal = 0

    private var stringHeight by Delegates.notNull<Int>()

    private var outSize: Int = 0
    private var bottomSize: Int = -1
        get() = if (field == -1) {
            selectedElements.size
        } else {
            field
        }

//    private var leftSize: Int = 0
//    private var rightSize: Int = -1
//        get() = if (field == -1) {
//            drawLifecycles.size
//        } else {
//            field
//        }

    override fun repaint() {
//        drawXStart = SequenceDiagramPanel.DRAW_START_X
//        drawYStart = SequenceDiagramPanel.DRAW_START_Y
    }

    override fun onMeasure(g: Graphics) {
        stringHeight = StringMetrics.getStringHeight("test", g.font, g)
        SequenceDiagramPanel.DRAW_START_X = StringMetrics.getStringWidth("2023-04-19 23:48:12.150  6138  7931", g.font, g)
        // 需要计算出element的上下限 element的上下限取决于窗口高度和drawYStart
        // mParent.height - drawYStart是从最上到最下需要绘制的部分，但包含上面滑出的部分
        // (-drawYStart-SequenceDiagramPanel.DRAW_START_Y-DiagramConstants.SEQ_HEIGHT-DiagramConstants.OPERATION_SPLIT_Y).coerceAtMost(0)是上限
        // 需要计算出lifecycle的左右限
//        println("parent height=${mParent.height}")
        val bottomLine = mParent.height - drawYStart
        val topLine = (-drawYStart + SequenceDiagramPanel.DRAW_START_Y + SEQ_HEIGHT + OPERATION_SPLIT_Y).coerceAtLeast(SEQ_HEIGHT + OPERATION_SPLIT_Y)
        val operationHeight = stringHeight + OPERATION_PADDING_VERTICAL * 2 + OPERATION_SPLIT_Y
        outSize = ((topLine / operationHeight) - 1).coerceAtLeast(0)
        bottomSize = ((bottomLine / operationHeight) + 1).coerceAtMost(selectedElements.size)

//        val leftLine = (-drawXStart + SequenceDiagramPanel.DRAW_START_X).coerceAtLeast(0)
//        val rightLine = mParent.width - drawXStart
//        leftSize = ((leftLine / (SEQ_WIDTH + SEQ_SPLIT_X)) - 1).coerceAtLeast(0)
//        rightSize = ((rightLine / (SEQ_WIDTH + SEQ_SPLIT_X)) + 1).coerceAtMost(drawLifecycles.size)
    }

    override fun onDraw(g: Graphics) {
        drewLifecycle.clear()
        lastDrawY = 0
        lifecycleWidthTotal = 0
        if (bottomSize < outSize) {
            return
        }
//        if (leftSize > rightSize) {
//            return
//        }
        for (index in drawLifecycles.indices) {
            val drawElement = drawLifecycles[index]
            drawLifecycle(g, drawElement, index)
        }
        for (index in outSize until bottomSize) {
            val drawElement = selectedElements[index]
            drawOperation(g, drawElement, index)
            drawArrow(
                g, drawElement.className, if (index != outSize) {
                    drewLifecycle[selectedElements[index - 1].className]
                } else {
                    null
                }
            )
        }
    }

    var lastDrawY = 0
    var lifecycleWidthTotal = 0
    private fun drawLifecycle(g: Graphics, lifecycle: SeqLifecycle, index: Int) {
        val drawStartX = movedXTotal + (SEQ_SPLIT_X) * index + SequenceDiagramPanel.DRAW_START_X + lifecycleWidthTotal
        val drawStartY = SequenceDiagramPanel.DRAW_START_Y
        val stringWidth = StringMetrics.getStringWidth(lifecycle.element.className, g.font, g)
        lifecycleWidthTotal += stringWidth
        val lifecycleWidth = (stringWidth + OPERATION_PADDING_HORIZONTAL * 2).coerceAtLeast(SEQ_WIDTH)
        val lifecycleHeight = stringHeight + OPERATION_PADDING_VERTICAL * 2
        drawRect(g, drawStartX, drawStartY, lifecycleWidth, lifecycleHeight)
        lifecycle.drawX = drawStartX
        lifecycle.drawY = drawStartY
        lifecycle.width = lifecycleWidth
        lifecycle.height = lifecycleHeight
        g.drawLine(lifecycle.getMiddleX(), lifecycle.drawY + lifecycle.height, lifecycle.getMiddleX(), mParent.height)
        g.drawString(lifecycle.element.className, lifecycle.drawX + OPERATION_PADDING_HORIZONTAL, lifecycle.drawY + OPERATION_PADDING_VERTICAL + stringHeight)
        drewLifecycle[lifecycle.element.className] = lifecycle
    }

    private fun drawOperation(g: Graphics, drawOperation: SequenceDiagramElement, index: Int) {
        val className = drawOperation.className
        val text = drawOperation.operation
        val currentLifecycle = drewLifecycle[className] ?: return
        val operationHeight = stringHeight + OPERATION_PADDING_VERTICAL * 2 + OPERATION_SPLIT_Y
        val drawStartY = SEQ_HEIGHT + OPERATION_SPLIT_Y + movedYTotal + operationHeight * index + SequenceDiagramPanel.DRAW_START_Y
        val drawStartX = currentLifecycle.getMiddleX()
        val width = (StringMetrics.getStringWidth(text, g.font, g) + OPERATION_PADDING_HORIZONTAL * 2).coerceAtLeast(OPERATION_WIDTH)
        drawRect(g, drawStartX, drawStartY, width, stringHeight + OPERATION_PADDING_VERTICAL * 2)
        g.drawString(text, drawStartX + OPERATION_PADDING_HORIZONTAL, drawStartY + OPERATION_PADDING_VERTICAL + stringHeight)
        lastDrawY = drawStartY

        val timeStr = drawOperation.timeStr + " " + drawOperation.pid+ " " + drawOperation.tid
        drawRect(g, 0, drawStartY, StringMetrics.getStringWidth(timeStr, g.font, g), stringHeight + OPERATION_PADDING_VERTICAL * 2 + OPERATION_SPLIT_Y)
        g.drawString(
            timeStr,
            0,
            drawStartY + OPERATION_PADDING_VERTICAL + stringHeight
        )
    }

    private fun drawArrow(g: Graphics, className: String, lastLifecycle: SeqLifecycle?) {
        if (lastLifecycle != null) {
            if (lastLifecycle.element.className != className) {
                g.drawLine(
                    lastLifecycle.getMiddleX() + OPERATION_WIDTH - 20,
                    lastDrawY - OPERATION_SPLIT_Y,
                    lastLifecycle.getMiddleX() + OPERATION_WIDTH - 20,
                    lastDrawY
                )
                val currentLifecycle = drewLifecycle[className] ?: return
                g.drawLine(
                    lastLifecycle.getMiddleX() + OPERATION_WIDTH - 20,
                    lastDrawY,
                    currentLifecycle.getMiddleX(),
                    lastDrawY
                )
            } else {
                g.drawLine(
                    lastLifecycle.getMiddleX() + OPERATION_WIDTH - 20,
                    lastDrawY - OPERATION_SPLIT_Y,
                    lastLifecycle.getMiddleX() + OPERATION_WIDTH - 20,
                    lastDrawY
                )
            }
        }
    }

    private fun drawRect(g: Graphics, startX: Int, startY: Int, width: Int, height: Int) {
        if (startX < 0) {
            if (-startX >= width) {
                return
            }
            val newWidth = width + startX
            g.drawRect(0, startY, newWidth, height)
            val oldColor = g.color
            g.color = JBColor(Color.yellow, Gray._80);
            g.fillRect(1, startY + 1, newWidth - 1, height - 1)
            g.color = oldColor
        } else if (startX > mParent.width) {
            return
        } else {
            if ((startX + width) > mParent.width) {
                val newWidth = mParent.width - startX
                g.drawRect(startX, startY, newWidth, height)
                val oldColor = g.color
                g.color = JBColor(Color.yellow, Gray._80);
                g.fillRect(startX + 1, startY + 1, newWidth - 1, height - 1)
                g.color = oldColor
            } else {
                g.drawRect(startX, startY, width, height)
                val oldColor = g.color
                g.color = JBColor(Color.yellow, Gray._80);
                g.fillRect(startX + 1, startY + 1, width - 1, height - 1)
                g.color = oldColor
            }

        }

    }

    override fun mouseWheelMoved(it: MouseWheelEvent) {
        val isScrollEvent = JBScrollPane.isScrollEvent(it)
        val isFromSeqPanel = it.source is SequenceDiagramPanel
        if (isScrollEvent && isFromSeqPanel) {

        }
    }

    override fun mouseDragged(e: MouseEvent?) {
        if (lastClickX != null && lastClickY != null && e != null) {
            val currentX = e.x
            val currentY = e.y
            val moveY = currentY - lastClickY!!
            val moveX = currentX - lastClickX!!
            movedYTotal = (movedYTotal + moveY).coerceAtMost(0)
            movedXTotal = (movedXTotal + moveX).coerceAtMost(0)
            drawXStart = (drawXStart + moveX).coerceAtMost(SequenceDiagramPanel.DRAW_START_X)
            drawYStart = (drawYStart + moveY).coerceAtMost(SequenceDiagramPanel.DRAW_START_Y)
            mParent.repaint()
            lastClickX = currentX
            lastClickY = currentY
        }
    }

    override fun mouseMoved(e: MouseEvent?) {
    }

    override fun mousePressed(e: MouseEvent?) {
        lastClickX = e?.x
        lastClickY = e?.y
    }

    override fun mouseReleased(e: MouseEvent?) {
        lastClickX = null
        lastClickY = null
    }

    override fun mouseExited(e: MouseEvent?) {
        lastClickX = null
        lastClickY = null
    }

    override fun setElement(data: List<SequenceDiagramElement>) {
        this.elements = data
        this.selectedElements = data
        val map = data.distinct().map {
            return@map SeqLifecycle(it)
        }
        this.lifecycleList = map
        this.drawLifecycles = map
        mParent.repaint()
    }

    override fun getSelectedElement(): List<SequenceDiagramElement> {
        return selectedElements
    }

    override fun getDrawLifecycles(): List<SeqLifecycle> {
        return drawLifecycles
    }

    override fun setDrawLifecycles(data: List<String>) {
        val list = arrayListOf<SeqLifecycle>()
        data.forEach { className ->
            list.add(lifecycleList.first {
                className == it.element.className
            })
        }
        drawLifecycles = list
        val selectedElement = arrayListOf<SequenceDiagramElement>()
        elements.forEach { element ->
            if (data.any { it == element.className }) {
                selectedElement.add(element)
            }
        }
        selectedElements = selectedElement
    }
}