package com.lixiang.car.happytools.tools.entity

data class SeqLifecycle(
    val className: String,
    val drawX: Int,
    val drawYEnd: Int,
    val width: Int,
    val height: Int
) {
    fun getMiddleX(): Int {
        return drawX + width / 2
    }
}