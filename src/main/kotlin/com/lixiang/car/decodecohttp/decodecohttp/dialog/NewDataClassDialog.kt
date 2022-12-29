package com.lixiang.car.decodecohttp.decodecohttp.dialog

import cn.yiiguxing.plugin.translate.util.jsonFormat
import com.intellij.ide.highlighter.JavaFileType
import com.intellij.json.JsonFileType
import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.application.runWriteAction
import com.intellij.openapi.editor.Editor
import com.intellij.openapi.editor.EditorFactory
import com.intellij.openapi.editor.event.DocumentEvent
import com.intellij.openapi.fileChooser.FileChooser
import com.intellij.openapi.fileChooser.FileChooserDescriptor
import com.intellij.openapi.progress.util.DispatchThreadProgressWindow
import com.intellij.openapi.project.Project
import com.intellij.openapi.ui.Messages
import com.intellij.psi.PsiDirectory
import com.intellij.psi.PsiFileFactory
import com.intellij.ui.components.RadioButton
import com.lixiang.car.decodecohttp.decodecohttp.util.*
import wu.seal.jsontokotlin.filetype.KotlinFileType
import wu.seal.jsontokotlin.interceptor.InterceptorManager
import wu.seal.jsontokotlin.model.ConfigManager
import wu.seal.jsontokotlin.ui.JsonInputDialogValidator
import wu.seal.jsontokotlin.ui.UrlInputValidator
import wu.seal.jsontokotlin.utils.ClassImportDeclaration
import wu.seal.jsontokotlin.utils.KotlinClassMaker
import java.awt.Dimension
import java.awt.Toolkit
import java.awt.datatransfer.DataFlavor
import java.net.URL
import java.util.*
import javax.swing.*
import javax.swing.event.ChangeEvent

private val jsonInputDialogValidator: JsonInputDialogValidator = JsonInputDialogValidator()

