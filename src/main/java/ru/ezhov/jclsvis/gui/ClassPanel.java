package ru.ezhov.jclsvis.gui;

import clsvis.Utils;
import clsvis.model.Class_;
import ru.ezhov.jclsvis.gui.utils.MouseMoveWindowListener;

import javax.swing.*;
import java.awt.*;

public class ClassPanel extends JPanel {
    private Class_ class_;
    private JLabel label = new JLabel();
    private boolean selected = false;

    public ClassPanel(Class_ class_, int width, int height) {
        setBorder(BorderFactory.createLineBorder(Color.GRAY));
        setLayout(new BorderLayout());
        this.class_ = class_;
        String originalTypeName = class_.getOriginalTypeName();
        label.setText(originalTypeName);
        label.setHorizontalAlignment(SwingConstants.CENTER);
        add(label, BorderLayout.CENTER);
        label.setToolTipText(class_.toString());

        label.setBackground(Color.decode("#" + Utils.colorAsRRGGBB(class_.kind.colorNum)));
        label.setOpaque(true);

        setSize(width, height);
        setPreferredSize(new Dimension(width, height));

        MouseMoveWindowListener mouseMoveWindowListener = new MouseMoveWindowListener(this);
        label.addMouseMotionListener(mouseMoveWindowListener);
        label.addMouseListener(mouseMoveWindowListener);
    }

    public Class_ getClass_() {
        return class_;
    }

    public boolean isSelected() {
        return selected;
    }

    public void setSelected(boolean selected) {
        this.selected = selected;
    }
}
