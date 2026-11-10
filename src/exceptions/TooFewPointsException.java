package exceptions;

/** Custom checked exception: thrown when too few points are requested to build a triangulation. */
public class TooFewPointsException extends Exception {
    public TooFewPointsException(String message) {
        super(message);
    }
}
