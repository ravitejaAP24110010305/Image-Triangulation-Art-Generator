package algorithm;

import java.awt.image.BufferedImage;

/** Regular grid of points, each shifted by a small random amount (even, "crystal" look). */
public class JitteredGridPointSelector extends PointSelector {

    public JitteredGridPointSelector(long seed) {
        super(seed);
    }

    @Override
    public String getName() {
        return "Jittered grid";
    }

    @Override
    protected void selectInterior(BufferedImage img, int count, double base, PointGrid grid) {
        int w = img.getWidth();
        int h = img.getHeight();
        double minDist = Math.max(edgeDist(base), base * 0.5);
        for (double y = base / 2; y < h; y += base) {
            for (double x = base / 2; x < w; x += base) {
                int px = clamp((int) (x + (rnd.nextDouble() - 0.5) * base * 0.6), w);
                int py = clamp((int) (y + (rnd.nextDouble() - 0.5) * base * 0.6), h);
                grid.add(px, py, minDist, false);
            }
        }
    }

    private static int clamp(int v, int size) {
        return Math.max(0, Math.min(size - 1, v));
    }
}
