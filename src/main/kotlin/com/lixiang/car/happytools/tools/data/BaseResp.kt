package com.lixiang.car.happytools.tools.data

data class BaseResp<T>(
    var code: Int,
    var `data`: Data<T>?,
    var message: String,
    var success: Boolean,
    var timestamp: Long
)

data class Data<T>(
    var list: List<T>,
    var pageNum: Int,
    var pageSize: Int,
    var total: Int
)