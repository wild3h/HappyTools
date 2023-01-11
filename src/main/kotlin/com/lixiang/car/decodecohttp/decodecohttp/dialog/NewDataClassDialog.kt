package com.lixiang.car.decodecohttp.decodecohttp.dialog

import com.lixiang.car.decodecohttp.decodecohttp.util.jsonFormat
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
import com.intellij.ui.DocumentAdapter
import com.intellij.ui.components.RadioButton
import com.lixiang.car.decodecohttp.decodecohttp.SerialInterceptor
import com.lixiang.car.decodecohttp.decodecohttp.config.KtFileConfigManager
import com.lixiang.car.decodecohttp.decodecohttp.util.*
import wu.seal.jsontokotlin.interceptor.IKotlinClassInterceptor
import wu.seal.jsontokotlin.interceptor.InterceptorManager
import wu.seal.jsontokotlin.model.ConfigManager
import wu.seal.jsontokotlin.model.DefaultValueStrategy
import wu.seal.jsontokotlin.model.PropertyTypeStrategy
import wu.seal.jsontokotlin.model.classscodestruct.KotlinClass
import wu.seal.jsontokotlin.ui.*
import wu.seal.jsontokotlin.utils.*
import java.awt.Dimension
import java.awt.Toolkit
import java.awt.datatransfer.DataFlavor
import java.net.URL
import javax.swing.*
import javax.swing.text.JTextComponent

private val jsonInputDialogValidator = JsonInputDialogValidator()

