package com.lixiang.car.happytools.tools.data.compose

class CPLazyVerticalGrid : BaseComposeViewGroup() {

    override operator fun set(key: String, value: Any) {
        when (key) {
            "columns" -> {
                if (value.toString().contains("Fixed")) {
                    //value的样例GridCells.Fixed(3) 获取Fixed后面的数字
                    val num = value.toString().replace("GridCells.Fixed(", "").replace(")", "")
                    //判断num是否是int类型的String字符串
                    if (num.matches(Regex("[0-9]+"))) {
                        params[key] = num.toInt()
                    } else {
                        params[key] = value
                    }
                } else if (value.toString().contains("Adaptive")) {
                    //value的样例GridCells.Adaptive(minSize = 100.dp) 获取Adaptive后面的数字
                    val num = value.toString().replace("GridCells.Adaptive(minSize = ", "").replace(".dp)", "")
                    //判断num是否是int或者float类型的String字符串
                    if (num.matches(Regex("[0-9]+")) || num.matches(Regex("[0-9]+.[0-9]+"))) {
                        params[key] = num.toFloat()
                    } else {
                        params[key] = value
                    }
                }
            }

            else -> params[key] = value
        }
    }
}