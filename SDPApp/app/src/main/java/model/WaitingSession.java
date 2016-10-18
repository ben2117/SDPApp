package model;

/**
 * Created by kisungtae on 11/10/2016.
 */
public class WaitingSession {

    private String waitingSessionID;
    private String sessionID;
    private String studentID;
    private long queuePosition;
    private String sessionName;

    public String getSessionID() {
        return sessionID;
    }

    public void setSessionID(String sessionID) {
        this.sessionID = sessionID;
    }

    public String getStudentID() {
        return studentID;
    }

    public void setStudentID(String studentID) {
        this.studentID = studentID;
    }

    public long getQueuePosition() {
        return queuePosition;
    }

    public void setQueuePosition(long queuePosition) {
        this.queuePosition = queuePosition;
    }

    public String getWaitingSessionID() {
        return waitingSessionID;
    }

    public void setWaitingSessionID(String waitingSessionID) {
        this.waitingSessionID = waitingSessionID;
    }

    public String getSessionName() {
        return sessionName;
    }

    public void setSessionName(String sessionName) {
        this.sessionName = sessionName;
    }
}
