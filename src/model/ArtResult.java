package model;

import java.awt.Point;
import java.util.List;

/** Holds everything produced by one generation run (points, triangles, image size). */
public class ArtResult {
    private final int width;
    private final int height;
    private final List<Point> points;
    private final List<Triangle> triangles;

    public ArtResult(int width, int height, List<Point> points, List<Triangle> triangles) {
        this.width = width;
        this.height = height;
        this.points = points;
        this.triangles = triangles;
    }

    public int getWidth() { return width; }
    public int getHeight() { return height; }
    public List<Point> getPoints() { return points; }
    public List<Triangle> getTriangles() { return triangles; }
}
