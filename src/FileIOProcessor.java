import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class FileIOProcessor {
    private final Logger logger;
    private final InputActionsHandler inputActionsHandler;

    private static final String INITIALIZE_REGEX = "Initialize\\((\\d+)\\)\\s*$";
    private static final String AVAILABLE_REGEX = "Available\\(\\)\\s*$";
    private static final String RESERVE_REGEX = "Reserve\\((\\d+),\\s*(\\d+)\\)\\s*$";
    private static final String QUIT_REGEX = "Quit\\(\\)\\s*$";
    private static final String ADD_SEATS_REGEX = "AddSeats\\((\\d+)\\)\\s*$";

    public FileIOProcessor() {
        logger = new Logger();
        inputActionsHandler = new InputActionsHandler();
    }

    public void processFile(String fileName) {
        logger.info("Processing file: " + fileName);

        List<String> outputs = this.readFile(fileName);
        this.writeFile(outputs);
    }

    private List<String> readFile(String fileName) {
        logger.info("Started reading file: " + fileName);

        Path filePath = Paths.get(fileName);
        List<String> outputs = new ArrayList<>();

        try (Stream<String> lines = Files.lines(filePath)) {
            List<String> inputLines = lines.collect(Collectors.toList());
            for(String line: inputLines) {
                if(line.matches(QUIT_REGEX)) {
                    outputs.add("Program Terminated!!");
                    break;
                }
                outputs.add(this.processLine(line));
            }
        } catch (IOException exception) {
            logger.error("Error reading the file: " + exception);
        }

        return outputs;
    }

    private void writeFile(List<String> outputs) {

    }

    private String processLine(String line) {
        String output = "";

        if (line.matches(INITIALIZE_REGEX)) {
            Matcher matcher = this.getMatcher(INITIALIZE_REGEX, line);
            if (matcher.find()) {
                int seatCount = Integer.parseInt(matcher.group(1));
                output = inputActionsHandler.initialize(seatCount);
            }
        } else if (line.matches(AVAILABLE_REGEX)) {
            return inputActionsHandler.available();
        } else if (line.matches(RESERVE_REGEX)) {
            Matcher matcher = this.getMatcher(RESERVE_REGEX, line);
            if (matcher.find()) {
                int userId = Integer.parseInt(matcher.group(1));
                int userPriority = Integer.parseInt(matcher.group(2));
            }
        } else if(line.matches(ADD_SEATS_REGEX)) {
            Matcher matcher = this.getMatcher(ADD_SEATS_REGEX, line);
            if (matcher.find()) {
                int seatCount = Integer.parseInt(matcher.group(1));
                output = inputActionsHandler.initialize(seatCount);
            }
        }

        return output;
    }

    private Matcher getMatcher(String regex, String text) {
        Pattern pattern = Pattern.compile(regex);
        return pattern.matcher(text);
    }
}
