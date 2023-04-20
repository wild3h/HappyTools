package com.lixiang.car.happytools.tools.compose

object ComposeTransform {
    fun parseComposeFunction(str: String): ComposeFunction {
        val parts = str.split("=(")
        val name = parts[0]
        val parameters = mutableListOf<ComposeParameter>()
        val modifier = mutableListOf<ComposeModifier>()
        for (i in 1 until parts.size) {
            var parameterStr = parts[i].substringBefore(')')
            val parameterParts = parameterStr.split("=")
            val parameterName = parameterParts[0]
            val parameterValue = parameterParts[1]
            parameters.add(ComposeParameter(parameterName, parameterValue))
            if (i == parts.size - 1) {
                parameterStr = str.substringAfter('(').substringBeforeLast(')')
            }
            val modifierParts = parameterStr.split(".")
            val modifierName = modifierParts[0]
            val modifierParameters = mutableListOf<ComposeParameter>()
            for (j in 1 until modifierParts.size) {
                val modifierParameterStr = modifierParts[j]
                val modifierParameterParts = modifierParameterStr.split("=")
                val modifierParameterName = modifierParameterParts[0]
                val modifierParameterValue = modifierParameterParts[1]
                modifierParameters.add(ComposeParameter(modifierParameterName, modifierParameterValue))
            }
            modifier.add(ComposeModifier(modifierName, modifierParameters))
        }
        return ComposeFunction(name, parameters, modifier)
    }
}