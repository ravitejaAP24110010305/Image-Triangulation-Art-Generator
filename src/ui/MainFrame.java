package ui;

import algorithm.EdgePointSelector;
import algorithm.JitteredGridPointSelector;
import algorithm.PointSelector;
import algorithm.RandomPointSelector;
import exceptions.InvalidImageException;
import model.ArtResult;
import render.FilledRenderer;
import render.MeshRenderer;
import render.Renderer;
import util.ImageLoader;
import util.ImageSaver;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JSlider;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

/** Main window: AWT/Swing components, layout managers and event listeners. */
public class MainFrame extends JFrame {
    private static final String[] ALGORITHMS = {"Edge-based (best clarity)", "Random points", "Jittered grid"};

    private BufferedImage source;
    private ArtResult result;
    private Renderer renderer = new FilledRenderer();   // polymorphic reference
    private GenerateWorker worker;
    private long seed = System.nanoTime();

    private final ImagePanel originalPanel = new ImagePanel("Original");
    private final ImagePanel resultPanel = new ImagePanel("Triangulated art");
    private final JComboBox<String> algoBox = new JComboBox<>(ALGORITHMS);
    private final JSlider pointsSlider = new JSlider(50, 8000, 3000);
    private final JSlider edgeSlider = new JSlider(0, 100, 70);
    private final JLabel pointsLabel = new JLabel("3000");
    private final JLabel edgeLabel = new JLabel("70%");
    private final JCheckBox meshBox = new JCheckBox("Show mesh");
    private final JButton openBtn = new JButton("Open Image...");
    private final JButton genBtn = new JButton("Generate");
    private final JButton cancelBtn = new JButton("Cancel");
    private final JButton randBtn = new JButton("Randomize");
    private final JButton resetBtn = new JButton("Reset");
    private final JButton saveBtn = new JButton("Save...");
    private final JProgressBar progressBar = new JProgressBar(0, 100);
    private final JLabel status = new JLabel("Open an image from your computer to begin.");

