package com.lixiang.car.happytools.tools.compose

object ComposeTransform {
    fun parseComposeFunction(composeCode: String): ComposeMethod {

        val regex = Regex("(\\w+)\\((.*)\\)")
        val matchResult = regex.find(composeCode)

        // 定义实体类
        val methodName = matchResult?.groups?.get(1)?.value
        val params = matchResult?.groups?.get(2)?.value?.split(",")?.map { it.trim() }

        // 返回结果
        return ComposeMethod(methodName ?: "", params ?: emptyList())
    }
}