class NewDataClassDialog(private val directory: String, private val project: Project, private val psiDirectory: PsiDirectory, private val okListener: (className: String, kotlinText: String, classes: List<KotlinClass>?) -> Unit) :
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
    private var multiClass: List<KotlinClass>? = null
    override fun getInputString(): String = jsonContentEditor.document.text.trim()

    override fun createNorthPanel(): JComponent {
        return jHorizontalLinearLayout {
            jButton("format", {
                try {
                    jsonContentEditor.document.setTextWrite(inputString.jsonFormat())
                } catch (e: Exception) {
                    e.printStackTrace()
                    dataClassContentEditor.document.setTextWrite(e.message.toString())
                }
            }) {
                preferredSize = Dimension(50, 40)
            }
        }
    }

    override fun createCenterPanel(): JComponent {
        jsonContentEditor = createJsonContentEditor()
        dataClassContentEditor = createKotlinContentEditor()
        jsonInputDialogValidator.jsonInputEditor = jsonContentEditor

        myField = createTextFieldComponent()

        return jBorderLayout {
            val viewGroup = JHorizontalLinearLayout()
            putCenterFill(viewGroup)
            viewGroup add jsonContentEditor.component
            viewGroup add JVerticalLinearLayout().apply {
                this add RadioButton("var   ").apply {
                    addChangeListener {
                        ConfigManager.isPropertiesVar = isSelected
                        this@NewDataClassDialog.revalidate()
                    }
                    isSelected = ConfigManager.isPropertiesVar
                }
                this add jLine()
                jSimpleButtonGroup(
                        RadioButton("Nullable  ")
                                .with(ConfigManager.propertyTypeStrategy == PropertyTypeStrategy.Nullable) {
                                    ConfigManager.propertyTypeStrategy = PropertyTypeStrategy.Nullable
                                    this@NewDataClassDialog.revalidate()
                                },
                        RadioButton("Non-Nullable  ")
                                .with(ConfigManager.propertyTypeStrategy == PropertyTypeStrategy.NotNullable) {
                                    ConfigManager.propertyTypeStrategy = PropertyTypeStrategy.NotNullable
                                    this@NewDataClassDialog.revalidate()
                                },
                        RadioButton("Auto Nullable  ")
                                .with(ConfigManager.propertyTypeStrategy == PropertyTypeStrategy.AutoDeterMineNullableOrNot) {
                                    ConfigManager.propertyTypeStrategy = PropertyTypeStrategy.AutoDeterMineNullableOrNot
                                    this@NewDataClassDialog.revalidate()
                                }

                ).let {
                    it.forEach {
                        this@apply add it
                    }
                }
                this add jLine()
                jSimpleButtonGroup(
                        RadioButton("Not Init  ")
                                .with(ConfigManager.defaultValueStrategy == DefaultValueStrategy.None) {
                                    ConfigManager.defaultValueStrategy = DefaultValueStrategy.None
                                    this@NewDataClassDialog.revalidate()
                                },
                        RadioButton("Init With Non-Null  ")
                                .with(ConfigManager.defaultValueStrategy == DefaultValueStrategy.AvoidNull) {
                                    ConfigManager.defaultValueStrategy = DefaultValueStrategy.AvoidNull
                                    this@NewDataClassDialog.revalidate()
                                },
                        RadioButton("Init With Null  ")
                                .with(ConfigManager.defaultValueStrategy == DefaultValueStrategy.AllowNull) {
                                    ConfigManager.defaultValueStrategy = DefaultValueStrategy.AllowNull
                                    this@NewDataClassDialog.revalidate()
                                }

                ).let {
                    it.forEach {
                        this@apply add it
                    }
                }
                this add jLine()
                this add RadioButton("Serializable").apply {
                    addChangeListener {
                        if (isSelected) {
                            ConfigManager.parenClassTemplate = "java.io.Serializable"
                        } else {
                            ConfigManager.parenClassTemplate = ""
                        }
                        this@NewDataClassDialog.revalidate()
                    }
                    isSelected = ConfigManager.parenClassTemplate == "java.io.Serializable"
                }
                this add jLine()
                this add RadioButton("Multi File  ").apply {
                    addChangeListener {
                        KtFileConfigManager.enableMultiFile = isSelected
                        this@NewDataClassDialog.revalidate()
                    }
                    isSelected = KtFileConfigManager.enableMultiFile
                }
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
            preferredSize = Dimension(500, 480)
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
                    jsonContentEditor.document.setTextWrite(transferable.getTransferData(DataFlavor.stringFlavor).toString())
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
                        jsonContentEditor.document.setTextWrite(urlContent.replace("\r\n", "\n"))
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
                    jsonContentEditor.document.setTextWrite(content.replace("\r\n", "\n"))
                }
            }
        }
    }

    private fun revalidate() {
        if (inputString.isEmpty()) {
            return
        }
        ConfigManager.isInnerClassModel = false
        val kt = if (KtFileConfigManager.enableMultiFile) {
            multiFileJson2kt(myField.text, inputString)
        } else {
            singleFileJson2Kt(myField.text, inputString)
        }
        dataClassContentEditor.document.setTextWrite(kt)
        ClassImportDeclarationWriter.insertImportClassCode(project, dataClassContentEditor.document)
        okAction.isEnabled = jsonInputDialogValidator.checkInput(myField.text)
    }

    private fun singleFileJson2Kt(classsName: String, jsonString: String): String {
        val dataClassAfterApplyInterceptor = onIntercept(classsName, jsonString)
        val fileNamesWithoutSuffix = currentDirExistsFileNamesWithoutKTSuffix(psiDirectory)
        val existsKotlinFileNames = IgnoreCaseStringSet().also { it.addAll(fileNamesWithoutSuffix) }
        val splitClasses = dataClassAfterApplyInterceptor.resolveNameConflicts(existsKotlinFileNames).getAllModifiableClassesRecursivelyIncludeSelf().run {
            if (!jsonString.isJSONSchema()) distinctByPropertiesAndSimilarClassName() else this
        }
        return buildString {
            if (directory.isNotEmpty()) {
                append(directory)
                append("\n\n")
            }
            splitClasses.forEachIndexed { index, splitDataClass ->
                val importClassDeclaration = ClassImportDeclaration.getImportClassDeclaration()
                if (index == 0) {
                    if (importClassDeclaration.isNotBlank()) {
                        append(importClassDeclaration)
                        append("\n\n")
                    }
                }
                append(splitDataClass.getOnlyCurrentCode())
                append("\n\n")
            }
        }
    }

    private fun multiFileJson2kt(classsName: String, jsonString: String): String {
        val dataClassAfterApplyInterceptor = onIntercept(classsName, jsonString)
        val fileNamesWithoutSuffix = currentDirExistsFileNamesWithoutKTSuffix(psiDirectory)
        val existsKotlinFileNames = IgnoreCaseStringSet().also { it.addAll(fileNamesWithoutSuffix) }
        val splitClasses = dataClassAfterApplyInterceptor.resolveNameConflicts(existsKotlinFileNames).getAllModifiableClassesRecursivelyIncludeSelf().run {
            if (!jsonString.isJSONSchema()) distinctByPropertiesAndSimilarClassName() else this
        }
        multiClass = splitClasses
        return buildString {
            splitClasses.forEachIndexed { index, splitDataClass ->
                if (directory.isNotEmpty()) {
                    append(directory)
                    append("\n\n")
                }
                val importClassDeclaration = ClassImportDeclaration.getImportClassDeclaration()
                if (importClassDeclaration.isNotBlank()) {
                    append(importClassDeclaration)
                    append("\n\n")
                }
                append(splitDataClass.getOnlyCurrentCode())
                if (index != splitClasses.size - 1) {
                    append("\n")
                    append(FileUtils.MULTI_FILE_SPLIT)
                    append("\n")
                }
            }
        }
    }

    private fun onIntercept(classsName: String, jsonString: String): KotlinClass {
        val kotlinClass = KotlinClassMaker(classsName, jsonString).makeKotlinClass()
        val interceptors = ArrayList<IKotlinClassInterceptor<KotlinClass>>()
        interceptors.addAll(InterceptorManager.getEnabledKotlinDataClassInterceptors())
        if (ConfigManager.parenClassTemplate == "java.io.Serializable") {
            interceptors.add(SerialInterceptor())
        }
        return kotlinClass.applyInterceptors(interceptors)
    }

    private fun currentDirExistsFileNamesWithoutKTSuffix(directory: PsiDirectory): List<String> {
        val kotlinFileSuffix = ".kt"
        return directory.files.filter { it.name.endsWith(kotlinFileSuffix) }
                .map { it.name.dropLast(kotlinFileSuffix.length) }
    }

    override fun createTextFieldComponent(): JTextComponent {
        return super.createTextFieldComponent().apply {
            document.addDocumentListener(object : DocumentAdapter() {
                override fun textChanged(event: javax.swing.event.DocumentEvent) {
                    this@NewDataClassDialog.revalidate()
                }
            })
        }
    }

    override fun doOKAction() {
        super.doOKAction()
        okListener.invoke(myField.text, dataClassContentEditor.document.text, this.multiClass)
    }
}
