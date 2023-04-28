package com.lixiang.car.happytools.tools.data.compose

import com.lixiang.car.happytools.tools.util.toJsonStr

class Modifier: BaseJson()  {
    override val params = JsonHashMap()
    fun put(pair: Pair<String, Any>): Modifier {
        params[pair.first] = pair.second
        return this
    }

    fun putWidth(width: Int): Modifier {
        params["width"] = width
        return this
    }

    fun putHeight(height: Int): Modifier {
        params["height"] = height
        return this
    }

    fun putWidth(width: Double): Modifier {
        params["width"] = width
        return this
    }

    fun putHeight(height: Double): Modifier {
        params["height"] = height
        return this
    }

    fun putPadding(padding: Padding): Modifier {
        params["padding"] = padding
        return this
    }

    override fun toJson(): String {
        return """
            ${params.toJsonStr()}
        """.trimIndent()
    }

    companion object{
        @JvmStatic
        fun main(args: Array<String>) {
            val modifier = Modifier()
            modifier.putWidth(1)
            modifier.putHeight(2)
            modifier.putPadding(Padding(1,2,3,4))
            println(modifier.toJson())
        }
    }

}