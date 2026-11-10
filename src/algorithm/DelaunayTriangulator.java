package algorithm;

import exceptions.ProcessingCancelledException;
import model.Triangle;

import java.awt.Point;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Delaunay triangulation using the Bowyer-Watson algorithm.
 * Delaunay condition: no point lies inside the circumcircle of any triangle.
 */
public class DelaunayTriangulator {

    /** Internal triangle that also stores its circumcircle. */
    private static class Tri {
        final int a;
        final int b;
        final int c;
        double cx;
        double cy;
        double r2;

        Tri(int a, int b, int c, double[] xs, double[] ys) {
            this.a = a;
            this.b = b;
            this.c = c;
            double ax = xs[a], ay = ys[a], bx = xs[b], by = ys[b], qx = xs[c], qy = ys[c];
            double d = 2 * (ax * (by - qy) + bx * (qy - ay) + qx * (ay - by));
            if (Math.abs(d) < 1e-9) {          // degenerate (collinear) triangle: never "bad"
                r2 = -1;
                return;
            }
            double a2 = ax * ax + ay * ay;
            double b2 = bx * bx + by * by;
            double c2 = qx * qx + qy * qy;
            cx = (a2 * (by - qy) + b2 * (qy - ay) + c2 * (ay - by)) / d;
            cy = (a2 * (qx - bx) + b2 * (ax - qx) + c2 * (bx - ax)) / d;
            r2 = (ax - cx) * (ax - cx) + (ay - cy) * (ay - cy);
        }

        boolean circumcircleContains(double x, double y) {
            double dx = x - cx;
            double dy = y - cy;
            return dx * dx + dy * dy < r2;
        }
    }

    public List<Triangle> triangulate(List<Point> pts, int w, int h, ProgressCallback cb)
            throws ProcessingCancelledException {
        int n = pts.size();
        double[] xs = new double[n + 3];
        double[] ys = new double[n + 3];
        for (int i = 0; i < n; i++) {
            xs[i] = pts.get(i).x;
            ys[i] = pts.get(i).y;
        }
        // super triangle enclosing all points
        double m = Math.max(w, h) * 50.0;
        xs[n] = w / 2.0 - 2 * m;     ys[n] = -m;
        xs[n + 1] = w / 2.0;         ys[n + 1] = 2 * m;
        xs[n + 2] = w / 2.0 + 2 * m; ys[n + 2] = -m;

        List<Tri> tris = new ArrayList<>();
        tris.add(new Tri(n, n + 1, n + 2, xs, ys));

        for (int p = 0; p < n; p++) {
            if (p % 50 == 0) {
                if (cb.isCancelled()) throw new ProcessingCancelledException("Cancelled by user");
                cb.update(p * 100 / n);
            }
            Map<Long, int[]> edges = new HashMap<>();   // edge -> {count, u, v}
            List<Tri> keep = new ArrayList<>(tris.size());
            for (Tri t : tris) {
                if (t.circumcircleContains(xs[p], ys[p])) {
                    addEdge(edges, t.a, t.b);
                    addEdge(edges, t.b, t.c);
                    addEdge(edges, t.c, t.a);
                } else {
                    keep.add(t);
                }
            }
            for (int[] e : edges.values()) {
                if (e[0] == 1) keep.add(new Tri(e[1], e[2], p, xs, ys));   // boundary of the hole
            }
            tris = keep;
        }
        cb.update(100);

        List<Triangle> out = new ArrayList<>();
        for (Tri t : tris) {
            if (t.a < n && t.b < n && t.c < n) out.add(new Triangle(t.a, t.b, t.c));
        }
        return out;
    }

    private static void addEdge(Map<Long, int[]> map, int u, int v) {
        long key = (long) Math.min(u, v) * 1_000_003L + Math.max(u, v);
        int[] e = map.get(key);
        if (e == null) {
            map.put(key, new int[]{1, u, v});
        } else {
            e[0]++;
        }
    }
}
