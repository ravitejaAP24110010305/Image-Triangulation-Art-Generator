package model;

import java.awt.Color;

/** One triangle of the mesh: three point indices and the colour it is filled with. */
public class Triangle {
    private final int a;
    private final int b;
    private final int c;
    private Color color = Color.GRAY;

    public Triangle(int a, int b, int c) {
        this.a = a;
        this.b = b;
        this.c = c;
    }

    public int getA() { return a; }
    public int getB() { return b; }
    public int getC() { return c; }
    public Color getColor() { return color; }
    public void setColor(Color color) { this.color = color; }
}
