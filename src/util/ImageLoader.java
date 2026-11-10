package util;

import exceptions.InvalidImageException;

import javax.imageio.ImageIO;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

/** File input: reads an image chosen in the file explorer and scales big photos down for speed. */
public final class ImageLoader {
    private static final int MAX_SIDE = 1200;

    private ImageLoader() { }

    public static BufferedImage load(File file) throws InvalidImageException {
        if (file == null || !file.isFile()) {
            throw new InvalidImageException("The selected file does not exist.");
        }
        try {
            BufferedImage img = ImageIO.read(file);
            if (img == null) {
                throw new InvalidImageException("'" + file.getName() + "' is not a supported image file.");
            }
            return toScaledRgb(img);
        } catch (IOException e) {
            throw new InvalidImageException("Could not read the file: " + e.getMessage(), e);
        }
    }

    private static BufferedImage toScaledRgb(BufferedImage in) {
        double scale = Math.min(1.0, (double) MAX_SIDE / Math.max(in.getWidth(), in.getHeight()));
        int w = Math.max(1, (int) Math.round(in.getWidth() * scale));
        int h = Math.max(1, (int) Math.round(in.getHeight() * scale));
        BufferedImage out = new BufferedImage(w, h, BufferedImage.TYPE_INT_RGB);
        Graphics2D g = out.createGraphics();
        g.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
        g.drawImage(in, 0, 0, w, h, null);
        g.dispose();
        return out;
    }
}
