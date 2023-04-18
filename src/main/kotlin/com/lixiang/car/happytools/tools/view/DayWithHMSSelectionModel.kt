package com.lixiang.car.happytools.tools.view

import org.jdesktop.swingx.calendar.DaySelectionModel
import java.util.*

class DayWithHMSSelectionModel:DaySelectionModel() {
    override fun getNormalizedDate(date: Date): Date {
        return date
    }
}