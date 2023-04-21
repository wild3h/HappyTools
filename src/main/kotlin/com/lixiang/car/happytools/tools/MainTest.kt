package com.lixiang.car.happytools.tools

import com.lixiang.car.happytools.tools.compose.ComposeTransform
import kotlinparse.KotlinLexer
import kotlinparse.KotlinParser
import kotlinparse.KotlinParserBaseListener
import org.antlr.v4.runtime.CharStreams
import org.antlr.v4.runtime.CommonTokenStream
import org.antlr.v4.runtime.ParserRuleContext
import org.antlr.v4.runtime.tree.ParseTreeWalker

object MainTest {
    @JvmStatic
    fun main(args: Array<String>) {
        val code = """
    @Composable
    private fun CancelButton() {
        Image(
            bitmap = LegoR.drawableAsImageBitmap(id = "mine_btn_cancel"),
            contentDescription = resources.getString(R.string.mine_detail_cancel),
            modifier = Modifier
                .width(140.dp)
                .height(140.dp)
                .clickable {
                    mViewModel.switchEditMode(false)
                    mViewModel.selectItemCount = 0
                    sendAccessibilityClickEvent()
                }
        )
    }
""".trimIndent()
        parseCompose(code).forEach {
            println(it)
            println("_________")
        }
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
//                println(ctx?.simpleIdentifier()?.text)
//                val argument = ctx?.expression()?.text
//                println(argument)
//                println("__________")
            }

            override fun enterEveryRule(ctx: ParserRuleContext?) {
                super.enterEveryRule(ctx)
//                println(ctx?.javaClass?.simpleName)
//                println(ctx?.text)
//                println("__________")
            }

            override fun enterPrimaryExpression(ctx: KotlinParser.PrimaryExpressionContext?) {
                super.enterPrimaryExpression(ctx)
                println(ctx?.callableReference()?.text)
                println(ctx?.functionLiteral()?.text)
                println(ctx?.objectLiteral()?.text)
                println(ctx?.collectionLiteral()?.text)
                println(ctx?.thisExpression()?.text)
                println(ctx?.superExpression()?.text)
                println(ctx?.ifExpression()?.text)
                println("__________")
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