package ui;

import algorithm.ColorCalculator;
import algorithm.DelaunayTriangulator;
import algorithm.PointSelector;
import algorithm.ProgressCallback;
import model.ArtResult;
import model.Triangle;
import util.RunLogger;

import javax.swing.SwingWorker;
import java.awt.Point;
import java.awt.image.BufferedImage;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.function.Consumer;

/**
 * MULTI-THREADING: the heavy work runs on a background thread so the window never freezes.
 * Extends SwingWorker, reports progress to the progress bar and can be cancelled.
 */
public class GenerateWorker extends SwingWorker<ArtResult, Void> implements ProgressCallback {
    private final BufferedImage image;
    private final PointSelector selector;      // polymorphic: any subclass works here
    private final int pointCount;
    private final Consumer<ArtResult> onSuccess;
    private final Consumer<String> onError;
    private final Runnable onCancel;

    public GenerateWorker(BufferedImage image, PointSelector selector, int pointCount,
                          Consumer<ArtResult> onSuccess, Consumer<String> onError, Runnable onCancel) {
        this.image = image;
        this.selector = selector;
        this.pointCount = pointCount;
        this.onSuccess = onSuccess;
        this.onError = onError;
        this.onCancel = onCancel;
    }

    @Override
    protected ArtResult doInBackground() throws Exception {
        long start = System.currentTimeMillis();
        int w = image.getWidth();
        int h = image.getHeight();

        List<Point> points = selector.select(image, pointCount);           // 0 - 10 %
        update(10);
        List<Triangle> triangles =
                new DelaunayTriangulator().triangulate(points, w, h, phase(10, 80));   // 10 - 80 %
        new ColorCalculator().colorize(image, points, triangles, phase(80, 100));      // 80 - 100 %
        update(100);

        RunLogger.log(selector.getName() + ": " + points.size() + " points, " + triangles.size()
                + " triangles, " + (System.currentTimeMillis() - start) + " ms");
        return new ArtResult(w, h, points, triangles);
    }

    @Override
    protected void done() {
        if (isCancelled()) {
            onCancel.run();
            return;
        }
        try {
            onSuccess.accept(get());
        } catch (ExecutionException e) {
            Throwable cause = e.getCause();
            if (cause instanceof exceptions.ProcessingCancelledException) {
                onCancel.run();
            } else {
                onError.accept(cause.getMessage() == null ? cause.toString() : cause.getMessage());
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            onCancel.run();
        }
    }

    @Override
    public void update(int percent) {
        setProgress(Math.max(0, Math.min(100, percent)));
    }

    /** Maps a 0-100 % step of one phase onto the overall progress bar range. */
    private ProgressCallback phase(final int from, final int to) {
        return new ProgressCallback() {
            @Override
            public void update(int percent) {
                GenerateWorker.this.update(from + (to - from) * percent / 100);
            }

            @Override
            public boolean isCancelled() {
                return GenerateWorker.this.isCancelled();
            }
        };
    }
}
