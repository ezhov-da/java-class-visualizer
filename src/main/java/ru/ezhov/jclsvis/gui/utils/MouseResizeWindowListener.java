package ru.ezhov.jclsvis.gui.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class MouseResizeWindowListener extends MouseAdapter {

    private static final Logger LOG = LoggerFactory.getLogger(MouseResizeWindowListener.class);

    private Point diffOnScreen;
    private Component component;

    public MouseResizeWindowListener(Component component) {
        this.component = component;
    }

    @Override
    public void mouseMoved(MouseEvent e) {
        Component componentSource = (Component) e.getSource();
        Point location = e.getLocationOnScreen();
        SwingUtilities.convertPointFromScreen(location, componentSource);
        System.out.println(location);

    }

    @Override
    public void mousePressed(MouseEvent e) {
        SwingUtilities.invokeLater(() -> {
            Point pressedPointLocationOnScreen = e.getLocationOnScreen();
            int x = pressedPointLocationOnScreen.x - component.getLocationOnScreen().x;
            int y = pressedPointLocationOnScreen.y - component.getLocationOnScreen().y;
            diffOnScreen = new Point(x, y);
//            LOG.trace("Начато перетаскивание компонента. Разница в точке начала и выборе: {}", diffOnScreen);
        });
    }

    @Override
    public void mouseDragged(MouseEvent e) {
        SwingUtilities.invokeLater(() -> {
            Point nowMouseLocation = e.getLocationOnScreen();
            Point point = new Point(
                    nowMouseLocation.x - diffOnScreen.x,
                    nowMouseLocation.y - diffOnScreen.y
            );
//            LOG.trace("Новое расположение компонента до конвертации {}.", point);
            SwingUtilities.convertPointFromScreen(point, component.getParent());
//            LOG.trace("Новое расположение компонента после конвертации {}.", point);
            component.setLocation(point);
        });
    }
}
