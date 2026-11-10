package algorithm;

import exceptions.ProcessingCancelledException;
import model.Triangle;

import java.awt.Color;
import java.awt.Point;
import java.awt.image.BufferedImage;
import java.util.List;

/** Gives every triangle the average colour of the real pixels inside it. */
public class ColorCalculator {

    public void colorize(BufferedImage img, List<Point> pts, List<Triangle> tris, ProgressCallback cb)
            throws ProcessingCancelledException {
        for (int i = 0; i < tris.size(); i++) {
            if (i % 200 == 0) {
                if (cb.isCancelled()) throw new ProcessingCancelledException("Cancelled by user");
                cb.update(i * 100 / tris.size());
            }
            tris.get(i).setColor(average(img, pts, tris.get(i)));
        }
        cb.update(100);
    }

    private Color average(BufferedImage img, List<Point> pts, Triangle t) {
        Point a = pts.get(t.getA());
        Point b = pts.get(t.getB());
        Point c = pts.get(t.getC());
        int minX = Math.max(0, Math.min(a.x, Math.min(b.x, c.x)));
        int maxX = Math.min(img.getWidth() - 1, Math.max(a.x, Math.max(b.x, c.x)));
        int minY = Math.max(0, Math.min(a.y, Math.min(b.y, c.y)));
        int maxY = Math.min(img.getHeight() - 1, Math.max(a.y, Math.max(b.y, c.y)));
        int step = ((maxX - minX) * (maxY - minY) > 4000) ? 2 : 1;

        long r = 0, g = 0, bl = 0;
        int cnt = 0;
        for (int y = minY; y <= maxY; y += step) {
            for (int x = minX; x <= maxX; x += step) {
                if (inside(x, y, a, b, c)) {
                    int rgb = img.getRGB(x, y);
                    r += (rgb >> 16) & 255;
                    g += (rgb >> 8) & 255;
                    bl += rgb & 255;
                    cnt++;
                }
            }
        }
        if (cnt == 0) {   // tiny triangle: use the centroid pixel
            int cx = Math.min(img.getWidth() - 1, Math.max(0, (a.x + b.x + c.x) / 3));
            int cy = Math.min(img.getHeight() - 1, Math.max(0, (a.y + b.y + c.y) / 3));
            return new Color(img.getRGB(cx, cy));
        }
        return new Color((int) (r / cnt), (int) (g / cnt), (int) (bl / cnt));
    }

    private boolean inside(int px, int py, Point a, Point b, Point c) {
        long d1 = (long) (px - b.x) * (a.y - b.y) - (long) (a.x - b.x) * (py - b.y);
        long d2 = (long) (px - c.x) * (b.y - c.y) - (long) (b.x - c.x) * (py - c.y);
        long d3 = (long) (px - a.x) * (c.y - a.y) - (long) (c.x - a.x) * (py - a.y);
        boolean neg = d1 < 0 || d2 < 0 || d3 < 0;
        boolean pos = d1 > 0 || d2 > 0 || d3 > 0;
        return !(neg && pos);
    }
}
