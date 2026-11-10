package exceptions;

/** Custom checked exception: thrown when the chosen file is missing or is not a valid image. */
public class InvalidImageException extends Exception {
    public InvalidImageException(String message) {
        super(message);
    }

    public InvalidImageException(String message, Throwable cause) {
        super(message, cause);
    }
}
