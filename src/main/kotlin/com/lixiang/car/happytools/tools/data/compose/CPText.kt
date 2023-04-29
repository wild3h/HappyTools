package com.lixiang.car.happytools.tools.data.compose

class CPText:BaseComposeView()  {
    fun setText(text: String) {
        params["text"] = text
    }
}