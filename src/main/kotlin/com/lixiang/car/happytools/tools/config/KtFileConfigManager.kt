package com.lixiang.car.happytools.tools.config

import com.intellij.ide.util.PropertiesComponent

object KtFileConfigManager {
    private const val KEY_MULTI_FILE_CONFIG = "key_multi_file_config"
    var enableMultiFile: Boolean
        get() = PropertiesComponent.getInstance().getBoolean(KEY_MULTI_FILE_CONFIG, false)
        set(value) {
            PropertiesComponent.getInstance().setValue(KEY_MULTI_FILE_CONFIG, value, false)
        }
}