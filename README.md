# Gator Ticket Master
### COP 5536 Fall 2024 - Programming Project

## Project Overview

**Gator Ticket Master** is a seat booking system specifically designed for Gator Events. It manages seat allocation, priority-based reservation, and waitlist handling. The system assigns seats based on availability and user priority, ensuring fair distribution by using a Binary Min-Heap for the priority queue and Red-Black trees for mapping data structures.

## Features

- **Dynamic Seat Allocation**: Initializes an event with a specified number of seats, with options to increase seat count.
- **Seat Reservation**: Allocates the lowest available seat number to users based on priority.
- **Waitlist Management**: Users without available seats are added to a priority-based waitlist.
- **Seat Cancellation and Reassignment**: Frees seats for waitlisted users when a reservation is canceled.
- **Priority Update**: Adjusts priority for waitlisted users, preserving original reservation order.
- **Batch Seat Release**: Releases all seats for users within a specified user ID range.
- **In-Order Display**: Shows all reservations in seat order.
- **Termination**: Ends the program and outputs results.

## Data Structures

- **Red-Black Tree**: Manages reserved seat information, storing each `userID` and `seatID` as unique nodes.
- **Binary Min-Heap**: Implements a priority queue to manage user waitlist entries and another to track unassigned seats.

## Commands for Execution
Use the following command to compile and run the project JAR file:
```bash
cd gator-ticket-master

make

# Execution using make (specify the file path for input)
make run file=<file_path>

# Execution using Java (navigate to the output directory and run the program)
cd out
java GatorTicketMaster <file_name>

# Clean all the generated output files
make clean
```

### Prerequisites

This project can be compiled with any Java version after 11. During development Java **11.0.24** was used for run time.




## Input/Output Requirements

- **Input File**: Contains commands to manage reservations, cancellations, and seat modifications. Provided as a command-line argument.
- **Output File**: Named `<input_file>_output_file.txt`, containing results of each command.

## Commands

- **Initialize(seatCount)**: Starts with `seatCount` available seats.
- **Available()**: Displays total available seats and waitlist size.
- **Reserve(userID, userPriority)**: Reserves a seat for a user or adds them to the waitlist if full.
- **Cancel(seatID, userID)**: Cancels a user’s reservation, reassigning the seat if there’s a waitlist.
- **ExitWaitlist(userID)**: Removes a user from the waitlist.
- **UpdatePriority(userID, userPriority)**: Updates a waitlisted user’s priority.
- **AddSeats(count)**: Adds more seats to the available seat list.
- **PrintReservations()**: Lists all reservations in seat order.
- **ReleaseSeats(userID1, userID2)**: Releases all seats for users within the specified range (inclusive).
- **Quit()**: Ends program processing and writes output to the file.

## Example

### Input
```plaintext
Initialize(5)
Available()
Reserve(1, 1)
Reserve(2, 1)
Cancel(1, 1)
Reserve(3, 1)
PrintReservations()
Quit()
```

### Output
```plaintext
5 Seats are made available for reservation
Total Seats Available : 5, Waitlist : 0
User 1 reserved seat 1
User 2 reserved seat 2
User 1 canceled their reservation
User 3 reserved seat 1
Seat 1, User 3
Seat 2, User 2
Program Terminated!!
```
---