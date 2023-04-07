package com.lixiang.car.happytools.tools.entity

data class SeqLifecycle(
    val element: SequenceDiagramElement,
    var drawX: Int=0,
    var drawY: Int=0,
    var width: Int=0,
    var height: Int=0
) {
    fun getMiddleX(): Int {
        return drawX + width / 2
    }
}