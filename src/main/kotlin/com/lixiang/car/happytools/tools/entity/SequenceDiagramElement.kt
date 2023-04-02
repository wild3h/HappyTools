package com.lixiang.car.happytools.tools.entity

data class SequenceDiagramElement(
    val time: Long = 0,
    var className: String,
    var operation: String
){
    fun isActivity(): Boolean {
        return className.contains("Activity",ignoreCase = true)
    }
}