package util;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

/** File output: saves the generated artwork as PNG or JPG. */
public final class ImageSaver {

    private ImageSaver() { }

    /** Returns the file that was actually written (extension is added if missing). */
    public static File save(BufferedImage img, File target) throws IOException {
        String name = target.getName().toLowerCase();
        String format = (name.endsWith(".jpg") || name.endsWith(".jpeg")) ? "jpg" : "png";
        if (!name.endsWith(".png") && !name.endsWith(".jpg") && !name.endsWith(".jpeg")) {
            target = new File(target.getPath() + ".png");
        }
        if (!ImageIO.write(img, format, target)) {
            throw new IOException("No writer available for format " + format);
        }
        return target;
    }
}
