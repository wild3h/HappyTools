package com.lixiang.car.happytools.tools.entity

import com.lixiang.car.happytools.tools.constants.DiagramConstants.MAX_CHAR_ONE_LINE
import com.lixiang.car.happytools.tools.util.formatToTime

data class SequenceDiagramElement(
    val time: Long = 0,
    val timeStr: String,
    val pid: String,
    val tid: String,
    var className: String,
    var operation: String,
    var lineString: String
) {
    private lateinit var formatOpt: String
    fun isActivity(): Boolean {
        return className.contains("Activity", ignoreCase = true)
    }

    infix fun inLifecycle(list: List<String>): Boolean {
        return list.contains(this.className)
    }

    fun getFormatOpt(): String {
        if (!::formatOpt.isInitialized) {
            val builder = StringBuilder()
            className.forEachIndexed { index, c ->
                builder.append(c)
                if (index % MAX_CHAR_ONE_LINE == MAX_CHAR_ONE_LINE - 1) {
                    builder.append("\n")
                }
            }
            formatOpt = builder.toString()
        }
        return formatOpt
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other == null || javaClass != other.javaClass) return false
        val person = other as SequenceDiagramElement
        return className == person.className
    }

    override fun hashCode(): Int {
        return className.hashCode()
    }

    override fun toString(): String {
        return lineString
    }
}