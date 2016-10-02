package model;

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

    public Session() {}

    public String getskillSetID(){
        return workshopID;
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
