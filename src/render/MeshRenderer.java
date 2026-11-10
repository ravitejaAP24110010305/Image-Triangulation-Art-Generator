package render;

import model.ArtResult;
import model.Triangle;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;

/** Inherits FilledRenderer and additionally draws the triangle edges (method overriding). */
public class MeshRenderer extends FilledRenderer {

    @Override
    public BufferedImage render(ArtResult result) {
        BufferedImage img = super.render(result);
        Graphics2D g = img.createGraphics();
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g.setColor(new Color(255, 255, 255, 110));
        for (Triangle t : result.getTriangles()) {
            g.drawPolygon(toPolygon(result, t));
        }
        g.dispose();
        return img;
    }

    @Override
    public String getName() {
        return "Triangles with mesh";
    }
}
