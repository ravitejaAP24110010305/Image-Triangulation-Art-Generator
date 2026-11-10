package render;

import model.ArtResult;
import model.Triangle;

import java.awt.Graphics2D;
import java.awt.Polygon;
import java.awt.image.BufferedImage;

/** Draws every triangle filled with its colour (AWT Graphics2D). */
public class FilledRenderer implements Renderer {

    @Override
    public BufferedImage render(ArtResult result) {
        BufferedImage out = new BufferedImage(result.getWidth(), result.getHeight(), BufferedImage.TYPE_INT_RGB);
        Graphics2D g = out.createGraphics();
        for (Triangle t : result.getTriangles()) {
            Polygon p = toPolygon(result, t);
            g.setColor(t.getColor());
            g.fillPolygon(p);
            g.drawPolygon(p);          // same-colour outline hides hairline seams
        }
        g.dispose();
        return out;
    }

    @Override
    public String getName() {
        return "Filled triangles";
    }

    protected Polygon toPolygon(ArtResult result, Triangle t) {
        Polygon p = new Polygon();
        p.addPoint(result.getPoints().get(t.getA()).x, result.getPoints().get(t.getA()).y);
        p.addPoint(result.getPoints().get(t.getB()).x, result.getPoints().get(t.getB()).y);
        p.addPoint(result.getPoints().get(t.getC()).x, result.getPoints().get(t.getC()).y);
        return p;
    }
}
