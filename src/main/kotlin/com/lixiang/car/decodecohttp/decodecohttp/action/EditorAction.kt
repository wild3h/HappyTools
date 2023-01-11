package com.lixiang.car.decodecohttp.decodecohttp.action

import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.actionSystem.CommonDataKeys
import com.intellij.openapi.editor.Editor
import com.intellij.openapi.util.TextRange
import com.intellij.openapi.wm.ToolWindowManager
import com.lixiang.car.decodecohttp.decodecohttp.toolswindow.DCToolsWindowFactory
import com.lixiang.car.decodecohttp.decodecohttp.util.SelectionMode
import com.lixiang.car.decodecohttp.decodecohttp.util.ViewUtils.findComponentByName
import javax.swing.JEditorPane

class EditorAction : AutoSelectAction(true) {
    override val selectionMode: SelectionMode = SelectionMode.INCLUSIVE
    override fun onActionPerformed(event: AnActionEvent, editor: Editor, selectionRange: TextRange) {
        super.onActionPerformed(event, editor, selectionRange)
        val project = editor.project ?: CommonDataKeys.PROJECT.getData(event.dataContext)
        val markupModel = editor.markupModel
        val selectionModel = editor.selectionModel
        val isColumnSelectionMode = editor.caretModel.caretCount > 1

        val text: String = if (selectionModel.hasSelection(true) && isColumnSelectionMode) {
            selectionModel.getSelectedText(true) ?: return
        } else {
            editor.document.getText(selectionRange)
        }
        if (project != null) {
            val toolWindow = ToolWindowManager.getInstance(project).getToolWindow(DCToolsWindowFactory.ID)
            if (toolWindow?.isActive == false){
                toolWindow.show()
            }
            toolWindow?.contentManager?.contents?.forEach {
                val editorText = it.component.findComponentByName<JEditorPane>("editerText")
                editorText?.text = editorText?.text + text
            }
        }
    }
}