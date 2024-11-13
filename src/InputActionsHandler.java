import java.util.List;
import java.util.Objects;

public class InputActionsHandler {
    private BinaryMinHeap availableSeatsList;

    private BinaryMinHeap usersWaitList;

    private RBTreeMap userReservationMap;

    private int availableSeats;

    private static final Integer WAITLIST_SIZE = 100000;

    public InputActionsHandler() {
        availableSeats = 0;
        usersWaitList = new BinaryMinHeap(WAITLIST_SIZE);
        userReservationMap = new RBTreeMap();
    }

    public String initialize(int seatCount) {
        availableSeatsList = new BinaryMinHeap(seatCount);
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
        usersWaitList.insert(WAITLIST_SIZE - userPriority);
        return String.format("User %d is added to the waiting list", userId);
    }

    public String cancel(Integer seatId, Integer userId) {
        return "";
    }

    public String exitWaitlist(int userId) {
        if(Objects.nonNull(this.usersWaitList.removeElement(userId))) {
            return String.format("User %d is removed from the waiting list", userId);
        }
        return String.format("User %d is not in waitlist", userId);
    }

    public List<String> addSeats(int seatCount) {
        int seatNumber;
        for (seatNumber = availableSeats + 1; seatNumber <= availableSeats + seatCount; seatNumber++) {
            if(!this.availableSeatsList.insert(seatNumber)) {
                break;
            }
        }
        return List.of(String.format("Additional %d Seats are made available for reservation", seatCount));
    }
}
