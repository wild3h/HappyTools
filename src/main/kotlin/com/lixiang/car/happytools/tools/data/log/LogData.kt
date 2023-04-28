package com.lixiang.car.happytools.tools.data.log;

data class LogItem(
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