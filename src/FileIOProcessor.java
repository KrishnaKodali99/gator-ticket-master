import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class FileIOProcessor {
    private static final String INITIALIZE_REGEX = "Initialize\\((\\d+)\\)\\s*$";
    private static final String AVAILABLE_REGEX = "Available\\(\\)\\s*$";
    private static final String RESERVE_REGEX = "Reserve\\((\\d+),\\s*(\\d+)\\)\\s*$";
    private static final String CANCEL_REGEX = "Cancel\\((\\d+),\\s*(\\d+)\\)\\s*$";
    private static final String EXIT_WAITLIST_REGEX = "ExitWaitlist\\((\\d+)\\)\\s*$";
    private static final String UPDATE_PRIORITY_REGEX = "UpdatePriority\\((\\d+),\\s*(\\d+)\\)\\s*$";
    private static final String ADD_SEATS_REGEX = "AddSeats\\((\\d+)\\)\\s*$";
    private static final String PRINT_RESERVATIONS_REGEX = "PrintReservations\\(\\)\\s*$";
    private static final String RELEASE_SEATS_REGEX = "ReleaseSeats\\((\\d+),\\s*(\\d+)\\)\\s*$";
    private static final String QUIT_REGEX = "Quit\\(\\)\\s*$";

    private final Logger logger;
    private final InputActionsHandler inputActionsHandler;

    public FileIOProcessor() {
        logger = new Logger();
        inputActionsHandler = new InputActionsHandler();
    }

    /**
     * Process the inputs from the file, collect the responses, and write them to the corresponding output file.
     *
     * @param fileNamePath path of the input file
     */
    public void processFile(String fileNamePath) {
        logger.info("Started processing inputs for Gator Ticket Master.");

        List<String> responses = this.readFile(fileNamePath);
        this.writeFile(fileNamePath, responses);

        logger.info("Completed processing tickets.");
    }

    // Read file from the file path and process accordingly.
    private List<String> readFile(String fileNamePath) {
        logger.info("Reading from input file: " + fileNamePath);

        Path filePath = Paths.get(fileNamePath);
        List<String> responses = new ArrayList<>();

        try (Stream<String> lines = Files.lines(filePath)) {
            List<String> inputLines = lines.collect(Collectors.toList());
            for (String line : inputLines) {
                if (line.matches(QUIT_REGEX)) {
                    responses.add(inputActionsHandler.quit());
                    break;
                }
                responses.addAll(this.processLine(line));
            }
        } catch (IOException exception) {
            logger.error("Error reading the file: " + exception);
        }

        return responses;
    }

    // Write the responses collected to the output file.
    private void writeFile(String fileNamePath, List<String> responses) {
        String[] fileDetails = fileNamePath.split("\\.", 2);
        String outputFileNamePath = fileDetails[0] + "_output_file." + ((Objects.isNull(fileDetails[1])) ? ".txt" : fileDetails[1]);

        logger.info("Writing responses into output file: " + outputFileNamePath);

        try {
            Files.write(Paths.get(outputFileNamePath), responses);
            System.out.println("Data successfully written to the file");
        } catch (IOException exception) {
            System.err.println("Error writing to file: " + exception);
        }
    }

    // Process line as per the matched regex.
    private List<String> processLine(String line) {
        List<String> responseStrings = new ArrayList<>();

        if (line.matches(INITIALIZE_REGEX)) {
            Matcher matcher = this.getMatcher(INITIALIZE_REGEX, line);
            if (matcher.find()) {
                int seatCount = Integer.parseInt(matcher.group(1));
                responseStrings.add(inputActionsHandler.initialize(seatCount));
            }
        } else if (line.matches(AVAILABLE_REGEX)) {
            responseStrings.add(inputActionsHandler.available());
        } else if (line.matches(RESERVE_REGEX)) {
            Matcher matcher = this.getMatcher(RESERVE_REGEX, line);
            if (matcher.find()) {
                int userId = Integer.parseInt(matcher.group(1));
                int userPriority = Integer.parseInt(matcher.group(2));
                responseStrings.add(inputActionsHandler.reserve(userId, userPriority));
            }
        } else if (line.matches(CANCEL_REGEX)) {
            Matcher matcher = this.getMatcher(CANCEL_REGEX, line);
            if (matcher.find()) {
                int seatId = Integer.parseInt(matcher.group(1));
                int userId = Integer.parseInt(matcher.group(2));
                responseStrings.addAll(inputActionsHandler.cancel(seatId, userId));
            }
        } else if (line.matches(EXIT_WAITLIST_REGEX)) {
            Matcher matcher = this.getMatcher(EXIT_WAITLIST_REGEX, line);
            if (matcher.find()) {
                int userId = Integer.parseInt(matcher.group(1));
                responseStrings.add(inputActionsHandler.exitWaitlist(userId));
            }
        } else if (line.matches(UPDATE_PRIORITY_REGEX)) {
            Matcher matcher = this.getMatcher(UPDATE_PRIORITY_REGEX, line);
            if (matcher.find()) {
                int userId = Integer.parseInt(matcher.group(1));
                int userPriority = Integer.parseInt(matcher.group(2));
                responseStrings.add(inputActionsHandler.updatePriority(userId, userPriority));
            }
        } else if (line.matches(ADD_SEATS_REGEX)) {
            Matcher matcher = this.getMatcher(ADD_SEATS_REGEX, line);
            if (matcher.find()) {
                int seatCount = Integer.parseInt(matcher.group(1));
                responseStrings.addAll(inputActionsHandler.addSeats(seatCount));
            }
        } else if (line.matches(PRINT_RESERVATIONS_REGEX)) {
            responseStrings.addAll(inputActionsHandler.printReservations());
        } else if (line.matches(RELEASE_SEATS_REGEX)) {
            Matcher matcher = this.getMatcher(RELEASE_SEATS_REGEX, line);
            if (matcher.find()) {
                int userId1 = Integer.parseInt(matcher.group(1));
                int userId2 = Integer.parseInt(matcher.group(2));
                responseStrings.addAll(inputActionsHandler.releaseSeats(userId1, userId2));
            }
        }

        return responseStrings;
    }

    // Returns a Matcher object for the given regex and text.
    private Matcher getMatcher(String regex, String text) {
        Pattern pattern = Pattern.compile(regex);
        return pattern.matcher(text);
    }
}
