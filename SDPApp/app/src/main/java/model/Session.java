package model;

import java.lang.reflect.Array;
import java.util.ArrayList;

/**
 * Created by kisungtae on 21/09/2016.
 */
public class Session {
    private String workshopID;
    private String restrictedFacultyID;
    private String restrictedCourseID;
    private String title;
    private String targetGroup;
    private String description;
    private String seesionId;
    private int maxAttendance;
    private int currentAttendance;
    private String date;
    private String tutor;


    public Session() {
    }


    public String getWorkshopID() {
        return workshopID;
    }

    public void setWorkshopID(String workshopID) {
        this.workshopID = workshopID;
    }

    public String getRestrictedFacultyID() {
        return restrictedFacultyID;
    }

    public void setRestrictedFacultyID(String restrictedFacultyID) {
        this.restrictedFacultyID = restrictedFacultyID;
    }

    public String getRestrictedCourseID() {
        return restrictedCourseID;
    }

    public void setRestrictedCourseID(String restrictedCourseID) {
        this.restrictedCourseID = restrictedCourseID;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getTargetGroup() {
        return targetGroup;
    }

    public void setTargetGroup(String targetGroup) {
        this.targetGroup = targetGroup;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void addClass(Class aClass) {
        //classes.add(aClass);
    }

    public void setSeesionId(String seesionId) {
        this.seesionId = seesionId;
    }

    public int getMaxAttendance() {
        return maxAttendance;
    }

    public void setMaxAttendance(int maxAttendance) {
        this.maxAttendance = maxAttendance;
    }

    public int getCurrentAttendance() {
        return currentAttendance;
    }

    public void setCurrentAttendance(int currentAttendance) {
        this.currentAttendance = currentAttendance;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }
}
