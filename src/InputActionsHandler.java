import java.util.List;

public class InputActionsHandler {
    private BinaryMinHeap availableSeatsList;

    private BinaryMinHeap usersWaitList;

    private RBTreeMap userReservationMap;

    private int availableSeats;

    private static final Integer WAITLIST_SIZE = 10000;

    public InputActionsHandler() {
        availableSeats = 0;
        usersWaitList = new BinaryMinHeap(WAITLIST_SIZE);
    }

    public String initialize(int seatCount) {
        availableSeatsList = new BinaryMinHeap(seatCount);
        for (int seatNumber = 1; seatNumber <= seatCount; seatNumber++) {
            availableSeatsList.insert(seatNumber);
        }
        availableSeats += seatCount;
        return String.format("%s Seats are made available for reservation", seatCount);
    }

    public String available() {
        return String.format("Total Seats Available : %d, Waitlist : %d", availableSeatsList.size(), usersWaitList.size());
    }

    public List<String> addSeats(int seatCount) {
        for (int seatNumber = availableSeats + 1; seatNumber <= availableSeats + seatCount; seatNumber++) {
            this.availableSeatsList.insert(seatNumber);
        }
        return List.of(String.format("Additional %d Seats are made available for reservation", seatCount));
    }
}
