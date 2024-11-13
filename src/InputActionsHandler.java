import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

public class InputActionsHandler {
    private final BinaryMinHeap<Integer> availableSeatsList;

    private final BinaryMinHeap<User> usersWaitList;

    private final RBTreeMap userReservationMap;

    private int availableSeats;

    private static final Integer WAITLIST_SIZE = 10000;

    private static final String INVALID_INPUT = "Invalid input. Please provide a valid number of seats.";

    public InputActionsHandler() {
        this.availableSeats = 0;
        this.usersWaitList = new BinaryMinHeap<>(WAITLIST_SIZE);
        this.availableSeatsList = new BinaryMinHeap<>(WAITLIST_SIZE);
        this.userReservationMap = new RBTreeMap();
    }

    public String initialize(int seatCount) {
        if (seatCount < 0 || seatCount > WAITLIST_SIZE) {
            return INVALID_INPUT;
        }
        for (int seatNumber = 1; seatNumber <= seatCount; seatNumber++) {
            this.availableSeatsList.insert(seatNumber);
        }
        this.availableSeats += seatCount;
        return String.format("%d Seats are made available for reservation", seatCount);
    }

    public String available() {
        return String.format("Total Seats Available : %d, Waitlist : %d", this.availableSeatsList.size(), this.usersWaitList.size());
    }

    public String reserve(Integer userId, Integer userPriority) {
        if (this.availableSeatsList.isEmpty()) {
            this.usersWaitList.insert(new User(userId, userPriority, System.nanoTime()));
            return String.format("User %d is added to the waiting list", userId);
        }
        int seatId = this.availableSeatsList.extractMin();
        this.userReservationMap.put(userId, seatId);
        return String.format("User %d reserved seat %d", userId, seatId);
    }

    public List<String> cancel(Integer seatId, Integer userId) {
        List<String> responses = new ArrayList<>();
        Integer cancelledSeatId = this.userReservationMap.remove(userId);

        if (Objects.isNull(cancelledSeatId)) {
            responses.add(String.format("User %d has no reservation to cancel", userId));
        } else if (!Objects.equals(cancelledSeatId, seatId)) {
            responses.add(String.format("User %d has no reservation for seat %d to cancel", userId, seatId));
        } else {
            responses.add(String.format("User %d canceled their reservation", userId));
            if (!this.usersWaitList.isEmpty()) {
                User user = this.usersWaitList.extractMin();
                if (Objects.nonNull(this.userReservationMap.putIfAbsent(user.getUserId(), seatId))) {
                    responses.add(String.format("User %d reserved seat %d", user.getUserId(), seatId));
                }
            } else {
                this.availableSeatsList.insert(seatId);
            }
        }
        return responses;
    }

    public String exitWaitlist(int userId) {
        if (Objects.nonNull(this.usersWaitList.removeElement(new User(userId)))) {
            return String.format("User %d is removed from the waiting list", userId);
        }
        return String.format("User %d is not in waitlist", userId);
    }

    public String updatePriority(int userId, int userPriority) {
        Map.Entry<Integer, User> userEntry = this.usersWaitList.getElementAndIndex(new User(userId));
        if (Objects.isNull(userEntry)) {
            return String.format("User %d priority is not updated", userId);

        }
        this.usersWaitList.updateElement(userEntry.getKey(), new User(userId, userPriority, userEntry.getValue().getTimestamp()));
        return String.format("User %d priority has been updated to %d", userId, userPriority);
    }

    public List<String> addSeats(int seatCount) {
        if (seatCount < 0 || seatCount > WAITLIST_SIZE) {
            return List.of(INVALID_INPUT);
        }

        int seatsAdded = 0;
        List<String> outputValues = new ArrayList<>();
        for (int seatNumber = this.availableSeats + 1; seatNumber <= this.availableSeats + seatCount; seatNumber++) {
            if (!this.availableSeatsList.insert(seatNumber)) {
                break;
            }
            seatsAdded++;
        }
        this.availableSeats += seatsAdded;
        outputValues.add(String.format("Additional %d Seats are made available for reservation", seatsAdded));

        while (!this.usersWaitList.isEmpty() && !this.availableSeatsList.isEmpty()) {
            int seatId = this.availableSeatsList.extractMin();
            Integer userId = this.usersWaitList.extractMin().getUserId();
            this.userReservationMap.put(userId, seatId);
            outputValues.add(String.format("User %d reserved seat %d", userId, seatId));
        }

        return outputValues;
    }

    public List<String> printReservations() {
        Set<Map.Entry<Integer, Integer>> reservationDetailsSet = this.userReservationMap.entrySet();
        return reservationDetailsSet.stream()
                .sorted(Map.Entry.comparingByValue())
                .map(setEntry -> String.format("Seat %d, User %d", setEntry.getValue(), setEntry.getKey())).collect(Collectors.toList());
    }

    public String quit() {
        return "Program Terminated!!";
    }
}
