import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

public class InputActionsHandler {
    private static final Integer WAITLIST_SIZE = 10000;
    private static final String INVALID_INPUT = "Invalid input. Please provide a valid number of seats.";

    private final BinaryMinHeap<Integer> availableSeatsList;
    private final BinaryMinHeap<User> usersWaitList;
    private final RBTreeMap userReservationMap;
    private int availableSeats;

    public InputActionsHandler() {
        this.availableSeats = 0;
        this.usersWaitList = new BinaryMinHeap<>(WAITLIST_SIZE);
        this.availableSeatsList = new BinaryMinHeap<>(WAITLIST_SIZE);
        this.userReservationMap = new RBTreeMap();
    }

    /**
     * Initialize the system with the specified number of seats.
     *
     * @param seatCount count of seats to initialize
     * @return response
     */
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

    /**
     * Get the number of available seats and the length of the waitlist.
     *
     * @return response
     */
    public String available() {
        return String.format("Total Seats Available : %d, Waitlist : %d", this.availableSeatsList.size(), this.usersWaitList.size());
    }

    /**
     * Reserve a seat for a user or add them to the waitlist.
     *
     * @param userId       ID of the user.
     * @param userPriority Priority associated with the `userId`
     * @return response
     */
    public String reserve(Integer userId, Integer userPriority) {
        if (this.availableSeatsList.isEmpty()) {
            this.usersWaitList.insert(new User(userId, userPriority, System.nanoTime()));
            return String.format("User %d is added to the waiting list", userId);
        }
        int seatId = this.availableSeatsList.extractMin();
        this.userReservationMap.put(userId, seatId);
        return String.format("User %d reserved seat %d", userId, seatId);
    }

    /**
     * Cancel a user's reservation and reassign the seat to the waitlist.
     *
     * @param seatId ID of the seat.
     * @param userId ID of the user.
     * @return response
     */
    public List<String> cancel(Integer seatId, Integer userId) {
        List<String> responses = new ArrayList<>();
        Integer assignedSeatId = this.userReservationMap.get(userId);

        if (Objects.isNull(assignedSeatId)) {
            responses.add(String.format("User %d has no reservation to cancel", userId));
        } else if (!Objects.equals(assignedSeatId, seatId)) {
            responses.add(String.format("User %d has no reservation for seat %d to cancel", userId, seatId));
        } else {
            assignedSeatId = this.userReservationMap.remove(userId);
            responses.add(String.format("User %d canceled their reservation", userId));
            if (!this.usersWaitList.isEmpty()) {
                User user = this.usersWaitList.extractMin();
                if (Objects.nonNull(this.userReservationMap.putIfAbsent(user.getUserId(), assignedSeatId))) {
                    responses.add(String.format("User %d reserved seat %d", user.getUserId(), assignedSeatId));
                }
            } else {
                this.availableSeatsList.insert(seatId);
            }
        }
        return responses;
    }

    /**
     * Remove a user from the waitlist.
     *
     * @param userId ID of the user.
     * @return response
     */
    public String exitWaitlist(int userId) {
        if (Objects.nonNull(this.usersWaitList.removeElement(new User(userId)))) {
            return String.format("User %d is removed from the waiting list", userId);
        }

        return String.format("User %d is not in waitlist", userId);
    }

    /**
     * Update the priority of a user in the waitlist.
     *
     * @param userId       ID of the user.
     * @param userPriority priority of the user
     * @return response
     */
    public String updatePriority(int userId, int userPriority) {
        Map.Entry<Integer, User> userEntry = this.usersWaitList.getElementAndIndex(new User(userId));
        if (Objects.isNull(userEntry)) {
            return String.format("User %d priority is not updated", userId);

        }
        this.usersWaitList.updateElement(userEntry.getKey(), new User(userId, userPriority, userEntry.getValue().getTimestamp()));
        return String.format("User %d priority has been updated to %d", userId, userPriority);
    }

    /**
     * Add more seats to the available seat list.
     *
     * @param seatCount number of seats to be added
     * @return response
     */
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

    /**
     * Print all reserved seats and the users they are assigned to
     *
     * @return response
     */
    public List<String> printReservations() {
        Set<Map.Entry<Integer, Integer>> reservationDetailsSet = this.userReservationMap.entrySet();
        return reservationDetailsSet.stream().sorted(Map.Entry.comparingByValue()).map(setEntry -> String.format("Seat %d, User %d", setEntry.getValue(), setEntry.getKey())).collect(Collectors.toList());
    }

    /**
     * Release seats for users in a specified user ID range and remove them from the waitlist
     *
     * @param userId1 starting value of range
     * @param userId2 ending value of range
     * @return response
     */
    public List<String> releaseSeats(Integer userId1, Integer userId2) {
        List<String> responses = new ArrayList<>();
        if (userId1 > userId2 || userId2 <= 0) {
            responses.add("Invalid input. Please provide a valid range of users.");
        } else {
            for (int userId = userId1; userId <= userId2; userId++) {
                if (!userReservationMap.isEmpty()) {
                    Integer seatId = this.userReservationMap.remove(userId);
                    if (Objects.nonNull(seatId)) {
                        this.availableSeatsList.insert(seatId);
                    }
                }
                if (!usersWaitList.isEmpty()) {
                    this.usersWaitList.removeElement(new User(userId));
                }
            }
            if (this.usersWaitList.isEmpty()) {
                responses.add(String.format("Reservations/waitlist of the users in the range [%d, %d] have been released", userId1, userId2));
            } else {
                responses.add(String.format("Reservations of the Users in the range [%d, %d] are released", userId1, userId2));
                while (!this.usersWaitList.isEmpty() && !this.availableSeatsList.isEmpty()) {
                    User user = this.usersWaitList.extractMin();
                    Integer seatId = this.availableSeatsList.extractMin();

                    if (Objects.nonNull(seatId)) {
                        this.userReservationMap.put(user.getUserId(), seatId);
                        responses.add(String.format("User %d reserved seat %d", user.getUserId(), seatId));
                    }
                }
            }
        }
        return responses;
    }

    public String quit() {
        return "Program Terminated!!";
    }
}
