package com.lixiang.car.happytools.tools.compose

import kotlinparse.KotlinLexer
import kotlinparse.KotlinParser
import org.antlr.v4.runtime.CharStreams
import org.antlr.v4.runtime.CommonTokenStream

object ComposeTransform {
    fun parseComposeFunction(composeCode: String): KotlinParser.KotlinFileContext {

        val charStream = CharStreams.fromString(composeCode)
        val lexer = KotlinLexer(charStream)
        val tokenStream = CommonTokenStream(lexer)
        val parser = KotlinParser(tokenStream)


        return parser.kotlinFile()
    }
}