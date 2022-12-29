package com.lixiang.car.decodecohttp.decodecohttp.action

import cn.yiiguxing.plugin.translate.util.isJSONSchema
import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.actionSystem.LangDataKeys
import com.intellij.openapi.actionSystem.PlatformDataKeys
import com.intellij.openapi.project.Project
import com.intellij.openapi.roots.ModuleRootManager
import com.intellij.openapi.ui.Messages
import com.intellij.psi.PsiDirectory
import com.intellij.psi.PsiFile
import com.intellij.psi.PsiFileFactory
import com.intellij.psi.PsiManager
import com.intellij.psi.impl.file.PsiDirectoryFactory
import com.lixiang.car.decodecohttp.decodecohttp.dialog.NewDataClassDialog

class NewClassAction : AnAction("Kotlin data class from JSON") {
    override fun actionPerformed(event: AnActionEvent) {
        try {
            val project = event.getData(PlatformDataKeys.PROJECT) ?: return

            val dataContext = event.dataContext
            val module = LangDataKeys.MODULE.getData(dataContext) ?: return

            val directory = when (val navigatable = LangDataKeys.NAVIGATABLE.getData(dataContext)) {
                is PsiDirectory -> navigatable
                is PsiFile -> navigatable.containingDirectory
                else -> {
                    val root = ModuleRootManager.getInstance(module)
                    root.sourceRoots
                        .asSequence()
                        .mapNotNull {
                            PsiManager.getInstance(project).findDirectory(it)
                        }.firstOrNull()
                }
            } ?: return

            val directoryFactory = PsiDirectoryFactory.getInstance(directory.project)
            val packageName = directoryFactory.getQualifiedName(directory, false)
            val psiFileFactory = PsiFileFactory.getInstance(project)
            val packageDeclare = if (packageName.isNotEmpty()) "package $packageName" else ""
            val inputDialog = NewDataClassDialog("",packageDeclare, event.project!!)
            inputDialog.show()
            val className = inputDialog.getClassName()
            val inputString = inputDialog.inputString.takeIf { it.isNotEmpty() } ?: return

        } catch (e: Throwable) {
            e.printStackTrace()
        }
    }
}