package com.lixiang.car.decodecohttp.decodecohttp.action

import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.actionSystem.CommonDataKeys
import com.intellij.openapi.editor.Editor
import com.intellij.openapi.util.TextRange
import com.intellij.terminal.JBTerminalWidget
import com.lixiang.car.decodecohttp.decodecohttp.util.SelectionMode

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
        val starts: IntArray
        val ends: IntArray
        if (selectionModel.hasSelection(true) && isColumnSelectionMode) {
            starts = selectionModel.blockSelectionStarts
            ends = selectionModel.blockSelectionEnds
            text = selectionModel.getSelectedText(true) ?: return
        } else {
            starts = intArrayOf(selectionRange.startOffset)
            ends = intArrayOf(selectionRange.endOffset)
            text = editor.document.getText(selectionRange)
        }
        println(text)
    }
}