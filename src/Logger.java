import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class Logger {
    private static final DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss");

    private static final String INFO = "INFO";
    private static final String WARN = "WARN";
    private static final String DEBUG = "DEBUG";
    private static final String ERROR = "ERROR";

    public void info(String message) {
        log(INFO, message);
    }

    public void warn(String message) {
        log(WARN, message);
    }

    public void debug(String message) {
        log(DEBUG, message);
    }

    public void error(String message) {
        log(ERROR, message);
    }

    private static void log(String level, String message) {
        String timestamp = LocalDateTime.now().format(dateTimeFormatter);
        if (level.equals(ERROR)) {
            System.err.println(timestamp + " [" + level + "] " + message);
        } else {
            System.out.println(timestamp + " [" + level + "] " + message);
        }
    }
}
