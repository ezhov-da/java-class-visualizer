package ru.ezhov.jclsvis.gui;

import clsvis.model.Class_;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.ezhov.jclsvis.gui.utils.MouseMoveWindowListener;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

public class ClassBandlePanel extends JPanel {
    private static final Logger LOG = LoggerFactory.getLogger(ClassBandlePanel.class);

    private Collection<Class_> classes;
    private List<ClassPanel> classPanels = new ArrayList<>();

    public ClassBandlePanel(Collection<Class_> classes, ClassPanelLocationStorage classPanelLocationStorage, int width, int height) {
        setLayout(null);
        setBorder(BorderFactory.createLineBorder(Color.BLACK));
        LOG.trace("Передано на построение {} класса(ов)", classes.size());
        this.classes = classes;
        if (classes.isEmpty()) return;
        for (Class_ class_ : classes) {
            classPanels.add(new ClassPanel(class_, width, height));
        }
        int columnAndRows = (int) Math.ceil(Math.sqrt(classPanels.size()));
        LOG.trace("Посчитанное количество строк и столбцов {}", columnAndRows);
        int indent = 15;
        int sizeWidthClean = 0;
        int sizeHeightClean = 0;
        Iterator<ClassPanel> iterator = classPanels.iterator();
        ClassPanel classPanelLast = null;
        int maxWidth = 0;
        int maxHeight = 0;
        exit:
        for (int r = 0; r < columnAndRows; r++) {
            for (int c = 0; c < columnAndRows; c++) {
                if (iterator.hasNext()) {
                    classPanelLast = iterator.next();
                    int locationX = sizeWidthClean + indent;
                    int locationY = sizeHeightClean + indent;
                    LOG.trace("Расположение класса {} на групповой панели: X {} Y {}", classPanelLast.getClass_().getOriginalTypeName(), locationX, locationY);
                    classPanelLast.setLocation(sizeWidthClean + indent, sizeHeightClean + indent);
                    sizeWidthClean += classPanelLast.getWidth() + indent;
                    maxWidth = Math.max(maxWidth, sizeWidthClean);
                    add(classPanelLast);
                    classPanelLocationStorage.add(classPanelLast.getClass_().getOriginalTypeName(), classPanelLast);
                } else {
                    if (classPanelLast != null && c != 0 /*это значит, что ряд полностью не заполнился*/) {
                        maxHeight = Math.max(maxHeight, sizeHeightClean + classPanelLast.getHeight() + indent);
                    }
                    break exit;
                }
            }
            sizeWidthClean = 0;
            sizeHeightClean += classPanelLast.getHeight() + indent;
            maxHeight = Math.max(maxHeight, sizeHeightClean);
        }
        Dimension dimension = new Dimension(maxWidth + indent, maxHeight + indent);
        setSize(dimension);
        setPreferredSize(dimension);
        LOG.trace("Ширина и высота сборки: W {} H {}", maxWidth, maxHeight);

        MouseMoveWindowListener mouseMoveWindowListener = new MouseMoveWindowListener(this);
        this.addMouseMotionListener(mouseMoveWindowListener);
        this.addMouseListener(mouseMoveWindowListener);
    }

    @Override
    public void paint(Graphics g) {
        super.paint(g);
        for (ClassPanel classPanel : classPanels) {
            Graphics2D graphics2D = (Graphics2D) g;
            Point locationOnScreen = classPanel.getLocationOnScreen();
            Point location = classPanel.getLocation();
            graphics2D.drawString(
                    "x: " + locationOnScreen.x + " y: " + locationOnScreen.y,
                    location.x,
                    location.y - 5
            );
        }

    }
}
