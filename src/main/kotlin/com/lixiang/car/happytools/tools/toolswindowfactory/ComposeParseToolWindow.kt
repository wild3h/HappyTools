package com.lixiang.car.happytools.tools.toolswindowfactory

import com.intellij.ide.highlighter.JavaFileType
import com.intellij.json.JsonFileType
import com.intellij.openapi.editor.event.DocumentListener
import com.intellij.openapi.project.Project
import com.lixiang.car.happytools.tools.compose.ComposeTransform
import com.lixiang.car.happytools.tools.util.add
import com.lixiang.car.happytools.tools.util.createContentEditor
import com.lixiang.car.happytools.tools.util.leftToLeft
import com.lixiang.car.happytools.tools.util.leftToRight
import org.jetbrains.kotlin.idea.KotlinFileType
import java.awt.Dimension
import javax.swing.JLabel
import javax.swing.Spring
import javax.swing.SpringLayout

class ComposeParseToolWindow : BaseToolWindow() {

    private val composeInputEditor by lazy {
        createContentEditor(JavaFileType.INSTANCE, listener = null)
    }

    private val legoResEditor by lazy {
        createContentEditor(JsonFileType.INSTANCE, listener = null)
    }

    override fun getDisplayName(): String {
        return "ComposeParse"
    }

    override fun initView(project: Project) {

        val springLayout = SpringLayout()
        rootView.layout = springLayout
        val composeComponent = composeInputEditor.component
        val legoComponent = legoResEditor.component
        val composeInputCons = springLayout.getConstraints(composeComponent)
        composeInputCons.x = Spring.constant(0)
        composeInputCons.y = Spring.constant(0)
        val toComposeLabel = JLabel(">>").apply {
            preferredSize = Dimension(100, 30)
        }
        this.getRootView().addComponentListener(object : java.awt.event.ComponentAdapter() {
            override fun componentResized(e: java.awt.event.ComponentEvent?) {
                super.componentResized(e)
                val windowWidth = this@ComposeParseToolWindow.getRootView().width
                val height = this@ComposeParseToolWindow.getRootView().height
                composeComponent.preferredSize = Dimension((windowWidth - 140) / 2, height - 40)
                legoComponent.preferredSize = Dimension((windowWidth - 140) / 2, height - 40)
            }
        })
        rootView.add(
            composeComponent,
            toComposeLabel,
            legoComponent
        )
        toComposeLabel.leftToRight(composeComponent, 20)
        legoComponent.leftToRight(toComposeLabel, 20)
        composeInputEditor.document.addDocumentListener(object :DocumentListener{
            override fun documentChanged(event: com.intellij.openapi.editor.event.DocumentEvent) {
                super.documentChanged(event)
                println("___________________________")
                ComposeTransform.parseKotlinToPsi(composeInputEditor.document.text).forEach {
                    println(it)
                }
            }
        })
    }


}