package com.lixiang.car.happytools.tools.data.compose

import com.lixiang.car.happytools.tools.util.toJsonStr

class Padding(start: Any? = null, top: Any? = null, end: Any? = null, bottom: Any? = null): BaseJson() {

    override val params = JsonHashMap()

    init {
        start?.let {
            params["start"] = start
        }
        top?.let {
            params["top"] = top
        }
        end?.let {
            params["end"] = end
        }
        bottom?.let {
            params["bottom"] = bottom
        }
    }

    fun put(pair: Pair<String, Any>): Padding {
        params[pair.first] = pair.second
        return this
    }

    fun putStart(start: Double): Padding {
        params["start"] = start
        return this
    }

    fun putTop(top: Double): Padding {
        params["top"] = top
        return this
    }

    fun putEnd(end: Double): Padding {
        params["end"] = end
        return this
    }

    fun putBottom(bottom: Double): Padding {
        params["bottom"] = bottom
        return this
    }

    fun putStart(start: Int): Padding {
        params["start"] = start
        return this
    }

    fun putTop(top: Int): Padding {
        params["top"] = top
        return this
    }

    fun putEnd(end: Int): Padding {
        params["right"] = end
        return this
    }

    fun putBottom(bottom: Int): Padding {
        params["bottom"] = bottom
        return this
    }


    override fun toJson(): String {
        return """
            ${params.toJson()}
        """.trimIndent()
    }

    companion object{
        @JvmStatic
        fun main(args: Array<String>){
            val padding = Padding(1,2,3,4)
            println(padding.toJson())
        }
    }
}