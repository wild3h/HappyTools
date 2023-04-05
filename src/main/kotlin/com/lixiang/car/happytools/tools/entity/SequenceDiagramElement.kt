package com.lixiang.car.happytools.tools.entity

data class SequenceDiagramElement(
    val time: Long = 0,
    var className: String,
    var operation: String
){
    fun isActivity(): Boolean {
        return className.contains("Activity",ignoreCase = true)
    }

    infix fun inLifecycle(list:List<String>): Boolean {
        return list.contains(this.className)
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other == null || javaClass != other.javaClass) return false
        val person = other as SequenceDiagramElement
        return className == person.className
    }

    override fun hashCode(): Int {
        var result = className.hashCode()
        return result
    }
}