package ru.ezhov.jclsvis.gui.utils.distance;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.awt.*;

public class CenterPoints {
    private static final Logger LOG = LoggerFactory.getLogger(MinimalDistance.class);
    private Point pointWest;
    private Point pointNorth;
    private Point pointSouth;
    private Point pointEast;

    private CenterPoints() {
    }

    public static CenterPoints from(Component component) {
        CenterPoints centerPoints = new CenterPoints();
        Point location = component.getLocationOnScreen();
        centerPoints.pointWest = new Point(location.x, location.y + component.getHeight() / 2);
        centerPoints.pointNorth = new Point(location.x + component.getWidth() / 2, location.y);
        centerPoints.pointSouth = new Point(location.x + component.getWidth() / 2, location.y + component.getHeight());
        centerPoints.pointEast = new Point(location.x + component.getWidth(), location.y + component.getHeight() / 2);
//        LOG.trace("pointWest {} pointNorth {} pointSouth {} pointEast {}", centerPoints.pointWest, centerPoints.pointNorth, centerPoints.pointSouth, centerPoints.pointEast);
        return centerPoints;
    }

    public Point getPointWest() {
        return new Point(pointWest);
    }

    public Point getPointNorth() {
        return new Point(pointNorth);
    }

    public Point getPointSouth() {
        return new Point(pointSouth);
    }

    public Point getPointEast() {
        return new Point(pointEast);
    }
}
