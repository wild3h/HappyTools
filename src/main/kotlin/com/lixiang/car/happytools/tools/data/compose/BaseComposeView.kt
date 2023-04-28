package com.lixiang.car.happytools.tools.data.compose

import com.lixiang.car.happytools.tools.util.jsonFormat

open class BaseComposeView : BaseJson() {
    var modifier: Modifier? = null
    override val params = JsonHashMap()

    override fun toString(): String {
        return """
            {
                    "componentName": ${this.javaClass.simpleName},
                    "componentId": "*component_id"
                    ${

            if (modifier != null) {
                ",Modifier" + ": ${modifier!!.toJson()}"
            } else {
                ""
            }
        }
                    
                    ${
            if (params.params.isNotEmpty()) {
                ",${
                    params.toJson().let {
                        it.substring(1, it.length - 1)
                    }
                }"
            } else {
                ""
            }
        }
                  }
        """.trimIndent().jsonFormat()
    }

    companion object {
        @JvmStatic
        fun main(args: Array<String>) {
            val baseComposeView = BaseComposeView()
            baseComposeView.modifier = Modifier().putWidth(1).putHeight(2).putPadding(Padding(1,2,3,4))
            baseComposeView.params["width"] = 1
            println(baseComposeView.toString())
        }
    }
}
