package com.lixiang.car.happytools.tools.data.compose

class CPParams(val name:String) : BaseJson() {
    override val params = JsonHashMap()
    operator fun set(key: String, value: Any) {
        params[key] = value
    }

    override fun toJson(): String {
        return """
            {
                "$name":${params.toJson()}
            }
        """.trimIndent()
    }
}