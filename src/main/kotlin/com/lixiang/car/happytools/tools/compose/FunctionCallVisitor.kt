package com.lixiang.car.happytools.tools.compose;

import org.jetbrains.kotlin.com.intellij.psi.util.PsiTreeUtil
import org.jetbrains.kotlin.psi.KtCallExpression

class FunctionCallVisitor : org.jetbrains.kotlin.psi.KtTreeVisitorVoid() {

    private var functionName: String? = null
    private var arguments: List<String>? = null

    override fun visitCallExpression(expression: KtCallExpression) {
        super.visitCallExpression(expression)

        // 如果是函数调用表达式
        val calleeExpression = expression.calleeExpression
        if (calleeExpression != null) {
            val referenceExpression = PsiTreeUtil.getChildOfType(calleeExpression, org.jetbrains.kotlin.psi.KtNameReferenceExpression::class.java)
            if (referenceExpression != null) {
                functionName = referenceExpression.getReferencedName()
            }
        }

        // 获取参数列表
        val args = mutableListOf<String>()
        expression.valueArguments.forEach { arg ->
            if (arg.isNamed()) {
                arg.getArgumentName()?.let { argName ->
                    args.add("$argName = ${arg.getArgumentExpression()?.text}")
                }
            } else {
                args.add(arg.getArgumentExpression()?.text ?: "")
            }
        }
        arguments = args
    }

    fun getFunctionName(): String? {
        return functionName
    }

    fun getArguments(): List<String>? {
        return arguments
    }
}