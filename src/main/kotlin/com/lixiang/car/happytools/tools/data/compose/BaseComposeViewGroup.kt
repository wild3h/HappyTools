package com.lixiang.car.happytools.tools.data.compose

import com.lixiang.car.happytools.tools.util.jsonFormat

open class BaseComposeViewGroup : BaseComposeView() {
    val child: List<BaseComposeView> = arrayListOf()
}
