package com.lixiang.car.decodecohttp.decodecohttp.action

import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.actionSystem.CommonDataKeys
import com.intellij.openapi.editor.Editor
import com.intellij.openapi.util.TextRange
import com.intellij.openapi.wm.ToolWindowManager
import com.intellij.terminal.JBTerminalWidget
import com.intellij.ui.EditorTextField
import com.lixiang.car.decodecohttp.decodecohttp.toolswindow.DCToolsWindowFactory
import com.lixiang.car.decodecohttp.decodecohttp.util.SelectionMode
import com.lixiang.car.decodecohttp.decodecohttp.util.notifyText
import javax.swing.JEditorPane
import javax.swing.JPanel
import javax.swing.JScrollPane
import javax.swing.JSplitPane
import javax.swing.JViewport

class EditorAction : AutoSelectAction(true) {
    override val selectionMode: SelectionMode = SelectionMode.INCLUSIVE
    override fun onActionPerformed(event: AnActionEvent, editor: Editor, selectionRange: TextRange) {
        super.onActionPerformed(event, editor, selectionRange)
        event.dataContext
        val project = editor.project ?: CommonDataKeys.PROJECT.getData(event.dataContext)
        val markupModel = editor.markupModel
        val selectionModel = editor.selectionModel
        val isColumnSelectionMode = editor.caretModel.caretCount > 1

        val text: String
        if (selectionModel.hasSelection(true) && isColumnSelectionMode) {
            text = selectionModel.getSelectedText(true) ?: return
        } else {
            text = editor.document.getText(selectionRange)
        }
        val formatText = text.replace("*", "").replace(" ", "").replace("\n", "").replace("\t", "")
        notifyText("select--$formatText")
        if (project != null) {
            val toolWindow = ToolWindowManager.getInstance(project).getToolWindow(DCToolsWindowFactory.ID)
            if (toolWindow?.isActive==false){
                toolWindow.show()
            }
            toolWindow?.contentManager?.contents?.forEach {
                val component = it.component
                if (component is JPanel) {
                    val jSplitPane = component.getComponent(0)
                    if (jSplitPane is JSplitPane && jSplitPane.componentCount > 0) {
                        val jPanel = jSplitPane.leftComponent
                        if (jPanel is JPanel && jPanel.componentCount > 0) {
                            val jScrollPane = jPanel.getComponent(0)
                            if (jScrollPane is JScrollPane && jScrollPane.componentCount > 0) {
                                val jViewport = jScrollPane.getComponent(0)
                                if (jViewport is JViewport) {
                                    val jEditorPane = jViewport.getComponent(0)
                                    if (jEditorPane is JEditorPane) {
                                        jEditorPane.text = jEditorPane.text + formatText
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}