package com.e.sdp.sdpapp;

/**
 * Created by ben on 26/08/2016.
 */
public class Session {
    private String skillSetID;
    private String restrictedFacultyID;
    private String restrictedCourseID;
    private String title;
    private String targetGroup;
    private String description;

    public Session() {}

    public String getskillSetID(){
        return skillSetID;
    }
    public String getRestrictedFacultyID(){
        return restrictedFacultyID;
    }
    public String getRestrictedCourseID(){
        return restrictedCourseID;
    }
    public String getTitle(){
        return title;
    }
    public String getDescription(){
        return description;
    }
    public String getTargetGroup(){
        return targetGroup;
    }



}
