package com.lixiang.car.decodecohttp.decodecohttp.util

import java.awt.Component
import java.awt.Container
import java.util.Stack
import javax.swing.JComponent

object ViewUtils {
    inline fun <reified T : Component?> Container.findComponentByName(name: String): T? {
        val result: T? = null
        val stack = Stack<Component>()
        stack.push(this)
        do {
            val component = stack.pop()
            if (component.name == name && component is T) {
                return component
            }
            if (component is Container) {
                for (i in component.components) {
                    stack.push(i)
                }
            }
        } while (stack.isNotEmpty())
        return result
    }
}

operator fun JComponent.plusAssign(component: JComponent){
    this.add(component)
}