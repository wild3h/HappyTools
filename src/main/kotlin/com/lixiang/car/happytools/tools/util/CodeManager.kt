package com.lixiang.car.happytools.tools.util

import com.intellij.openapi.project.Project
import com.lixiang.car.happytools.tools.util.FileUtils.defaultFileFolder
import com.lixiang.car.happytools.tools.util.FileUtils.openFileEditorAndSaveState
import com.lixiang.car.happytools.tools.util.FileUtils.saveFile
import com.lixiang.car.happytools.tools.util.NotifyUtil.notifyMessage
import java.io.File
import java.util.*

object CodeManager {
    @Deprecated("")
    fun openCode(project: Project?, text: String?) {
        openCode(project, text, "json-res-" + UUID.randomUUID() + ".json")
    }

    fun openCode(project: Project?, text: String?, fileName: String) {
        val filePath = defaultFileFolder() + fileName
        val file = File(filePath)
        if (file.exists()) {
            file.delete()
        }
        try {
            saveFile(file, text)
            openFileEditorAndSaveState(file, project)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun onlyOpenCode(project: Project?, fileName: String, defaultContent: String?=null): Boolean {
        val filePath = defaultFileFolder() + File.separator + "decodeCohttp" + File.separator + fileName
        val file = File(filePath)
        try {
            if (!file.exists()) {
                saveFile(file, defaultContent)
            }
            openFileEditorAndSaveState(file, project)
        } catch (e: Exception) {
            e.printStackTrace()
            return false
        }
        return true
    }

    fun saveAndOpen(project: Project?, path: String, text: String?, fileName: String): Boolean {
        val filePath = path + File.separator + fileName
        val file = File(filePath)
        if (file.exists()) {
            return false
        }
        try {
            saveFile(file, text)
            openFileEditorAndSaveState(file, project)
        } catch (e: Exception) {
            e.printStackTrace()
            notifyMessage(e.cause.toString())
        }
        return true
    }
}