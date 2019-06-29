package ru.ezhov.jclsvis.gui.utils.distance;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.awt.*;

public class MinimalDistance {
    private static final Logger LOG = LoggerFactory.getLogger(MinimalDistance.class);
    private Point from;
    private Point to;

    private double min = Double.MAX_VALUE;

    public Distance find(CenterPoints from, CenterPoints to) {
        check(from.getPointWest(), to.getPointWest());
        check(from.getPointWest(), to.getPointEast());
        check(from.getPointWest(), to.getPointNorth());
        check(from.getPointWest(), to.getPointSouth());

        check(from.getPointEast(), to.getPointWest());
        check(from.getPointEast(), to.getPointEast());
        check(from.getPointEast(), to.getPointNorth());
        check(from.getPointEast(), to.getPointSouth());

        check(from.getPointNorth(), to.getPointWest());
        check(from.getPointNorth(), to.getPointEast());
        check(from.getPointNorth(), to.getPointNorth());
        check(from.getPointNorth(), to.getPointSouth());

        check(from.getPointSouth(), to.getPointWest());
        check(from.getPointSouth(), to.getPointEast());
        check(from.getPointSouth(), to.getPointNorth());
        check(from.getPointSouth(), to.getPointSouth());

        //LOG.trace("from {} to {} min {}", this.from, this.to, min);
        return new Distance(this.from, this.to);
    }

    private void check(Point from, Point to) {
        //https://ru.onlinemschool.com/math/library/analytic_geometry/point_point_length/
        double current = Math.sqrt(Math.pow(to.x - from.x, 2D) + Math.pow(to.y - from.y, 2D));

        if (current <= min) {
            //LOG.trace("from {} to {} current {} min {}", from, to, min, current);
            min = current;
            this.from = from;
            this.to = to;
        }
    }
}
