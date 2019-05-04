package ru.ezhov.jclsvis.gui;

import clsvis.model.Class_;

import javax.swing.*;
import java.awt.*;

public class ClassPanel extends JPanel {
    private Class_ class_;
    private JLabel label = new JLabel();

    public ClassPanel(Class_ class_) {
        setBorder(BorderFactory.createLineBorder(Color.GRAY));
        setLayout(new BorderLayout());
        this.class_ = class_;
        String originalTypeName = class_.getOriginalTypeName();
        label.setText(originalTypeName);
        label.setHorizontalAlignment(SwingConstants.CENTER);
        add(label, BorderLayout.CENTER);
        label.setToolTipText(class_.getOriginalTypeName());
        setSize(20, 20);
        setPreferredSize(new Dimension(20, 20));
    }

    public Class_ getClass_() {
        return class_;
    }
}
