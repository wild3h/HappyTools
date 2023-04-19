package com.lixiang.car.happytools.tools.util

import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.fileEditor.FileDocumentManager
import com.intellij.openapi.fileEditor.FileEditorManager
import com.intellij.openapi.fileEditor.OpenFileDescriptor
import com.intellij.openapi.project.Project
import com.intellij.openapi.vfs.LocalFileSystem
import com.intellij.openapi.vfs.VirtualFile
import com.intellij.openapi.vfs.newvfs.RefreshQueue
import com.intellij.psi.PsiDirectory
import com.intellij.psi.PsiFileFactory
import com.lixiang.car.happytools.tools.config.KtFileConfigManager
import wu.seal.jsontokotlin.filetype.KotlinFileType
import wu.seal.jsontokotlin.model.classscodestruct.KotlinClass
import wu.seal.jsontokotlin.utils.executeCouldRollBackAction
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.io.IOException
import java.lang.Boolean
import java.util.*
import kotlin.ByteArray
import kotlin.String
import kotlin.Throwable

object FileUtils {
    const val MULTI_FILE_SPLIT = "-------------------请勿修改分隔符--------------------"


    fun saveFile(project: Project, psiFileFactory: PsiFileFactory, directory: PsiDirectory, fileName: String, kotlinFileContent: String, classes: List<KotlinClass>?) {
        executeCouldRollBackAction(project) {
            if (KtFileConfigManager.enableMultiFile) {
                val str = StringTokenizer(kotlinFileContent, MULTI_FILE_SPLIT)
                var i = 0
                while (str.hasMoreTokens()) {
                    val nextToken = str.nextToken()
                    if (nextToken.isEmpty()){
                        continue
                    }
                    val fileNameMaybe = if (i == 0) {
                        fileName
                    } else {
                        classes!![i].name
                    }
                    i++
                    val file = psiFileFactory.createFileFromText("${fileNameMaybe.trim('`')}.kt", KotlinFileType(), nextToken)
                    directory.add(file)
                }
            } else {
                val file = psiFileFactory.createFileFromText("${fileName.trim('`')}.kt", KotlinFileType(), kotlinFileContent)
                directory.add(file)
            }

        }
    }

    fun saveFile(path: String?, body: String?) {
        saveFile(File(path), body)
    }

    fun saveFile(file: File, body: String?) {
        try {
            if (body == null) {
                return
            }
            if (!file.parentFile.exists()) {
                file.parentFile.mkdirs()
            }
            if (!file.exists()) {
                file.createNewFile()
            }
            val fileOutputStream = FileOutputStream(file, Boolean.FALSE)
            fileOutputStream.write(body.toByteArray(charset("UTF-8")))
            fileOutputStream.close()
        } catch (io: IOException) {
            println("保存文件错误")
            io.printStackTrace()
        }
    }

    fun getFileBody(filePath: String?): String? {
        return getFileBody(File(filePath))
    }

    fun getFileBody(file: File): String? {
        var all = ""
        if (file.exists()) {
            val filelength = file.length()
            val filecontent = ByteArray(filelength.toInt())
            try {
                val `in` = FileInputStream(file)
                `in`.read(filecontent)
                `in`.close()
                all = java.lang.String(filecontent, "UTF-8").toString()
            } catch (i: IOException) {
                i.printStackTrace()
            }
        }
        return all
    }


    fun trim(str: String): String? {
        return str.replace("[\\s|\\t]".toRegex(), "")
    }


    fun openFileEditor(file: File?, project: Project?) {
        ApplicationManager.getApplication().invokeLater {
            val vf = LocalFileSystem.getInstance().refreshAndFindFileByIoFile(file!!)
            val descriptor = OpenFileDescriptor(project!!, vf!!)
            FileEditorManager.getInstance(project).openTextEditor(descriptor, false)
            RefreshQueue.getInstance().refresh(false, false, null, vf)
        }
    }


    fun openFileEditorAndSaveState(file: File?, project: Project?) {
        ApplicationManager.getApplication().invokeLater {
            val vf = LocalFileSystem.getInstance().refreshAndFindFileByIoFile(file!!)
            val descriptor = OpenFileDescriptor(project!!, vf!!)
            FileEditorManager.getInstance(project).openTextEditor(descriptor, false)
        }
    }

    fun saveEditDocument(file: VirtualFile?) {
        if (FileDocumentManager.getInstance().isFileModified(file!!)) {
            try {
                ApplicationManager.getApplication().invokeLaterOnWriteThread { ApplicationManager.getApplication().runWriteAction { FileDocumentManager.getInstance().saveDocument(FileDocumentManager.getInstance().getDocument(file)!!) } }
            } catch (ignore: Throwable) {
                ignore.printStackTrace()
            }
        }
    }

    fun separator(): String? {
        return if (File.separator == "\\") {
            "/"
        } else {
            ""
        }
    }

    fun defaultFileFolder(): String? {
        return System.getProperty("java.io.tmpdir") + File.separator + "HappyToolsTemple" + File.separator
    }
}