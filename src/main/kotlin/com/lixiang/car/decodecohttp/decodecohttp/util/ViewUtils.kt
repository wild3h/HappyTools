package com.lixiang.car.decodecohttp.decodecohttp.util

import com.intellij.json.JsonFileType
import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.editor.Document
import com.intellij.openapi.editor.Editor
import com.intellij.openapi.editor.EditorFactory
import com.intellij.openapi.editor.event.DocumentEvent
import com.intellij.openapi.editor.event.DocumentListener
import com.intellij.openapi.fileTypes.FileType
import com.intellij.openapi.util.NlsContexts.RadioButton
import com.intellij.ui.components.RadioButton
import wu.seal.jsontokotlin.model.ConfigManager
import wu.seal.jsontokotlin.model.PropertyTypeStrategy
import java.awt.Component
import java.awt.Container
import java.awt.Dimension
import java.util.Stack
import javax.swing.JComponent
import javax.swing.JPopupMenu
import javax.swing.JRadioButton

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

operator fun JComponent.plusAssign(component: JComponent) {
    this.add(component)
}

infix fun JComponent.add(component: JComponent) {
    this.add(component)
}

fun JComponent.add(vararg components: JComponent) {
    components.forEach {
        this.add(it)
    }
}

fun createJsonContentEditor(listener: DocumentListener? = null): Editor {
    val editorFactory = EditorFactory.getInstance()
    val document = editorFactory.createDocument("").apply {
        setReadOnly(false)
        if (listener != null) {
            addDocumentListener(listener)
        }
    }

    val editor = editorFactory.createEditor(document, null, JsonFileType.INSTANCE, false)

    editor.component.apply {
        isEnabled = true
        preferredSize = Dimension(500, 480)
        autoscrolls = true
    }

    val contentComponent = editor.contentComponent
    contentComponent.isFocusable = true

    return editor
}

fun createContentEditor(fileType: FileType, listener: DocumentListener? = null): Editor {
    val editorFactory = EditorFactory.getInstance()
    val document = editorFactory.createDocument("").apply {
        setReadOnly(false)
        if (listener != null) {
            addDocumentListener(listener)
        }
    }

    val editor = editorFactory.createEditor(document, null, fileType, false)

    editor.component.apply {
        isEnabled = true
        preferredSize = Dimension(500, 480)
        autoscrolls = true
    }

    val contentComponent = editor.contentComponent
    contentComponent.isFocusable = true

    return editor
}

fun Document.setTextWrite(text: String) {
    ApplicationManager.getApplication().runWriteAction {
        setText(text)
    }
}

fun jSimpleButtonGroup(vararg child: ButtonData): List<JRadioButton> {
    child.forEach { me ->
        val bro = child.filter { me != it }
        me.apply {
            button.addChangeListener {
                val othersIsFalse = bro.filter { !it.button.isSelected }.size == bro.size
                if (othersIsFalse) {
                    println("${me.button.hashCode()} others is false")
                    if (!button.isSelected) {
                        println("${me.button.hashCode()} me button isSelected")
                        button.isSelected = true
                    }
                    return@addChangeListener
                }
                bro.forEach {
                    if (it.button.isSelected){
                        it.button.isSelected = false
                    }
                }
                onChange(button.isSelected)
            }
            button.isSelected = initVal
        }
    }
    return child.map {
        it.button
    }
}

inline fun JRadioButton.with(initVal: Boolean, noinline onChange: (selected: Boolean) -> Unit): ButtonData {
    return ButtonData(this, initVal, onChange)
}

data class ButtonData(val button: JRadioButton, val initVal: Boolean, val onChange: (selected: Boolean) -> Unit)