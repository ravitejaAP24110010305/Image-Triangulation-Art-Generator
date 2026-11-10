package algorithm;

import java.awt.image.BufferedImage;

/** Simple random selection: points are scattered uniformly over the image. */
public class RandomPointSelector extends PointSelector {

    public RandomPointSelector(long seed) {
        super(seed);
    }

    @Override
    public String getName() {
        return "Random points";
    }

    @Override
    protected void selectInterior(BufferedImage img, int count, double base, PointGrid grid) {
        int w = img.getWidth();
        int h = img.getHeight();
        double minDist = Math.max(edgeDist(base), base * 0.6);
        int added = 0;
        for (long tries = 0; added < count && tries < (long) count * 60; tries++) {
            if (grid.add(rnd.nextInt(w), rnd.nextInt(h), minDist, false)) added++;
        }
    }
}
