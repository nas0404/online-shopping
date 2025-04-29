package uts.isd.model;

public class Logs {

    private int UserID;
    private String ActivityTime; // properties
    private String ActivityType;

    public Logs() {

    }

    public Logs(int UserID, String ActivityTime, String ActivityType) {
        this.UserID = UserID;
        this.ActivityTime = ActivityTime;
        this.ActivityType = ActivityType; // constructor
    }

    // just all getters and setters
    public int getUserID() {
        return UserID;
    }

    public void setUserID(int userID) {
        UserID = userID;
    }

    public String getActivityTime() {
        return ActivityTime;
    }

    public void setActivityTime(String activityTime) {
        ActivityTime = activityTime;
    }

    public String getActivityType() {
        return ActivityType;
    }

    public void setActivityType(String activityType) {
        ActivityType = activityType;
    }

}
