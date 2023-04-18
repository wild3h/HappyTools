package com.lixiang.car.happytools.tools.view

import org.jdesktop.swingx.JXDatePicker
import java.util.*
import javax.swing.JLabel
import javax.swing.JPanel
import javax.swing.JSpinner
import javax.swing.SpinnerNumberModel


class DateSelectorView : JXDatePicker() {
    var hourValue: Int = 0
    var minValue: Int = 0
    var secondValue: Int = 0

    init {
        monthView.selectionModel = DayWithHMSSelectionModel()
        monthView
        val timePanel = JPanel()

        val hourModel = SpinnerNumberModel(0, 0, 23, 1)
        hourModel.addChangeListener {
            val value = hourModel.value
            if (value !is Int) {
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
        timePanel.add(hourSpinner)

        val hourLabel = JLabel("小时")
        timePanel.add(hourLabel)

        val minuteModel = SpinnerNumberModel(0, 0, 59, 1)
        val minuteSpinner = JSpinner(minuteModel)
        timePanel.add(minuteSpinner)
        minuteModel.addChangeListener {
            val value = minuteModel.value
            if (value !is Int) {
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
        timePanel.add(secondSpinner)
        secondModel.addChangeListener {
            val value = secondModel.value
            if (value !is Int) {
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

        popupMenuListeners
    }

    override fun getDate(): Date? {
        val preDate = super.getDate() ?: return super.getDate()
        val cal = Calendar.getInstance().apply { time = preDate }
        val yr = cal.get(Calendar.YEAR)
        val mon = cal.get(Calendar.MONTH)
        val dy = cal.get(Calendar.DAY_OF_MONTH)
        cal.set(yr, mon, dy, hourValue, minValue, secondValue)
        return cal.time
    }

}