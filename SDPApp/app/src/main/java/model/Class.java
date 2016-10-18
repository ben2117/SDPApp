package model;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by kisungtae on 25/09/2016.
 */
public class Class {
    private String sessionID;
    private String room;
    private String date;
    private String time;
    private boolean attendance = false;
    private String classID;
    private String sessionName;
    private String tutor;

    public String getSessionID() {
        return sessionID;
    }

    public void setSessionID(String sessionID) {
        this.sessionID = sessionID;
    }

    public String getRoom() {
        return room;
    }

    public void setRoom(String room) {
        this.room = room;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public Date getDateObject() {

        String dateString = this.date;
        DateFormat df = new SimpleDateFormat("dd/MM/yyyy");
        Date dateObject;
        try {
            dateObject = df.parse(dateString);
            return dateObject;
        } catch (Exception e) {
            //e.printStackTrace();
            return null;
        }
    }

    public boolean bookingIsPast(){
        Date todaysDate = new Date();
        Date date = this.getDateObject();
        return date.before(todaysDate);
    }

    public boolean bookingIsFuture(){
        Date todaysDate = new Date();
        Date date = this.getDateObject();
        return date.after(todaysDate);
    }

    public boolean isAttendance() {
        return attendance;
    }

    public void setAttendance(boolean attendance) {
        this.attendance = attendance;
    }

    public String getClassID() {
        return classID;
    }

    public void setClassID(String classID) {
        this.classID = classID;
    }

    public String getSessionName() {
        return sessionName;
    }

    public void setSessionName(String sessionName) {
        this.sessionName = sessionName;
    }

    public String getTutor() {
        return tutor;
    }

    public void setTutor(String tutor) {
        this.tutor = tutor;
    }

}
