import edu.princeton.cs.algs4.Point2D;
import edu.princeton.cs.algs4.RectHV;
import edu.princeton.cs.algs4.StdOut;
import edu.princeton.cs.algs4.In;
import edu.princeton.cs.algs4.StdDraw;

import java.awt.Color;
import java.util.ArrayList;
import java.util.List;
import java.util.TreeSet;

public class PointSET {

    final private TreeSet<Point2D> allPoints;

    // construct an empty set of points
    public PointSET() {
        allPoints = new TreeSet<Point2D>();
    }

    // is the set empty?
    public boolean isEmpty() {
        return allPoints.isEmpty();
    }

    // number of points in the set
    public int size() { return allPoints.size(); }

    // add the point to the set (if it is not already in the set)
    public void insert(Point2D p) {
        if (p == null) throw new IllegalArgumentException("argument p is null in the insert method.");
        if (!contains(p)) allPoints.add(p);
    }

    // does the set contain point p?
    public boolean contains(Point2D p) {
        if (p == null) throw new IllegalArgumentException("argument p is null in the contains method.");
        return allPoints.contains(p);
    }

    // draw all points to standard draw
    public void draw() {
        for (Point2D p: allPoints) { p.draw(); }
    }

    // all points that are inside the rectangle (or on the boundary)
    public Iterable<Point2D> range(RectHV rect) {
        if (rect == null) throw new IllegalArgumentException("argument rect is null in the range method.");

        List<Point2D> list = new ArrayList<Point2D>();

        for (Point2D p: allPoints) {
            if (rect.contains(p)) {
                list.add(p);
            }
        }

        return list;
    }
    // a nearest neighbor in the set to point p; null if the set is empty
    public Point2D nearest(Point2D p) {
        if (p == null) throw new IllegalArgumentException("argument p is null in the nearest method.");

        Point2D result = null;
        double nearest = Double.POSITIVE_INFINITY;

        if (isEmpty()) {
            return result;
        }

        for (Point2D other: allPoints) {
            double distance = p.distanceSquaredTo(other);

            if (distance < nearest) {
                nearest = distance;
                result = other;
            }
        }

        return result;
    }

    // unit testing of the methods (optional)
    public static void main(String[] args) {
        In file = new In(args[0]);

        PointSET ps = new PointSET();

        while (!file.isEmpty()) {
            ps.insert(new Point2D(file.readDouble(), file.readDouble()));
        }

        ps.draw();

        Point2D nearest = ps.nearest(new Point2D(0, 0));

        StdOut.println(nearest.toString());

        StdDraw.setPenColor(Color.PINK);
        StdDraw.setPenRadius(0.01);

        nearest.draw();

        StdOut.println(ps.size());
    }
}
