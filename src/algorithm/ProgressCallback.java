package algorithm;

/** Interface used by long-running algorithms to report progress and to check for cancellation. */
public interface ProgressCallback {
    void update(int percent);

    boolean isCancelled();
}
