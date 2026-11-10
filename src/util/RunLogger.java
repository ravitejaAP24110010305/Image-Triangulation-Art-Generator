package util;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.time.LocalDateTime;

/** File I/O: appends one line per generation run to triangulation_log.txt. */
public final class RunLogger {
    private static final String LOG_FILE = "triangulation_log.txt";

    private RunLogger() { }

    public static synchronized void log(String message) {
        try (PrintWriter out = new PrintWriter(new FileWriter(LOG_FILE, true))) {
            out.println(LocalDateTime.now() + "  " + message);
        } catch (IOException e) {
            System.err.println("Could not write log: " + e.getMessage());
        }
    }
}
