package com.lixiang.car.happytools.tools.data.log

data class LogConfigBeans(
    var key_word: List<String>,
    var log_type: String,
    var time_end_date: String,
    var time_start_date: String,
    var vin: String,
    var key_no_word: List<String>
)
