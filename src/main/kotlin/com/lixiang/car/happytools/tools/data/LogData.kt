package com.lixiang.car.happytools.tools.data;
data class LogData(
    var code: Int,
    var `data`: Data,
    var message: String,
    var success: Boolean,
    var timestamp: Long
)
data class Data(
    var list: List<Item>,
    var pageNum: Int,
    var pageSize: Int,
    var total: Int
)
data class Item(
    var collectTime: String,
    var domain: String,
    var downloadURL: String,
    var fileKey: String,
    var fileName: String,
    var fileSize: Int,
    var logLevel: Any?,
    var logType: String,
    var receiveTime: String,
    var sn: Any?,
    var vin: String
)