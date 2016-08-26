package com.e.sdp.sdpapp;

/**
 * Created by ben on 26/08/2016.
 */
public class Session {
    private String sessionTypeID;
    private String courseID;
    private String facultyID;
    private String title;
    private String description;
    private String sessionTime;
    private String sessionLocation;
    private int maxAttendance;
    private int currentAttendance;

    public Session() {}

    public String getSessionTypeID(){
        return sessionTypeID;
    }
    public String getCourseID(){
        return courseID;
    }
    public String getFacultyID(){
        return facultyID;
    }
    public String getTitle(){
        return title;
    }
    public String getDescription(){
        return description;
    }
    public String getSessionTime(){
        return sessionTime;
    }
    public String getSessionLocation(){
        return sessionLocation;
    }
    public int getMaxAttendance(){
        return maxAttendance;
    }
    public int getCurrentAttendance(){
        return currentAttendance;
    }



}
