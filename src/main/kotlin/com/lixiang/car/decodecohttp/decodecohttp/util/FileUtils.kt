package com.lixiang.car.decodecohttp.decodecohttp.util

import com.intellij.openapi.project.Project
import com.intellij.psi.PsiDirectory
import com.intellij.psi.PsiFileFactory
import com.lixiang.car.decodecohttp.decodecohttp.config.KtFileConfigManager
import wu.seal.jsontokotlin.filetype.KotlinFileType
import wu.seal.jsontokotlin.model.classscodestruct.KotlinClass
import wu.seal.jsontokotlin.utils.executeCouldRollBackAction
import java.util.*

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
}