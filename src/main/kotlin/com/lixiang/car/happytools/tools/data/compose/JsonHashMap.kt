package com.lixiang.car.happytools.tools.data.compose

import com.lixiang.car.happytools.tools.util.toJsonStr

class JsonHashMap : BaseJson() {

    override val params = linkedMapOf<String, Any>()

    fun getPrams():LinkedHashMap<String, Any>{
        return params
    }
    override fun toJson(): String {
        val res = StringBuilder()
        val paramsSize = params.size
        var index = 0
        params.forEach { (any, u) ->
            res.append("\"$any\":" +
                if (u is String) {
                    "\"$u\""
                } else if ((u is Int) or (u is Long) or (u is Double) or (u is Float)) {
                    "$u"
                } else {
                    u.toJsonStr()
                })
            if (index < paramsSize - 1) {
                res.append(",")
            }
            index++
        }
        return """{${res}}"""
    }

    operator fun set(key: String, value: Any) {
        params[key] = value
    }
}
