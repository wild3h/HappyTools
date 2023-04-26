package com.lixiang.car.happytools.tools.compose

import kastree.ast.Node
import org.jetbrains.kotlin.cli.common.CLIConfigurationKeys
import org.jetbrains.kotlin.cli.common.messages.MessageRenderer
import org.jetbrains.kotlin.cli.common.messages.PrintingMessageCollector
import org.jetbrains.kotlin.cli.jvm.compiler.EnvironmentConfigFiles
import org.jetbrains.kotlin.cli.jvm.compiler.KotlinCoreEnvironment
import org.jetbrains.kotlin.com.intellij.openapi.project.Project
import org.jetbrains.kotlin.com.intellij.openapi.util.Disposer
import org.jetbrains.kotlin.config.CompilerConfiguration
import org.jetbrains.kotlin.config.JVMConfigurationKeys
import org.jetbrains.kotlin.config.JvmTarget
import org.jetbrains.kotlin.psi.KtFile


object ComposeTransform {

    private var classList: ArrayList<String> = arrayListOf()
    private var methodList: ArrayList<String> = arrayListOf()
    private var proj: Project

    init {
        val configuration = CompilerConfiguration()
        configuration.put(CLIConfigurationKeys.MESSAGE_COLLECTOR_KEY, PrintingMessageCollector(System.out, MessageRenderer.PLAIN_FULL_PATHS, true))
        configuration.put(JVMConfigurationKeys.JVM_TARGET, JvmTarget.JVM_1_8)
        proj = KotlinCoreEnvironment.createForProduction(Disposer.newDisposable(),
            configuration,
            EnvironmentConfigFiles.JVM_CONFIG_FILES).project

    }

    fun parseKotlinToPsi(code: String): ArrayList<String> {
        // look https://github.com/maldinixiang/ktvisitor-kastree.git
        // https://github.com/cretz/kastree
        val classParser = Parser()
        val classFile = classParser.parseFile(code)
        classFile.decls.forEach {
            when (it) {
                is Node.Decl.Structured -> {
                    getFunInfo(it)
                }
                is Node.Decl.Func -> {
                    it.name?.let { funName -> methodList.add(funName) }
                }

                else -> {}
            }
        }
        return methodList
    }

    private fun getFunInfo(structured: Node.Decl.Structured) {
        structured.members.forEach {
            when (it) {
                is Node.Decl.Structured -> {
                    getFunInfo(it)
                }
                is Node.Decl.Func -> {
                    println(it.name)
                    val body = it.body
                    if (body is Node.Decl.Func.Body.Block){
                        body.block.stmts.forEach {expr->
                            if (expr is Node.Stmt.Expr){
                                val call  = expr.expr
                                if (call is Node.Expr.Call){
                                    val callName = call.expr
                                    if (callName is Node.Expr.Name){
                                        println("控件类型${callName.name}")
                                    }
                                    val valueArgs = call.args
                                    valueArgs.forEach { valueArg->
                                        println("参数${valueArg.name}")
                                        val expr = valueArg.expr
                                        if (expr is Node.Expr.BinaryOp){
                                            println("数值${expr}")
                                        }
                                    }
                                }
                            }
                        }
                    }
                }

                else -> {}
            }
        }
    }
}