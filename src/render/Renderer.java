package render;

import model.ArtResult;

import java.awt.image.BufferedImage;

/** Interface: anything that can turn an ArtResult into a picture. */
public interface Renderer {
    BufferedImage render(ArtResult result);

    String getName();
}
