package com.lixiang.car.happytools.tools.data.compose

import java.util.*

class CPText : BaseComposeView() {
    fun setText(text: String) {
        params["text"] = text
    }

    override operator fun set(key: String, value: Any) {
        when (key) {
            "" -> {}
            "text" -> setText(value as String)
            "fontSize","letterSpacing","lineHeight" -> {
                if (value.toString().contains(".sp")) {
                    val value = value.toString().replace(".sp", "")
                    //判断value是否是int或者float类型的String字符串
                    if (value.matches(Regex("[0-9]+")) || value.matches(Regex("[0-9]+.[0-9]+"))) {
                        params[key] = value.toFloat()
                    } else {
                        params[key] = value
                    }
                } else {
                    params[key] = value
                }
            }
            "fontStyle","fontWeight","fontFamily","textDecoration","textAlign" -> {
                //key的第一个字母变成大写
                val upperKey = key.replaceFirst(key[0], key[0].uppercaseChar())
                if (value.toString().contains("$upperKey.")) {
                    params[key] = value.toString().replace("$upperKey.", "")
                } else {
                    params[key] = value
                }
            }
            "overflow" -> {
                if (value.toString().contains("TextOverflow.")) {
                    params[key] = value.toString().replace("TextOverflow.", "")
                } else {
                    params[key] = value
                }
            }
            else -> params[key] = value
        }
    }
}