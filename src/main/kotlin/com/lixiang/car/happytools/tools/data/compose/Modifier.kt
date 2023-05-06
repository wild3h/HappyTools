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
        var key = "padding"
        var i = 0
        while (params.containsKey(key)) {
            i++
            key = "padding$i"
        }
        params[key] = padding
        return this
    }

    fun putFillMaxSize(value:Float): Modifier {
        params["fillMaxSize"] = value
        return this
    }

    fun putFillMaxWidth(value:Float): Modifier {
        params["fillMaxWidth"] = value
        return this
    }

    fun putFillMaxHeight(value:Float): Modifier {
        params["fillMaxHeight"] = value
        return this
    }

    fun putRequiredWidth(value:Int): Modifier {
        params["requiredWidth"] = value
        return this
    }

    fun putRequiredHeight(value:Int): Modifier {
        params["requiredHeight"] = value
        return this
    }

    fun putAlpha(value:Float): Modifier {
        params["alpha"] = value
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