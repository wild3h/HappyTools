package com.lixiang.car.decodecohttp.toolswindow;

import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.wm.ToolWindow;
import com.intellij.uiDesigner.core.GridConstraints;
import com.lixiang.car.decodecohttp.decode.UnZipFormator;
import com.lixiang.car.decodecohttp.decodecohttp.util.NotifyUtil;
import com.lixiang.car.decodecohttp.decodecohttp.util.StringsKt;
import com.lixiang.car.decodecohttp.decodecohttp.util.ViewUtilsKt;
import com.lixiang.car.decodecohttp.util.CodeManager;

import javax.swing.*;
import java.util.concurrent.atomic.AtomicReference;

public class DCToolWindows {
    private JEditorPane editerText;
    private JButton btnDecode;
    private JPanel contentPanel;
    private JPanel resContainer;
    private JScrollPane resScroll;
    private JButton openInFile;
    private JButton btnClear;
    private JButton format;
    Editor resText;

    public DCToolWindows(Project project, ToolWindow toolWindow) {
        AtomicReference<String> res = new AtomicReference<>("");
        btnDecode.addActionListener(e -> {
            if (editerText.getText().isEmpty()) {
                NotifyUtil.INSTANCE.notifyMessage("请先输入数据");
                return;
            }
            res.set(UnZipFormator.format(StringsKt.legal(editerText.getText(),false)));
            ApplicationManager.getApplication().runWriteAction(() -> resText.getDocument().setText(res.get()));
        });

        openInFile.addActionListener(e -> {
            if (res.get().isEmpty()) {
                NotifyUtil.INSTANCE.notifyMessage("请先解析数据");
                return;
            }
            CodeManager.openCode(project, res.get());
        });

        btnClear.addActionListener(e -> {
            editerText.setText("");
            res.set("");
            ApplicationManager.getApplication().runWriteAction(() -> resText.getDocument().setText(""));
        });

        resText = ViewUtilsKt.createJsonContentEditor(null);
        resText.setBorder(BorderFactory.createEmptyBorder(0, 0, 2, 0));
        GridConstraints constraints = new GridConstraints();
        constraints.setFill(GridConstraints.FILL_BOTH);
        resContainer.add(resText.getComponent(), constraints);

        editerText.setName("editerText");
        format.addActionListener(e -> {
            String resContent = resText.getDocument().getText();
            if (!resContent.isEmpty()) {
                try {
                    String jsonFormat = StringsKt.jsonFormat(resContent);
                    res.set(jsonFormat);
                } catch (Exception ex) {
                    NotifyUtil.INSTANCE.notifyMessage("格式化失败，好像输入有问题~");
                    NotifyUtil.INSTANCE.notifyMessage(ex.getMessage());
                }
                ApplicationManager.getApplication().runWriteAction(() -> resText.getDocument().setText(res.get()));
            }
        });
    }

    public JPanel getContentPanel() {
        return contentPanel;
    }
}
