package com.lixiang.car.happytools.tools.data.compose

import com.lixiang.car.happytools.tools.util.jsonFormat

open class BaseComposeViewGroup : BaseComposeView() {
    val child: ArrayList<BaseComposeView> = arrayListOf()

    override fun toString(): String {
        return """
            {
                    "componentName": ${this.javaClass.simpleName.substring(2)},
                    "componentId": "*component_id"
                    ${

            if (modifier != null) {
                ",Modifier" + ": ${modifier!!.toJson()}"
            } else {
                ""
            }
        }
                    
                    ${
            if (params.getPrams().isNotEmpty()) {
                ",${
                    params.toJson().let {
                        it.substring(1, it.length - 1)
                    }
                }"
            } else {
                ""
            }
        }
                    ${
            if (child.isNotEmpty()) {
                ",child" + ": [${child.joinToString(",") { it.toString() }}]"
            } else {
                ""
            }
        }
                  }
        """.trimIndent().jsonFormat()
    }
}
