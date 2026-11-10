package exceptions;

/** Custom checked exception: thrown inside the worker thread when the user presses Cancel. */
public class ProcessingCancelledException extends Exception {
    public ProcessingCancelledException(String message) {
        super(message);
    }
}
