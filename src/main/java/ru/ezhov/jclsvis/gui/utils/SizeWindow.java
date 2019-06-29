package ru.ezhov.jclsvis.gui.utils;

import javax.swing.*;
import java.awt.*;

public class SizeWindow extends JDialog {
    JSpinner spinner = new JSpinner(new SpinnerNumberModel(1, 1, 1000, 5));
    JButton buttonDown = new JButton("D");
    JButton buttonRight = new JButton("R");
    JButton buttonUp = new JButton("U");
    JButton buttonLeft = new JButton("L");

    private Component component;

    public SizeWindow(Component component) {
        this.component = component;
        this.setSize(300, 200);
        this.setLocationRelativeTo(null);
        JPanel panel = new JPanel();
        panel.add(spinner);
        panel.add(buttonLeft);
        panel.add(buttonUp);
        panel.add(buttonDown);
        panel.add(buttonRight);
        add(panel, BorderLayout.CENTER);

        buttonLeft.addActionListener(actionEvent -> {
            SwingUtilities.invokeLater(() -> {
                Integer integer = Integer.valueOf(spinner.getValue() + "");
                Dimension preferredSize = component.getPreferredSize();
                Dimension newPreferredSize = new Dimension(preferredSize.width - integer, preferredSize.height);
                component.setSize(newPreferredSize);
                component.setPreferredSize(newPreferredSize);
                component.revalidate();
            });
        });

        buttonUp.addActionListener(actionEvent -> {
            SwingUtilities.invokeLater(() -> {
                Integer integer = Integer.valueOf(spinner.getValue() + "");
                Dimension preferredSize = component.getPreferredSize();
                Dimension newPreferredSize = new Dimension(preferredSize.width, preferredSize.height - integer);
                component.setSize(newPreferredSize);
                component.setPreferredSize(newPreferredSize);
                component.revalidate();
            });
        });

        buttonDown.addActionListener(actionEvent -> {
            SwingUtilities.invokeLater(() -> {
                Integer integer = Integer.valueOf(spinner.getValue() + "");
                Dimension preferredSize = component.getPreferredSize();
                Dimension newPreferredSize = new Dimension(preferredSize.width, preferredSize.height + integer);
                component.setSize(newPreferredSize);
                component.setPreferredSize(newPreferredSize);
                component.revalidate();
            });
        });

        buttonRight.addActionListener(actionEvent -> {
            SwingUtilities.invokeLater(() -> {
                Integer integer = Integer.valueOf(spinner.getValue() + "");
                Dimension preferredSize = component.getPreferredSize();
                Dimension newPreferredSize = new Dimension(preferredSize.width + integer, preferredSize.height);
                component.setSize(newPreferredSize);
                component.setPreferredSize(newPreferredSize);
                component.revalidate();
            });
        });
    }
}