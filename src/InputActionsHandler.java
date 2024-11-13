import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

public class InputActionsHandler {
    private BinaryMinHeap<Integer> availableSeatsList;

    private BinaryMinHeap<User> usersWaitList;

    private RBTreeMap userReservationMap;

    private int availableSeats;

    private static final Integer WAITLIST_SIZE = 10000;

    private static final String INVALID_INPUT = "Invalid input. Please provide a valid number of seats.";

    public InputActionsHandler() {
        availableSeats = 0;
        usersWaitList = new BinaryMinHeap<>(WAITLIST_SIZE);
        availableSeatsList = new BinaryMinHeap<>(WAITLIST_SIZE);
        userReservationMap = new RBTreeMap();
    }

    public String initialize(int seatCount) {
        if (seatCount < 0 || seatCount > WAITLIST_SIZE) {
            return INVALID_INPUT;
        }
        for (int seatNumber = 1; seatNumber <= seatCount; seatNumber++) {
            availableSeatsList.insert(seatNumber);
        }
        availableSeats += seatCount;
        return String.format("%d Seats are made available for reservation", seatCount);
    }

    public String available() {
        return String.format("Total Seats Available : %d, Waitlist : %d", availableSeatsList.size(), usersWaitList.size());
    }

    public String reserve(Integer userId, Integer userPriority) {
        if (!availableSeatsList.isEmpty()) {
            int seatId = availableSeatsList.extractMin();
            userReservationMap.put(userId, seatId);
            return String.format("User %d reserved seat %d", userId, seatId);
        }
        usersWaitList.insert(new User(userId, userPriority, System.nanoTime()));
        return String.format("User %d is added to the waiting list", userId);
    }

    public List<String> cancel(Integer seatId, Integer userId) {
        return List.of("");
    }

    public String exitWaitlist(int userId) {
        if (Objects.nonNull(this.usersWaitList.removeElement(new User(userId)))) {
            return String.format("User %d is removed from the waiting list", userId);
        }
        return String.format("User %d is not in waitlist", userId);
    }

    public List<String> addSeats(int seatCount) {
        if (seatCount < 0 || seatCount > WAITLIST_SIZE) {
            return List.of(INVALID_INPUT);
        }

        int seatsAdded = 0;

        List<String> outputValues = new ArrayList<>();
        for (int seatNumber = availableSeats + 1; seatNumber <= availableSeats + seatCount; seatNumber++) {
            if (!this.availableSeatsList.insert(seatNumber)) {
                break;
            }
            seatsAdded++;
        }
        availableSeats += seatsAdded;
        outputValues.add(String.format("Additional %d Seats are made available for reservation", seatsAdded));

        while (!usersWaitList.isEmpty() && !availableSeatsList.isEmpty()) {
            int seatId = availableSeatsList.extractMin();
            Integer userId = usersWaitList.extractMin().getUserId();
            userReservationMap.put(userId, seatId);
            outputValues.add(String.format("User %d reserved seat %d", userId, seatId));
        }

        return outputValues;
    }

    public List<String> printReservations() {
        Set<Map.Entry<Integer, Integer>> reservationDetailsSet = userReservationMap.entrySet();
        return reservationDetailsSet.stream()
                .map(setEntry -> String.format("[%d, %d]", setEntry.getValue(), setEntry.getKey()))
                .collect(Collectors.toList());
    }

    public String quit() {
        return "Program Terminated!!";
    }
}
