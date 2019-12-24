import edu.princeton.cs.algs4.Point2D;
import edu.princeton.cs.algs4.RectHV;
import edu.princeton.cs.algs4.StdOut;
import edu.princeton.cs.algs4.In;
import java.util.ArrayList;
import java.util.List;
import java.util.TreeSet;

public class PointSET {

    private TreeSet<Point2D> allPoints;

    // construct an empty set of points
    public PointSET() {
        allPoints = new TreeSet();
    }

    // is the set empty?
    public boolean isEmpty() {
        return allPoints.isEmpty();
    }

    // number of points in the set
    public int size() { return allPoints.size(); }

    // add the point to the set (if it is not already in the set)
    public void insert(Point2D p) { if (p != null) allPoints.add(p); }

    // does the set contain point p?
    public boolean contains(Point2D p) {
        if (p == null) return false;
        return allPoints.contains(p);
    }

    // draw all points to standard draw
    public void draw() {
        for(Point2D p: allPoints) { p.draw(); }
    }

    // all points that are inside the rectangle (or on the boundary)
    public Iterable<Point2D> range(RectHV rect) {
        if (rect == null) return null;

        List<Point2D> list = new ArrayList<Point2D>();

        for(Point2D p: allPoints) {
            if (rect.contains(p)) {
                list.add(p);
            }
        }

        return list;
    }
    // a nearest neighbor in the set to point p; null if the set is empty
    public Point2D nearest(Point2D p) {

        Point2D result = null;
        double nearest = Double.POSITIVE_INFINITY;

        if (isEmpty()) {
            return result;
        }

        for (Point2D other: allPoints) {
            double distance = p.distanceTo(other);
            if (distance < nearest && distance > 0) {
                nearest = distance;
                result = p;
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

        StdOut.println(ps.size());
    }
}
