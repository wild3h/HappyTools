package com.lixiang.car.happytools.tools.view

import org.jdesktop.swingx.JXDatePicker
import java.util.*
import javax.swing.*
import javax.swing.event.ChangeEvent
import javax.swing.event.DocumentEvent
import javax.swing.event.DocumentListener
import javax.swing.text.NumberFormatter


class DateSelectorView : JXDatePicker() {
    var hourValue: Int = 0
    var minValue: Int = 0
    var secondValue: Int = 0

    init {
        monthView.selectionModel = DayWithHMSSelectionModel(this)

        val timePanel = JPanel()

        val hourModel = SpinnerNumberModel(0, 0, 23, 1)
        hourModel.addChangeListener {
            val value = hourModel.value
            if (value !is Int) {
                return@addChangeListener
            }
            if (date == null) {
                return@addChangeListener
            }
            hourValue = value
            val cal = Calendar.getInstance().apply { time = date }
            val yr = cal.get(Calendar.YEAR)
            val mon = cal.get(Calendar.MONTH)
            val dy = cal.get(Calendar.DAY_OF_MONTH)
            cal.set(yr, mon, dy, hourValue, minValue, secondValue)
            val newDate = cal.time
            date = newDate
        }
        val hourSpinner = JSpinner(hourModel)
        val hourEditor = hourSpinner.editor
        if (hourEditor is JSpinner.NumberEditor) {
            hourEditor.textField.isEnabled = false
        }
        timePanel.add(hourSpinner)

        val hourLabel = JLabel("小时")
        timePanel.add(hourLabel)

        val minuteModel = SpinnerNumberModel(0, 0, 59, 1)
        val minuteSpinner = JSpinner(minuteModel)
        val minuteEditor = minuteSpinner.editor
        if (minuteEditor is JSpinner.NumberEditor) {
            minuteEditor.textField.isEnabled = false
        }
        timePanel.add(minuteSpinner)
        minuteModel.addChangeListener {
            val value = minuteModel.value
            if (value !is Int) {
                return@addChangeListener
            }
            if (date == null) {
                return@addChangeListener
            }
            minValue = value
            val cal = Calendar.getInstance().apply { time = date }
            val yr = cal.get(Calendar.YEAR)
            val mon = cal.get(Calendar.MONTH)
            val dy = cal.get(Calendar.DAY_OF_MONTH)
            cal.set(yr, mon, dy, hourValue, minValue, secondValue)
            val newDate = cal.time
            date = newDate
        }
        val minuteLabel = JLabel("分钟")
        timePanel.add(minuteLabel)

        val secondModel = SpinnerNumberModel(0, 0, 59, 1)
        val secondSpinner = JSpinner(secondModel)
        val secondEditor = secondSpinner.editor
        if (secondEditor is JSpinner.NumberEditor) {
            secondEditor.textField.isEnabled = false
        }
        timePanel.add(secondSpinner)
        secondModel.addChangeListener {
            val value = secondModel.value
            if (value !is Int) {
                return@addChangeListener
            }
            if (date == null) {
                return@addChangeListener
            }
            secondValue = value
            val cal = Calendar.getInstance().apply { time = date }
            val yr = cal.get(Calendar.YEAR)
            val mon = cal.get(Calendar.MONTH)
            val dy = cal.get(Calendar.DAY_OF_MONTH)
            cal.set(yr, mon, dy, hourValue, minValue, secondValue)
            val newDate = cal.time
            date = newDate
        }
        val secondLabel = JLabel("秒")
        timePanel.add(secondLabel)
        linkPanel = timePanel
    }

}