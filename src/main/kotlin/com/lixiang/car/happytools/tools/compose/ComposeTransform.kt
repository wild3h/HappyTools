package com.lixiang.car.happytools.tools.compose

import com.intellij.openapi.project.ProjectManager
import com.intellij.openapi.project.impl.ProjectImpl
import com.lixiang.car.happytools.tools.util.FileUtils
import org.jetbrains.kotlin.cli.common.environment.setIdeaIoUseFallback
import org.jetbrains.kotlin.cli.jvm.compiler.EnvironmentConfigFiles
import org.jetbrains.kotlin.cli.jvm.compiler.KotlinCoreEnvironment.Companion.createForProduction
import org.jetbrains.kotlin.com.intellij.lang.impl.PsiBuilderImpl
import org.jetbrains.kotlin.com.intellij.openapi.project.Project
import org.jetbrains.kotlin.com.intellij.psi.PsiElement
import org.jetbrains.kotlin.com.intellij.psi.PsiFileFactory
import org.jetbrains.kotlin.config.CompilerConfiguration
import org.jetbrains.kotlin.idea.KotlinLanguage
import org.jetbrains.kotlin.parsing.KotlinParserDefinition
import org.jetbrains.kotlin.psi.KtCallExpression
import org.jetbrains.kotlin.psi.KtClass
import org.jetbrains.kotlin.psi.KtFile
import org.jetbrains.kotlin.psi.KtFunctionType
import org.jetbrains.kotlin.psi.KtNamedFunction
import org.jetbrains.kotlin.psi.KtParameter
import org.jetbrains.kotlin.psi.KtProperty
import org.jetbrains.kotlin.psi.KtValueArgument
import org.jetbrains.kotlin.psi.KtValueArgumentList
import org.jetbrains.kotlin.psi.KtVisitorVoid
import java.io.File
import java.io.FileInputStream
import java.io.IOException


object ComposeTransform {
    fun parseKotlinToPsi(code: String): KtFile? {
        // look https://github.com/maldinixiang/ktvisitor-kastree.git
        try {
            val environment = createForProduction({}, CompilerConfiguration(), EnvironmentConfigFiles.JVM_CONFIG_FILES)
            val file = PsiFileFactory.getInstance(environment.project).createFileFromText(FileUtils.defaultFileFolder() + "Test.kt", KotlinLanguage.INSTANCE, code) as KtFile
            setIdeaIoUseFallback()
            val project = environment.project

            val builder = PsiBuilderImpl(project,KotlinParserDefinition(),KotlinParserDefinition().createLexer(project), code)
            builder.setDebugMode(true)

            val file = KotlinParserDefinition().createFile(builder)
            builder.eof()

            val errorCount = builder.errorsCount
            if (errorCount != 0) {
                builder.getStatisticsInfo()?.entries?.forEach {
                    println(it)
                }
                println("Number of syntax errors found: $errorCount\n")
                for (error in builder.javaFileCharSequence.languageDialect.getSyntaxErrors(builder.originalText, errorCount)) {
                    println("Line ${error.line}: ${error.description}")
                }
            } else {
                file.acceptChildren(object : MyVisitor() {})
            }

            file.acceptChildren(object : KtVisitorVoid() {

                //                override fun visitElement(element: PsiElement) {
//                    super.visitElement(element)
//                    println("${element.javaClass.simpleName}: ${element.text}")
//                }
//
                override fun visitCallExpression(expression: KtCallExpression) {
                    super.visitCallExpression(expression)
                    println(expression.text)
                }

                override fun visitParameter(parameter: KtParameter) {
                    super.visitParameter(parameter)
                    println(parameter.text)
                }
                override fun visitNamedFunction(function: KtNamedFunction) {
                    // Get the name of the function.
                    val functionName = function.name

                    // Get the parameters of the function.
                    val parameters = function.valueParameterList?.parameters

                    // Output the information.
                    println("Function: $functionName, Parameters: $parameters")
                }
            })
        } catch (e: IOException) {
            e.printStackTrace()
        }
        return null
    }
}