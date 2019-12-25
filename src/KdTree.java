import edu.princeton.cs.algs4.In;
import edu.princeton.cs.algs4.Point2D;
import edu.princeton.cs.algs4.RectHV;
import edu.princeton.cs.algs4.StdDraw;
import edu.princeton.cs.algs4.StdOut;

import java.awt.Color;
import java.util.ArrayList;
import java.util.List;

public class KdTree {

    private static class Node {
        private Point2D p;
        private RectHV rect;
        private Node lb;
        private Node rt;
        private boolean flag;
    }

    final private Node root;
    private int length;
    private Point2D nearest;
    private double nearestDistance;
    private boolean contains;

    // construct an empty set of points
    public KdTree() {
        root = new Node();
        root.rect = new RectHV(0, 0, 1, 1);
        root.flag = true;
    }

    private void dfsDraw(Node node) {
        if (node.p != null) {
            if (node.flag) {
                StdDraw.setPenColor(Color.red);
                double x = node.p.x();
                double ymin = node.rect.ymin();
                double ymax = node.rect.ymax();
                StdDraw.line(x, ymin, x, ymax);

            } else {
                StdDraw.setPenColor(Color.blue);
                double y = node.p.y();
                double xmin = node.rect.xmin();
                double xmax = node.rect.xmax();
                StdDraw.line(xmin, y, xmax, y);
            }

            StdDraw.setPenColor(Color.black);
            node.p.draw();

            if (node.lb.p != null) dfsDraw(node.lb);
            if (node.rt.p != null) dfsDraw(node.rt);
        }
    }

    private void insert(Node node, Point2D p) {
        node.p = p;
        node.lb = new Node();
        node.rt = new Node();

        node.lb.flag = !node.flag;
        node.rt.flag = !node.flag;

        double lbXMin;
        double lbYMin;
        double lbXMax;
        double lbYMax;
        double rtXMin;
        double rtYMin;
        double rtXMax;
        double rtYMax;

        if (node.flag) { // 竖线分开矩形
            lbXMin = node.rect.xmin();
            lbXMax = p.x();
            lbYMin = node.rect.ymin();
            lbYMax = node.rect.ymax();

            rtXMin = p.x();
            rtXMax = node.rect.xmax();
            rtYMin = node.rect.ymin();
            rtYMax = node.rect.ymax();

        } else { // 横线分开矩形
            lbXMin = node.rect.xmin();
            lbXMax = node.rect.xmax();
            lbYMin = node.rect.ymin();
            lbYMax = p.y();

            rtXMin = node.rect.xmin();
            rtXMax = node.rect.xmax();
            rtYMin = p.y();
            rtYMax = node.rect.ymax();
        }

        node.lb.rect = new RectHV(lbXMin, lbYMin, lbXMax, lbYMax);
        node.rt.rect = new RectHV(rtXMin, rtYMin, rtXMax, rtYMax);
        length++;
    }

    private List<Point2D> dfsRange(Node node, RectHV rect) {

        if (node == null) return null;

        if (!node.rect.intersects(rect)) return null;

        List<Point2D> list = new ArrayList<Point2D>();

        if (node.p != null && rect.contains(node.p)) {
            list.add(node.p);
        }

        List<Point2D> lb = dfsRange(node.lb, rect);
        List<Point2D> rt = dfsRange(node.rt, rect);
        if (lb != null) {
            list.addAll(lb);
        }
        if (rt != null) {
            list.addAll(rt);
        }

        return list;
    }


    private void dfsContains(Node node, Point2D p) {
        if (node.p == null) return;

        if (node.p.x() == p.x() && node.p.y() == p.y()) {
            contains = true;
        }

        if (node.lb != null && node.lb.rect.contains(p)) dfsContains(node.lb, p);
        if (node.rt != null && node.rt.rect.contains(p)) dfsContains(node.rt, p);
    }

    private void dfsNearest(Node node, Point2D p) {
        if (node.p == null) return;

        // StdOut.println("Searching: " + node.p.toString());

        if (node.p.distanceSquaredTo(p) == 0) {
            nearestDistance = 0;
            nearest = node.p;
            return; // 本身
        }

        double distance = node.p.distanceSquaredTo(p);

        if (distance < nearestDistance) {
            nearestDistance = distance;
            nearest = node.p;
        }

        if (
                (node.flag && p.x() > node.p.x()) /* 以竖线分割，并且 p 在竖线的右边 */
                ||
                (!node.flag && p.y() > node.p.y()) /* 以横线分割，并且 p 在横线的上边 */
        ) {
            // 先搜右上
            if (node.rt != null && nearestDistance > node.rt.rect.distanceSquaredTo(p)) {
                dfsNearest(node.rt, p);
            }

            if (node.lb != null && nearestDistance > node.lb.rect.distanceSquaredTo(p)) {
                dfsNearest(node.lb, p);
            }
        } else {
            // 先搜左下
            if (node.lb != null && nearestDistance > node.lb.rect.distanceSquaredTo(p)) {
                dfsNearest(node.lb, p);
            }

            if (node.rt != null && nearestDistance > node.rt.rect.distanceSquaredTo(p)) {
                dfsNearest(node.rt, p);
            }
        }


    }

    // is the set empty?
    public boolean isEmpty() {
        return root.p == null;
    }

    // number of points in the set
    public int size() { return length; }

    // add the point to the set (if it is not already in the set)
    public void insert(Point2D p) {
        if (p == null) throw new IllegalArgumentException("argument p is null in the insert method.");
        Node node = root;

        while (node.p != null) {
            if (node.p.x() == p.x() && node.p.y() == p.y()) return;

            if (node.flag) {
                node = p.x() < node.p.x() ? node.lb : node.rt;
            } else {
                node = p.y() < node.p.y() ? node.lb : node.rt;
            }
        }

        insert(node, p);

    }


    // does the set contain point p?
    public boolean contains(Point2D p) {
        if (p == null) throw new IllegalArgumentException("argument p is null in the contains method.");

        contains = false;
        dfsContains(root, p);
        return contains;
    }


    // draw all points to standard draw
    public void draw() {
        dfsDraw(root);
    }

    // all points that are inside the rectangle (or on the boundary)
    public Iterable<Point2D> range(RectHV rect) {
        // only checks the rectangle that intersect with rect
        if (rect == null) throw new IllegalArgumentException("argument rect is null in the range method.");

        return dfsRange(root, rect);
    }

    // a nearest neighbor in the set to point p; null if the set is empty
    public Point2D nearest(Point2D p) {
        if (p == null) throw new IllegalArgumentException("argument p is null in the nearest method.");

        if (root.p == null) return null;

        nearest = root.p;

        nearestDistance = root.p.distanceSquaredTo(p);

        dfsNearest(root, p);

        return nearest;
    }

    // unit testing of the methods (optional)
    public static void main(String[] args) {
        In file = new In(args[0]);

        KdTree ps = new KdTree();

        while (!file.isEmpty()) {
            ps.insert(new Point2D(file.readDouble(), file.readDouble()));
        }

        Point2D query = new Point2D(0.96, 0.74);
        StdDraw.setPenColor(Color.PINK);
        StdDraw.setPenRadius(0.01);
        query.draw();
        StdDraw.setPenRadius(0.001);

        StdOut.println("Nearest: " + ps.nearest(query));
        StdOut.println(ps.size());
        ps.draw();

    }
}
