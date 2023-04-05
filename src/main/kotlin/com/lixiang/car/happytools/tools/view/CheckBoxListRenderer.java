package com.lixiang.car.happytools.tools.view;

import javax.swing.*;
import java.awt.*;

public class CheckBoxListRenderer extends DefaultListCellRenderer {
    private JCheckBox checkBox;

    public CheckBoxListRenderer() {
        checkBox = new JCheckBox();
    }

    @Override
    public Component getListCellRendererComponent(
            JList list, Object value, int index,
            boolean isSelected, boolean cellHasFocus) {
        checkBox.setSelected(isSelected);
        checkBox.setText(value.toString());
        return checkBox;
    }
}