    public MainFrame() {
        super("Image Triangulation Art Generator - Delaunay Triangulation");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLayout(new BorderLayout(6, 6));

        JPanel images = new JPanel(new GridLayout(1, 2, 6, 6));
        images.add(originalPanel);
        images.add(resultPanel);
        add(images, BorderLayout.CENTER);

        JPanel row1 = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 4));
        row1.add(openBtn);
        row1.add(new JLabel("Algorithm:"));
        row1.add(algoBox);
        row1.add(new JLabel("Points:"));
        row1.add(pointsSlider);
        row1.add(pointsLabel);
        row1.add(new JLabel("Edge focus:"));
        row1.add(edgeSlider);
        row1.add(edgeLabel);

        JPanel row2 = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 4));
        row2.add(meshBox);
        row2.add(genBtn);
        row2.add(cancelBtn);
        row2.add(randBtn);
        row2.add(resetBtn);
        row2.add(saveBtn);

        JPanel top = new JPanel(new GridLayout(2, 1));
        top.add(row1);
        top.add(row2);
        add(top, BorderLayout.NORTH);

        JPanel bottom = new JPanel(new BorderLayout(8, 0));
        progressBar.setStringPainted(true);
        bottom.add(progressBar, BorderLayout.WEST);
        bottom.add(status, BorderLayout.CENTER);
        add(bottom, BorderLayout.SOUTH);

        cancelBtn.setEnabled(false);

        // ---- event handling (AWT listeners)
        openBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                openImage();
            }
        });
        genBtn.addActionListener(e -> generate());
        cancelBtn.addActionListener(e -> {
            if (worker != null) worker.cancel(true);
        });
        randBtn.addActionListener(e -> {
            seed = System.nanoTime();
            generate();
        });
        resetBtn.addActionListener(e -> reset());
        saveBtn.addActionListener(e -> saveImage());
        meshBox.addActionListener(e -> refreshResult());
        algoBox.addActionListener(e -> edgeSlider.setEnabled(algoBox.getSelectedIndex() == 0));
        pointsSlider.addChangeListener(e -> pointsLabel.setText(String.valueOf(pointsSlider.getValue())));
        edgeSlider.addChangeListener(e -> edgeLabel.setText(edgeSlider.getValue() + "%"));

        originalPanel.setPixelListener(this::showPixel);
        resultPanel.setPixelListener(this::showPixel);

        setSize(1200, 720);
        setLocationRelativeTo(null);
    }

    // ------------------------------------------------------------------ actions

    /** The image can only be chosen through the system file explorer. */
    private void openImage() {
        JFileChooser fc = new JFileChooser();
        fc.setDialogTitle("Select an image from your computer");
        fc.setFileFilter(new FileNameExtensionFilter("Images (jpg, jpeg, png, bmp, gif)",
                "jpg", "jpeg", "png", "bmp", "gif"));
        if (fc.showOpenDialog(this) != JFileChooser.APPROVE_OPTION) return;
        File file = fc.getSelectedFile();
        try {
            source = ImageLoader.load(file);              // may throw InvalidImageException
            result = null;
            originalPanel.setImage(source);
            resultPanel.setImage(null);
            status.setText("Loaded " + file.getName() + " (" + source.getWidth() + "x" + source.getHeight() + ")");
            generate();
        } catch (InvalidImageException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Invalid image", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void generate() {
        if (source == null) {
            JOptionPane.showMessageDialog(this, "Please open an image first.", "No image",
                    JOptionPane.INFORMATION_MESSAGE);
            return;
        }
        if (worker != null && !worker.isDone()) return;
        setRunning(true);
        progressBar.setValue(0);
        status.setText("Generating...");
        final long t0 = System.currentTimeMillis();

        worker = new GenerateWorker(source, createSelector(), pointsSlider.getValue(),
                r -> {
                    result = r;
                    refreshResult();
                    setRunning(false);
                    progressBar.setValue(100);
                    status.setText("Done: " + r.getPoints().size() + " points, " + r.getTriangles().size()
                            + " triangles in " + (System.currentTimeMillis() - t0) + " ms. "
                            + "Click any pixel to see its colour.");
                },
                msg -> {
                    setRunning(false);
                    progressBar.setValue(0);
                    status.setText("Error: " + msg);
                    JOptionPane.showMessageDialog(this, msg, "Cannot generate", JOptionPane.WARNING_MESSAGE);
                },
                () -> {
                    setRunning(false);
                    progressBar.setValue(0);
                    status.setText("Cancelled.");
                });
        worker.addPropertyChangeListener(evt -> {
            if ("progress".equals(evt.getPropertyName())) {
                progressBar.setValue((Integer) evt.getNewValue());
            }
        });
        worker.execute();
    }

    /** Polymorphism: returns a PointSelector, the actual class depends on the user's choice. */
    private PointSelector createSelector() {
        switch (algoBox.getSelectedIndex()) {
            case 1:
                return new RandomPointSelector(seed);
            case 2:
                return new JitteredGridPointSelector(seed);
            default:
                return new EdgePointSelector(seed, edgeSlider.getValue());
        }
    }

    /** Polymorphism: the Renderer variable holds either a FilledRenderer or a MeshRenderer. */
    private void refreshResult() {
        if (result == null) return;
        renderer = meshBox.isSelected() ? new MeshRenderer() : new FilledRenderer();
        resultPanel.setImage(renderer.render(result));
    }

    private void reset() {
        if (worker != null && !worker.isDone()) worker.cancel(true);
        pointsSlider.setValue(3000);
        edgeSlider.setValue(70);
        algoBox.setSelectedIndex(0);
        meshBox.setSelected(false);
        result = null;
        resultPanel.setImage(null);
        progressBar.setValue(0);
        status.setText(source == null ? "Open an image from your computer to begin."
                : "Reset. Press Generate.");
    }

    private void saveImage() {
        if (result == null) {
            JOptionPane.showMessageDialog(this, "Generate the artwork first.", "Nothing to save",
                    JOptionPane.INFORMATION_MESSAGE);
            return;
        }
        JFileChooser fc = new JFileChooser();
        fc.setDialogTitle("Save artwork");
        fc.setSelectedFile(new File("triangulated_art.png"));
        if (fc.showSaveDialog(this) != JFileChooser.APPROVE_OPTION) return;
        try {
            File written = ImageSaver.save(renderer.render(result), fc.getSelectedFile());
            status.setText("Saved to " + written.getAbsolutePath());
        } catch (IOException ex) {
            JOptionPane.showMessageDialog(this, "Save failed: " + ex.getMessage(), "Error",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    private void showPixel(Color c) {
        status.setText("Clicked pixel colour: RGB(" + c.getRed() + ", " + c.getGreen() + ", " + c.getBlue() + ")");
    }

    private void setRunning(boolean running) {
        openBtn.setEnabled(!running);
        genBtn.setEnabled(!running);
        randBtn.setEnabled(!running);
        saveBtn.setEnabled(!running);
        cancelBtn.setEnabled(running);
    }
}
