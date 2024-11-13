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
    private static final String CANCEL_REGEX = "Cancel\\((\\d+),\\s*(\\d+)\\)\\s*$";
    private static final String EXIT_WAITLIST_REGEX = "ExitWaitlist\\((\\d+)\\)\\s*$";
    private static final String QUIT_REGEX = "Quit\\(\\)\\s*$";
    private static final String ADD_SEATS_REGEX = "AddSeats\\((\\d+)\\)\\s*$";

    public FileIOProcessor() {
        logger = new Logger();
        inputActionsHandler = new InputActionsHandler();
    }

    public void processFile(String fileNamePath) {
        logger.info("Started processing inputs for Gator Ticket Master.");

        List<String> outputLines = this.readFile(fileNamePath);
        this.writeFile(fileNamePath, outputLines);

        logger.info("Completed processing tickets.");
    }

    private List<String> readFile(String fileNamePath) {
        logger.info("Reading from input file: " + fileNamePath);

        Path filePath = Paths.get(fileNamePath);
        List<String> outputLines = new ArrayList<>();

        try (Stream<String> lines = Files.lines(filePath)) {
            List<String> inputLines = lines.collect(Collectors.toList());
            for(String line: inputLines) {
                if(line.matches(QUIT_REGEX)) {
                    outputLines.add("Program Terminated!!");
                    break;
                }
                outputLines.add(this.processLine(line));
            }
        } catch (IOException exception) {
            logger.error("Error reading the file: " + exception);
        }

        return outputLines;
    }

    private void writeFile(String fileNamePath, List<String> outputLines) {
        String[] fileDetails = fileNamePath.split("\\.", 2);
        String outputFileNamePath = fileDetails[0] + "_output_file." + fileDetails[1];

        try {
            // Write the list of strings to the file
            Files.write(Paths.get(outputFileNamePath), outputLines);
            System.out.println("Data written to file successfully.");
        } catch (IOException e) {
            System.err.println("Error writing to file: " + e.getMessage());
        }

        logger.info("Writing into output file: " + outputFileNamePath);

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
                return inputActionsHandler.reserve(userId, userPriority);
            }
        } else if (line.matches(CANCEL_REGEX)) {
            Matcher matcher = this.getMatcher(CANCEL_REGEX, line);
            if (matcher.find()) {
                int seatId = Integer.parseInt(matcher.group(1));
                int userId = Integer.parseInt(matcher.group(2));
                return inputActionsHandler.cancel(seatId, userId);
            }
        } else if(line.matches(EXIT_WAITLIST_REGEX)) {
            Matcher matcher = this.getMatcher(EXIT_WAITLIST_REGEX, line);
            if (matcher.find()) {
                int userId = Integer.parseInt(matcher.group(1));
                output = inputActionsHandler.exitWaitlist(userId);
            }
        }
        else if(line.matches(ADD_SEATS_REGEX)) {
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
