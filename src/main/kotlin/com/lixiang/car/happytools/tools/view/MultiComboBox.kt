package com.lixiang.car.happytools.tools.view

import com.intellij.ui.JBColor
import com.intellij.ui.components.JBScrollPane
import java.awt.BorderLayout
import java.awt.Dimension
import java.awt.GridLayout
import java.awt.event.ActionEvent
import java.awt.event.ActionListener
import java.util.*
import javax.swing.*
import javax.swing.plaf.basic.BasicArrowButton

/**
 * 下拉复选框组件
 *
 */
class MultiComboBox(val onConfirm:(Array<String>)->Unit) : JComponent(), ActionListener {
    private var popup: MultiPopup? = null
    private var editor: JTextField? = null
    protected var arrowButton: JButton? = null
    private var values: Array<String> = arrayOf("全选")
    init {
        initComponent()
    }

    private fun initComponent() {
        this.layout = BorderLayout()
        popup = MultiPopup(values)
        editor = JTextField()
        editor!!.background = JBColor.WHITE
        editor!!.isEditable = false
        editor!!.preferredSize = Dimension(140, 22)
        editor!!.addActionListener(this)
        arrowButton = createArrowButton()
        arrowButton!!.addActionListener(this)
        editor?.let { add(it, BorderLayout.WEST) }
        arrowButton?.let { add(it, BorderLayout.CENTER) }
    }

    val selectedValues: Array<String>
        //获取选中的数据
        get() = popup!!.selectedValues
    fun setValues(values: Array<String>) {
        this.values = values
        initComponent()
    }
    //设置需要选中的值
    fun setSelectValues(selectValues: Array<String>) {
        popup!!.setSelectValues(selectValues)
        setText(selectValues)
    }

    private fun setText(values: Array<String>) {
        if (values.isNotEmpty()) {
            var value = values.contentToString()
            value = value.replace("[", "")
            value = value.replace("]", "")
            editor!!.text = value
        } else {
            editor!!.text = ""
        }
    }

    override fun actionPerformed(arg0: ActionEvent) {
        // TODO Auto-generated method stub
        if (!popup!!.isVisible) {
            popup!!.show(this, 0, height)
        }
    }

    protected fun createArrowButton(): JButton {
        val button: JButton = BasicArrowButton(
            BasicArrowButton.SOUTH,
            UIManager.getColor("ComboBox.buttonBackground"),
            UIManager.getColor("ComboBox.buttonShadow"),
            UIManager.getColor("ComboBox.buttonDarkShadow"),
            UIManager.getColor("ComboBox.buttonHighlight")
        )
        button.name = "ComboBox.arrowButton"
        return button
    }

    //内部类MultiPopup
    inner class MultiPopup(private val values: Array<String>) : JPopupMenu(), ActionListener {
        private val checkBoxList: MutableList<JCheckBox> = ArrayList()
        private var commitButton: JButton? = null
        private var cancelButton: JButton? = null

        init {
            initComponent()
        }

        private fun initComponent() {
            val checkboxPane = JPanel()
            val buttonPane = JPanel()
            this.layout = BorderLayout()
            for (v in values) {
                val temp = JCheckBox(v.toString())
                checkBoxList.add(temp)
            }
            if (checkBoxList[0].text == "全选") {
                checkBoxList[0].addItemListener {
                    if (checkBoxList[0].isSelected) {
                        for (i in 1 until checkBoxList.size) {
                            if (!checkBoxList[i].isSelected) {
                                checkBoxList[i].isSelected = true
                            }
                        }
                    } else {
                        for (i in 1 until checkBoxList.size) {
                            if (checkBoxList[i].isSelected) {
                                checkBoxList[i].isSelected = false
                            }
                        }
                    }
                }
            }
            checkboxPane.layout = GridLayout(checkBoxList.size, 1, 3, 3)
            for (box in checkBoxList) {
                checkboxPane.add(box)
            }
            commitButton = JButton("确定")
            commitButton!!.addActionListener(this)
            cancelButton = JButton("取消")
            cancelButton!!.addActionListener(this)
            buttonPane.add(commitButton)
            buttonPane.add(cancelButton)
            val scrollPaneLayout = JBScrollPane(checkboxPane)
            scrollPaneLayout.preferredSize = Dimension(140,500)
            scrollPaneLayout.horizontalScrollBarPolicy = ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED
            scrollPaneLayout.verticalScrollBarPolicy = ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED
            this.add(scrollPaneLayout, BorderLayout.CENTER)
            this.add(buttonPane, BorderLayout.SOUTH)
        }

        fun setSelectValues(values: Array<String>) {
            if (values.isNotEmpty()) {
                for (i in values.indices) {
                    for (j in checkBoxList.indices) {
                        if (values[i] == checkBoxList[j].text) {
                            checkBoxList[j].isSelected = true
                        }
                    }
                }
                setText(this.selectedValues)
            }
        }

        val selectedValues: Array<String>
            get() {
                val selectedValues: MutableList<String> = ArrayList()
                if (checkBoxList[0].text == "全选") {
                    if (checkBoxList[0].isSelected) {
                        for (i in 1 until checkBoxList.size) {
                            selectedValues.add(values[i])
                        }
                    } else {
                        for (i in 1 until checkBoxList.size) {
                            if (checkBoxList[i].isSelected) {
                                selectedValues.add(values[i])
                            }
                        }
                    }
                } else {
                    for (i in checkBoxList.indices) {
                        if (checkBoxList[i].isSelected) {
                            selectedValues.add(values[i])
                        }
                    }
                }
                return selectedValues.toTypedArray<String>()
            }

        override fun actionPerformed(arg0: ActionEvent) {
            val source = arg0.source
            if (source is JButton) {
                if (source == commitButton) {
                    setText(this.selectedValues)
                    popup!!.isVisible = false
                    onConfirm.invoke(this.selectedValues)
                } else if (source == cancelButton) {
                    popup!!.isVisible = false
                }
            }
        }
    }
}