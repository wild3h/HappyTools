package com.lixiang.car.decodecohttp.toolswindow;

import com.intellij.json.JsonFileType;
import com.intellij.openapi.editor.EditorFactory;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.wm.ToolWindow;
import com.intellij.ui.EditorTextField;
import com.intellij.uiDesigner.core.GridConstraints;
import com.lixiang.car.decodecohttp.decode.UnZipFormator;
import com.lixiang.car.decodecohttp.decodecohttp.util.NotifyUtil;
import com.lixiang.car.decodecohttp.util.CodeManager;

import javax.swing.*;
import javax.swing.event.DocumentListener;
import java.awt.event.InputMethodEvent;
import java.awt.event.InputMethodListener;
import java.util.concurrent.atomic.AtomicReference;

public class DCToolWindows {
    private JEditorPane editerText;
    private JButton btnDecode;
    private JPanel contentPanel;
    private JPanel resContainer;
    private JScrollPane resScroll;
    private JButton openInFile;
    private JButton btnClear;
    private EditorTextField resText;

    public DCToolWindows(Project project, ToolWindow toolWindow) {
        AtomicReference<String> res = new AtomicReference<>("");
        btnDecode.addActionListener(e -> {
            if (editerText.getText().isEmpty()) {
                NotifyUtil.INSTANCE.notifyMessage("请先输入数据");
                return;
            }
            res.set(UnZipFormator.format(editerText.getText()));
            resText.setText(res.get());
        });

        openInFile.addActionListener(e -> {
            if (res.get().isEmpty()) {
                NotifyUtil.INSTANCE.notifyMessage("请先解析数据");
                return;
            }
            CodeManager.openCode(project, res.get());
        });

        btnClear.addActionListener(e->{
            editerText.setText("");
            res.set("");
            resText.setText("");
        });

        resText = new EditorTextField(EditorFactory.getInstance().createDocument(""), project, JsonFileType.INSTANCE, true, false);
        resText.setBorder(BorderFactory.createEmptyBorder(0, 0, 2, 0));
        GridConstraints constraints = new GridConstraints();
        constraints.setFill(GridConstraints.FILL_BOTH);
        resContainer.add(resText, constraints);

        editerText.setName("editerText");
    }

    public JPanel getContentPanel() {
        return contentPanel;
    }
}
