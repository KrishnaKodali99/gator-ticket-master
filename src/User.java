public class User implements Comparable<User> {
    private final int userId;

    private int userPriority;

    private long timestamp;

    public User(int userId) {
        this.userId = userId;
    }

    public User(int userId, int userPriority, long timestamp) {
        this.userId = userId;
        this.userPriority = userPriority;
        this.timestamp = timestamp;
    }

    public int getUserId() {
        return this.userId;
    }

    public long getTimestamp() {
        return this.timestamp;
    }

    @Override
    public boolean equals(Object object) {
        if (this == object) return true;
        if (object == null || getClass() != object.getClass()) return false;

        User user = (User) object;
        return userId == user.userId;
    }

    @Override
    public int compareTo(User comparedUser) {
        return this.compare(this, comparedUser);
    }

    private int compare(User user1, User user2) {
        if (user1.userPriority == user2.userPriority) {
            return Long.compare(user1.timestamp, user2.timestamp);
        }
        return (user1.userPriority > user2.userPriority) ? -1 : 1;
    }
}
