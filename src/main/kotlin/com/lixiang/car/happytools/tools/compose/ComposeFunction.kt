package com.lixiang.car.happytools.tools.compose

data class ComposeFunction(
    val name: String,
    val parameters: List<ComposeParameter>,
    val modifier: List<ComposeModifier>
)

data class ComposeParameter(
    val name: String,
    val value: String
)

data class ComposeModifier(
    val name: String,
    val parameters: List<ComposeParameter>
)