package com.lixiang.car.happytools.tools.data.compose

abstract class BaseJson {
    abstract val params:Any

    open fun toJson():String{
        return """
            {
                ${this.params.javaClass.simpleName}:$params
            }
        """.trimIndent()
    }
}