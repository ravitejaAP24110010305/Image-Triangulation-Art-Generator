package algorithm;

import java.awt.Point;
import java.util.ArrayList;
import java.util.List;

/** Stores points and rejects any new point that is too close to an existing one (uses a spatial grid). */
public class PointGrid {
    private final double cell;
    private final int[][] grid;
    private final List<Point> points = new ArrayList<>();

    public PointGrid(int width, int height, double cell) {
        this.cell = cell;
        this.grid = new int[(int) (width / cell) + 3][(int) (height / cell) + 3];
    }

    /** Adds a point; returns false when it is rejected (too close to another point). */
    public boolean add(int x, int y, double minDist, boolean force) {
        int cx = (int) (x / cell);
        int cy = (int) (y / cell);
        if (force) {
            if (grid[cx][cy] != 0) return false;
        } else {
            int rad = (int) Math.ceil(minDist / cell);
            for (int i = Math.max(0, cx - rad); i <= Math.min(grid.length - 1, cx + rad); i++) {
                for (int j = Math.max(0, cy - rad); j <= Math.min(grid[0].length - 1, cy + rad); j++) {
                    int idx = grid[i][j];
                    if (idx != 0) {
                        Point q = points.get(idx - 1);
                        double dx = q.x - x;
                        double dy = q.y - y;
                        if (dx * dx + dy * dy < minDist * minDist) return false;
                    }
                }
            }
        }
        points.add(new Point(x, y));
        grid[cx][cy] = points.size();
        return true;
    }

    public List<Point> getPoints() {
        return points;
    }
}
