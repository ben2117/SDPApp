package model;

/**
 * Created by ben on 8/10/2016.
 */
public class BookingLine {

    private String sessionID;
    private String studentID;
    private String alarmType;
    private String bookingID;

    public BookingLine() {};

    public BookingLine(String _sessionID, String _studentID, String _alarmType) {
        sessionID = _sessionID;
        studentID = _studentID;
        alarmType = _alarmType;
    };

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

    public String getAlarmType() {
        return alarmType;
    }

    public void setAlarmType(String alarmType) {
        this.alarmType = alarmType;
    }

    public String getBookingID() {
        return bookingID;
    }

    public void setBookingID(String bookingID) {
        this.bookingID = bookingID;
    }
}
