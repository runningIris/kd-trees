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
    }

    private Node root;
    private int length;
    private Point2D nearest;
    private double nearestDistance;
    private boolean contains;

    // construct an empty set of points
    public KdTree() {
        root = new Node();
        root.rect = new RectHV(0, 0, 1, 1);
    }

    // 水平插入点 p
    private void insertHorizontal(Point2D p, Node node) {
        if (node.p == null) {
            node.p = p;
            node.lb = new Node();
            node.lb.rect = new RectHV(node.rect.xmin(), node.rect.ymin(), p.x(), node.rect.ymax());
            node.rt = new Node();
            node.rt.rect = new RectHV(p.x(), node.rect.ymin(), node.rect.xmax(), node.rect.ymax());
        }
    }

    // 竖直插入点 p
    private void insertVertical(Point2D p, Node node) {
        if (node.p == null) {
            node.p = p;
            node.lb = new Node();
            node.lb.rect = new RectHV(node.rect.xmin(), node.rect.ymin(), node.rect.xmax(), p.y());
            node.rt = new Node();
            node.rt.rect = new RectHV(node.rect.xmin(), p.y(), node.rect.xmax(), node.rect.ymax());
        }
    }

    private void dfsDraw(Node node, Boolean flag) {
        if(node.p != null) {
            StdDraw.setPenRadius(0.005);
            if (flag) {
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

            StdDraw.setPenRadius(0.01);
            StdDraw.setPenColor(Color.black);
            node.p.draw();

            if (node.lb.p != null) dfsDraw(node.lb, !flag);
            if (node.rt.p != null) dfsDraw(node.rt, !flag);
        }
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

    // is the set empty?
    public boolean isEmpty() {
        return root.p == null;
    }

    // number of points in the set
    public int size() { return length; }

    // add the point to the set (if it is not already in the set)
    public void insert(Point2D p) {

        Boolean flag = true; // 是否以 x 轴作为分界线
        Node node = root; // 当前节点

        while (node.p != null) {

            if (flag) {
                node = p.x() <= node.p.x() ? node.lb : node.rt; // 以 x 为分界线，沿 x 轴的左右递归
            } else {
                node = p.y() <= node.p.y() ? node.lb : node.rt; // 以 y 为分界线，沿 y 轴的上下递归
            }

            flag = !flag;
        }

        // 插入点 p
        if (flag) {
            insertHorizontal(p, node);
        } else {
            insertVertical(p, node);
        }

        length++;
    }

    private void dfsContains(Node node, Point2D p) {
        if (node.p.x() == node.p.y()) {
            contains = true;
        }

        if (node.lb.rect.contains(p)) dfsContains(node.lb, p);
        if (node.rt.rect.contains(p)) dfsContains(node.rt, p);
    }

    // does the set contain point p?
    public boolean contains(Point2D p) {
        contains = false;
        dfsContains(root, p);
        return contains;
    }


    // draw all points to standard draw
    public void draw() {
        dfsDraw(root, true);

    }

    // all points that are inside the rectangle (or on the boundary)
    public Iterable<Point2D> range(RectHV rect) {
        // only checks the rectangle that intersect with rect
        if (rect == null) return null;
        return dfsRange(root, rect);
    }

    private void dfsNearest(Node node, Point2D p) {
        if (node.p == null) return;
        if (node.p.distanceSquaredTo(p) == 0) return; // 排除本身

        double distance = node.p.distanceSquaredTo(p);

        if (distance < nearestDistance) {
            nearestDistance = distance;
            nearest = node.p;
        }

        if (node.lb != null && nearestDistance > node.lb.rect.distanceSquaredTo(p)) {
            dfsNearest(node.lb, p);
        }

        if (node.rt != null && nearestDistance > node.rt.rect.distanceSquaredTo(p)) {
            dfsNearest(node.rt, p);
        }
    }

    // a nearest neighbor in the set to point p; null if the set is empty
    public Point2D nearest(Point2D p) {
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

        StdOut.println(ps.size());
        ps.draw();
    }
}
