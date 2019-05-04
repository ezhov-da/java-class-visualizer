package ru.ezhov.jclsvis.gui.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.concurrent.atomic.AtomicBoolean;

public class MouseMoveWindowListener extends MouseAdapter {

    private static final Logger LOG = LoggerFactory.getLogger(MouseMoveWindowListener.class);

    private Point diffOnScreen;
    private Component component;

    public MouseMoveWindowListener(Component component) {
        this.component = component;
    }

    private AtomicBoolean atomicBooleanStartDrag = new AtomicBoolean();

    @Override
    public void mousePressed(MouseEvent e) {
        SwingUtilities.invokeLater(() -> {
            atomicBooleanStartDrag.set(true);
            component.setCursor(new Cursor(Cursor.MOVE_CURSOR));
            Point pressedPointLocationOnScreen = e.getLocationOnScreen();
            int x = pressedPointLocationOnScreen.x - component.getLocationOnScreen().x;
            int y = pressedPointLocationOnScreen.y - component.getLocationOnScreen().y;
            diffOnScreen = new Point(x, y);
//            LOG.trace("Начато перетаскивание компонента. Разница в точке начала и выборе: {}", diffOnScreen);
        });
    }

    @Override
    public void mouseReleased(MouseEvent e) {
        if (atomicBooleanStartDrag.get()) {
            component.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
            atomicBooleanStartDrag.set(false);
        }
    }

    @Override
    public void mouseDragged(MouseEvent e) {
        SwingUtilities.invokeLater(() -> {
            if (atomicBooleanStartDrag.get()) {

                Point nowMouseLocation = e.getLocationOnScreen();
                Point point = new Point(
                        nowMouseLocation.x - diffOnScreen.x,
                        nowMouseLocation.y - diffOnScreen.y
                );
//            LOG.trace("Новое расположение компонента до конвертации {}.", point);
                SwingUtilities.convertPointFromScreen(point, component.getParent());
//            LOG.trace("Новое расположение компонента после конвертации {}.", point);
                component.setLocation(point);
            }
        });
    }
}
