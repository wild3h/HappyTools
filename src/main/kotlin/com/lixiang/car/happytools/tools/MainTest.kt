package com.lixiang.car.happytools.tools

import kotlinparse.KotlinLexer
import kotlinparse.KotlinParser
import kotlinparse.KotlinParserBaseListener
import org.antlr.v4.runtime.CharStreams
import org.antlr.v4.runtime.CommonTokenStream
import org.antlr.v4.runtime.ParserRuleContext
import org.antlr.v4.runtime.tree.ParseTreeWalker
import org.jetbrains.kotlin.cli.jvm.compiler.EnvironmentConfigFiles
import org.jetbrains.kotlin.cli.jvm.compiler.KotlinCoreEnvironment
import org.jetbrains.kotlin.com.intellij.openapi.util.Disposer
import org.jetbrains.kotlin.com.intellij.psi.PsiFile
import org.jetbrains.kotlin.com.intellij.psi.PsiManager
import org.jetbrains.kotlin.config.CompilerConfiguration
import org.jetbrains.kotlin.config.JVMConfigurationKeys
import org.jetbrains.kotlin.config.JvmTarget
import org.jetbrains.kotlin.psi.KtFile
import org.jetbrains.kotlin.psi.KtNamedFunction
import org.jetbrains.kotlin.psi.KtVisitorVoid

object MainTest {
    @JvmStatic
    fun main(args: Array<String>) {
        val code = """
    @Composable
    private fun DeleteButton() {
        Box(
            modifier = Modifier
                .width(250.dp)
                .height(140.dp)
                .clip(RoundedCornerShape(20.dp))
                .background(
                    color = LegoR.color(
                        id = if (mViewModel.selectItemCount != 0) {
                            "mine_del_btn_selected_bg"
                        } else {
                            "mine_del_btn_bg"
                        }
                    )
                )
                .description(R.string.mine_delete)
                .clickable(enabled = mViewModel.selectItemCount != 0) {
                    mViewModel.deleteSelected()
                    sendAccessibilityClickEvent()
                },
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = if (mViewModel.selectItemCount != 0) {
                    "删除"
                } else {
                    "删除"
                },
                fontSize = 36.sp,
                color = if (mViewModel.selectItemCount != 0) {
                    Color.White
                } else {
                    LegoR.color(id = "main_detail_text_color")
                },
                modifier = Modifier.alpha(
                    if (mViewModel.selectItemCount != 0) {
                        0.7f
                    } else {
                        0.3f
                    }
                )
            )
        }
    }
""".trimIndent()
//        parseCompose(code).forEach {
//            println(it)
//            println("_________")
//        }

        // Get the syntax tree of the compiled code.
//        val ktPsiFile = getKtPsiFile(code)
//        val ast: PsiFile? = file.psiFile
//        ast?.acceptChildren(object : KtVisitorVoid() {
//            override fun visitNamedFunction(function: KtNamedFunction) {
//                // Get the name of the function.
//                val functionName = function.name
//
//                // Get the parameters of the function.
//                val parameters = function.valueParameterList?.parameters
//
//                // Output the information.
//                println("Function: $functionName, Parameters: $parameters")
//            }
//        })
    }

    private fun parseCompose(code: String): List<String> {
        val charStream = CharStreams.fromString(code)
        val lexer = KotlinLexer(charStream)
        val tokenStream = CommonTokenStream(lexer)
        val parser = KotlinParser(tokenStream)

        val listener = object : KotlinParserBaseListener() {
            private val argsList = mutableListOf<String>()
            override fun enterValueArgument(ctx: KotlinParser.ValueArgumentContext?) {
                super.enterValueArgument(ctx)
                val args = ctx?.simpleIdentifier()?.text
                val argument = ctx?.expression()?.text
                if (args != null && argument != null) {
                    argsList.add("enterValueArgument")
                    argsList.add(args)
                    argsList.add(argument)
                    argsList.add("enterValueArgument over")
                }
            }

            override fun exitValueArgument(ctx: KotlinParser.ValueArgumentContext?) {
                super.exitValueArgument(ctx)
                val args = ctx?.simpleIdentifier()?.text
                val argument = ctx?.expression()?.text
                if (args != null && argument != null) {
                    argsList.add("exitValueArgument")
                    argsList.add(args)
                    argsList.add(argument)
                    argsList.add("exitValueArgument over")
                }
            }

            override fun enterEveryRule(ctx: ParserRuleContext?) {
                super.enterEveryRule(ctx)
//                println(ctx?.javaClass?.simpleName)
//                println(ctx?.text)
//                println("__________")
            }

            override fun enterBlock(ctx: KotlinParser.BlockContext?) {
                super.enterBlock(ctx)
//                println(ctx?.text)
//                println("__________")
            }

            override fun enterLambdaLiteral(ctx: KotlinParser.LambdaLiteralContext?) {
                super.enterLambdaLiteral(ctx)
                val text = ctx?.text
                //去掉text的前后大括号和最前面的空格
                val trimText = text?.substring(1, text.length - 1)?.trimStart()
                //如果trimText是以Text开头或者Box开头，打印这个trimText
                if (trimText?.startsWith("Text") == true || trimText?.startsWith("Box") == true) {
                    argsList.add(trimText)
                }
            }

            override fun enterPrimaryExpression(ctx: KotlinParser.PrimaryExpressionContext?) {
                super.enterPrimaryExpression(ctx)
                val text = ctx?.text
                //如果text是以Text开头或者Box开头，打印这个text
                if (text?.startsWith("Text") == true || text?.startsWith("Box") == true) {
                    argsList.add(text)
                }
            }

            override fun enterSimpleIdentifier(ctx: KotlinParser.SimpleIdentifierContext?) {
                super.enterSimpleIdentifier(ctx)
//                println(ctx?.text)
//                println("__________")
            }

            fun getArgs(): List<String> {
                return argsList.toList()
            }
        }
        ParseTreeWalker.DEFAULT.walk(listener, parser.kotlinFile())
        return listener.getArgs()
    }
}