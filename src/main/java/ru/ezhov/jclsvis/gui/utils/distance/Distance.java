package ru.ezhov.jclsvis.gui.utils.distance;

import java.awt.*;

public class Distance {
    Point from;
    Point to;

    Distance(Point from, Point to) {
        this.from = from;
        this.to = to;
    }

    public Point getFrom() {
        return from;
    }

    public Point getTo() {
        return to;
    }

    @Override
    public String toString() {
        return "Distance{" +
                "from=" + from +
                ", to=" + to +
                '}';
    }
}
