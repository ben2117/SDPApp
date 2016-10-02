package model;

/**
 * Created by kisungtae on 22/09/2016.
 */
public class Booking {

    //might be easier to have these in booking database??
 //   private String sessionID;


    private String classID;
    private String studentID;
    private Boolean attended;
    private String alarmType;

    public Booking() {}

    public String getClassID() {
        return classID;
    }

    public void setClassID(String classID) {
        this.classID = classID;
    }

    public String getStudentID() {
        return studentID;
    }

    public void setStudentID(String studentID) {
        this.studentID = studentID;
    }

    public Boolean getAttended() {
        return attended;
    }

    public void setAttended(Boolean attended) {
        this.attended = attended;
    }

    public String getAlarmType() {
        return alarmType;
    }

    public void setAlarmType(String alarmType) {
        this.alarmType = alarmType;
    }
}
