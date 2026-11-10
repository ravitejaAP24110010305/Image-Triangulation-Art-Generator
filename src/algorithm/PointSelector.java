package algorithm;

import exceptions.TooFewPointsException;

import java.awt.Point;
import java.awt.image.BufferedImage;
import java.util.List;
import java.util.Random;

/**
 * ABSTRACT base class (inheritance + polymorphism).
 * select() is a "template method": it validates, adds border points and then calls
 * the abstract selectInterior(), which each subclass implements in its own way.
 */
public abstract class PointSelector {
    public static final int MIN_POINTS = 100;

    protected final Random rnd;

    protected PointSelector(long seed) {
        this.rnd = new Random(seed);
    }

    /** Name shown in the algorithm drop-down. */
    public abstract String getName();

    /** Subclasses choose the interior points in their own way. */
    protected abstract void selectInterior(BufferedImage img, int count, double base, PointGrid grid);

    public final List<Point> select(BufferedImage img, int count) throws TooFewPointsException {
        if (count < MIN_POINTS) {
            throw new TooFewPointsException("At least " + MIN_POINTS + " points are needed, but only "
                    + count + " were requested. Increase the Points slider.");
        }
        int w = img.getWidth();
        int h = img.getHeight();
        double base = Math.sqrt((double) w * h / count);
        PointGrid grid = new PointGrid(w, h, edgeDist(base) / 1.414);
        addBorder(grid, w, h, base);
        selectInterior(img, count, base, grid);
        return grid.getPoints();
    }

    /** Smallest allowed distance between two points. */
    protected static double edgeDist(double base) {
        return Math.max(1.5, base * 0.35);
    }

    /** Border and corner points guarantee the mesh covers the whole image. */
    private void addBorder(PointGrid grid, int w, int h, double base) {
        double step = Math.max(8, base * 1.5);
        for (double x = 0; x < w; x += step) {
            grid.add((int) x, 0, 0, true);
            grid.add((int) x, h - 1, 0, true);
        }
        for (double y = 0; y < h; y += step) {
            grid.add(0, (int) y, 0, true);
            grid.add(w - 1, (int) y, 0, true);
        }
        grid.add(w - 1, 0, 0, true);
        grid.add(0, h - 1, 0, true);
        grid.add(w - 1, h - 1, 0, true);
    }

    @Override
    public String toString() {
        return getName();
    }
}
