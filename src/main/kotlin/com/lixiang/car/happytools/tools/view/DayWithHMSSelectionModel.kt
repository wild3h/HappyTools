package com.lixiang.car.happytools.tools.view

import org.jdesktop.swingx.calendar.DaySelectionModel
import java.util.*

class DayWithHMSSelectionModel(val parentView: DateSelectorView) : DaySelectionModel() {
    override fun getNormalizedDate(date: Date): Date {
        return date
    }

    override fun getFirstSelectionDate(): Date? {
        val firstSelectionDate = super.getFirstSelectionDate() ?: return null
        val cal = Calendar.getInstance().apply { time = firstSelectionDate }
        val yr = cal.get(Calendar.YEAR)
        val mon = cal.get(Calendar.MONTH)
        val dy = cal.get(Calendar.DAY_OF_MONTH)
        cal.set(yr, mon, dy, parentView.hourValue, parentView.minValue, parentView.secondValue)
        return cal.time
    }
}