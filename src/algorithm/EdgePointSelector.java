package algorithm;

import java.awt.image.BufferedImage;

/**
 * Edge-based selection (best for keeping the photo clear).
 * A Sobel filter finds edges; most points are placed where edges are strong,
 * the rest are spread evenly so flat areas still get triangles.
 */
public class EdgePointSelector extends PointSelector {
    private final int edgePercent;

    public EdgePointSelector(long seed, int edgePercent) {
        super(seed);
        this.edgePercent = edgePercent;
    }

    @Override
    public String getName() {
        return "Edge-based (best clarity)";
    }

    @Override
    protected void selectInterior(BufferedImage img, int count, double base, PointGrid grid) {
        int w = img.getWidth();
        int h = img.getHeight();
        double[] mag = sobel(img);
        double max = 1;
        for (double m : mag) {
            if (m > max) max = m;
        }
        double dEdge = edgeDist(base);
        double dFill = base * 0.85;
        int edgeTarget = count * edgePercent / 100;
        int fillTarget = count - edgeTarget;

        int added = 0;
        for (long tries = 0; added < edgeTarget && tries < (long) edgeTarget * 300; tries++) {
            int x = rnd.nextInt(w);
            int y = rnd.nextInt(h);
            double p = Math.min(1.0, mag[y * w + x] / (max * 0.5));
            if (rnd.nextDouble() < p && grid.add(x, y, dEdge, false)) added++;
        }
        added = 0;
        for (long tries = 0; added < fillTarget && tries < (long) fillTarget * 60; tries++) {
            if (grid.add(rnd.nextInt(w), rnd.nextInt(h), dFill, false)) added++;
        }
    }

    /** Sobel edge magnitude for every pixel. */
    private static double[] sobel(BufferedImage img) {
        int w = img.getWidth();
        int h = img.getHeight();
        int[] gray = new int[w * h];
        for (int y = 0; y < h; y++) {
            for (int x = 0; x < w; x++) {
                int rgb = img.getRGB(x, y);
                gray[y * w + x] = (int) (0.299 * ((rgb >> 16) & 255) + 0.587 * ((rgb >> 8) & 255) + 0.114 * (rgb & 255));
            }
        }
        double[] mag = new double[w * h];
        for (int y = 1; y < h - 1; y++) {
            for (int x = 1; x < w - 1; x++) {
                int i = y * w + x;
                double gx = -gray[i - w - 1] + gray[i - w + 1] - 2 * gray[i - 1] + 2 * gray[i + 1]
                        - gray[i + w - 1] + gray[i + w + 1];
                double gy = -gray[i - w - 1] - 2 * gray[i - w] - gray[i - w + 1]
                        + gray[i + w - 1] + 2 * gray[i + w] + gray[i + w + 1];
                mag[i] = Math.sqrt(gx * gx + gy * gy);
            }
        }
        return mag;
    }
}
