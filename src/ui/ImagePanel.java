package ui;

import javax.swing.BorderFactory;
import javax.swing.JPanel;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Insets;
import java.awt.RenderingHints;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.util.function.Consumer;

/** Shows an image scaled to fit. Clicking a pixel reports its colour (AWT mouse events). */
public class ImagePanel extends JPanel {
    private BufferedImage image;
    private double scale = 1;
    private int offsetX;
    private int offsetY;
    private Consumer<Color> pixelListener;

    public ImagePanel(String title) {
        setBorder(BorderFactory.createTitledBorder(title));
        setBackground(new Color(40, 40, 40));
        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (image == null || pixelListener == null) return;
                int ix = (int) ((e.getX() - offsetX) / scale);
                int iy = (int) ((e.getY() - offsetY) / scale);
                if (ix >= 0 && iy >= 0 && ix < image.getWidth() && iy < image.getHeight()) {
                    pixelListener.accept(new Color(image.getRGB(ix, iy)));
                }
            }
        });
    }

    public void setImage(BufferedImage image) {
        this.image = image;
        repaint();
    }

    public void setPixelListener(Consumer<Color> listener) {
        this.pixelListener = listener;
    }

    @Override
    protected void paintComponent(Graphics g0) {
        super.paintComponent(g0);
        if (image == null) return;
        Graphics2D g = (Graphics2D) g0;
        g.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
        Insets in = getInsets();
        int aw = getWidth() - in.left - in.right;
        int ah = getHeight() - in.top - in.bottom;
        scale = Math.min((double) aw / image.getWidth(), (double) ah / image.getHeight());
        int dw = (int) (image.getWidth() * scale);
        int dh = (int) (image.getHeight() * scale);
        offsetX = in.left + (aw - dw) / 2;
        offsetY = in.top + (ah - dh) / 2;
        g.drawImage(image, offsetX, offsetY, dw, dh, null);
    }
}