class NewDataClassDialog(classsName: String, private val directory: String, private val project: Project) :
    Messages.InputDialog(
        project,
        "please input JSON and className",
        "New Kotlin Data Class Code",
        null,
        "",
        jsonInputDialogValidator
    ) {
    private lateinit var jsonContentEditor: Editor
    private lateinit var dataClassContentEditor: Editor
    fun getClassName(): String {
        return if (exitCode == 0) {
            val name = myField.text.trim()
            name.let { if (it.first().isDigit() || it.contains('$')) "`$it`" else it }
        } else ""
    }

    override fun getInputString(): String = jsonContentEditor.document.text.trim()

    override fun createNorthPanel(): JComponent {
        return jLabel("")
    }

    override fun createCenterPanel(): JComponent {
        jsonContentEditor = createJsonContentEditor()
        dataClassContentEditor = createKotlinContentEditor()
        jsonInputDialogValidator.jsonInputEditor = jsonContentEditor

        myField = createTextFieldComponent()

        return jBorderLayout {
            val viewGroup = JHorizontalLinearLayout()
            putCenterFill(viewGroup)
            viewGroup += jsonContentEditor.component
            viewGroup += JVerticalLinearLayout().apply {
                this += RadioButton("var").apply {
                    addChangeListener {
                        ConfigManager.isPropertiesVar = isSelected
                        ConfigManager.isInnerClassModel = isSelected
                    }
                }
                this += jButton("format", {
                    jsonContentEditor.document.setText(inputString.jsonFormat())
                })
                this += jButton(">>", {
                    val kt = json2Kt(myField.text, inputString)
                    dataClassContentEditor.document.setText(kt)
                })
            }
            viewGroup += dataClassContentEditor.component
            bottomContainer {
                jVerticalLinearLayout {
                    fixedSpace(7)
                    jHorizontalLinearLayout {
                        jLabel("Class Name: ", 14f)
                        add(myField)
                    }
                    fixedSpace(3)
                }
            }
        }
    }

    private fun createJsonContentEditor(): Editor {
        val editorFactory = EditorFactory.getInstance()
        val document = editorFactory.createDocument("").apply {
            setReadOnly(false)
            addDocumentListener(object : com.intellij.openapi.editor.event.DocumentListener {
                override fun documentChanged(event: DocumentEvent) = revalidate()

                override fun beforeDocumentChange(event: DocumentEvent) = Unit
            })
        }

        val editor = editorFactory.createEditor(document, null, JsonFileType.INSTANCE, false)

        editor.component.apply {
            isEnabled = true
            preferredSize = Dimension(640, 480)
            autoscrolls = true
        }

        val contentComponent = editor.contentComponent
        contentComponent.isFocusable = true
        contentComponent.componentPopupMenu = JPopupMenu().apply {
            add(createPasteFromClipboardMenuItem())
            add(createRetrieveContentFromHttpURLMenuItem())
            add(createLoadFromLocalFileMenu())
        }

        return editor
    }

    private fun createKotlinContentEditor(): Editor {
        val editorFactory = EditorFactory.getInstance()
        val document = editorFactory.createDocument("").apply {
            setReadOnly(false)
        }

        val editor = editorFactory.createEditor(document, null, JavaFileType.INSTANCE, false)

        editor.component.apply {
            isEnabled = true
            preferredSize = Dimension(640, 480)
            autoscrolls = true
        }

        return editor
    }

    private fun createPasteFromClipboardMenuItem() = JMenuItem("Paste from clipboard").apply {
        addActionListener {
            val transferable = Toolkit.getDefaultToolkit().systemClipboard.getContents(null)
            if (transferable.isDataFlavorSupported(DataFlavor.stringFlavor)) {
                runWriteAction {
                    jsonContentEditor.document.setText(transferable.getTransferData(DataFlavor.stringFlavor).toString())
                }
            }
        }
    }

    private fun createRetrieveContentFromHttpURLMenuItem() = JMenuItem("Retrieve content from Http URL").apply {
        addActionListener {
            val url = Messages.showInputDialog("URL", "Retrieve content from Http URL", null, null, UrlInputValidator)
            val p = DispatchThreadProgressWindow(false, project)
            p.isIndeterminate = true
            p.setRunnable {
                try {
                    val urlContent = URL(url).readText()
                    runWriteAction {
                        jsonContentEditor.document.setText(urlContent.replace("\r\n", "\n"))
                    }
                } finally {
                    p.stop()
                }
            }
            p.start()
        }
    }

    private fun createLoadFromLocalFileMenu() = JMenuItem("Load from local file").apply {
        addActionListener {
            FileChooser.chooseFile(FileChooserDescriptor(true, false, false, false, false, false), null, null) { file ->
                val content = String(file.contentsToByteArray())
                ApplicationManager.getApplication().runWriteAction {
                    jsonContentEditor.document.setText(content.replace("\r\n", "\n"))
                }
            }
        }
    }

    private fun revalidate() {
        okAction.isEnabled = jsonInputDialogValidator.checkInput(myField.text)
    }

    private fun json2Kt(
        classsName: String,
        jsonString: String
    ): String {
        val kotlinClass = KotlinClassMaker(classsName, jsonString).makeKotlinClass()
        val dataClassAfterApplyInterceptor =
            kotlinClass.applyInterceptors(InterceptorManager.getEnabledKotlinDataClassInterceptors())
        val classCodeContent = dataClassAfterApplyInterceptor.getCode()
        val kotlinFileContent = buildString {
            if (directory.isNotEmpty()) {
                append(directory)
                append("\n\n")
            }
            val importClassDeclaration = ClassImportDeclaration.getImportClassDeclaration()
            if (importClassDeclaration.isNotBlank()) {
                append(importClassDeclaration)
                append("\n\n")
            }
            append(classCodeContent)
        }
        return kotlinFileContent
    }
}
