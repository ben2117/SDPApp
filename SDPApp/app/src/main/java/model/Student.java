package model;

/**
 * Created by kisungtae on 21/09/2016.
 */
public class Student {
    private String name;
    private String preferredFirstName;
    private String courseID;
    private String facultyID;
    private String homePhone;
    private String mobile;
    private String bestContactNo;
    private String dob;
    private String gender;
    private String year;
    private String status;
    private String firstLanguage;

    public Student(){}

    public String getCourseID(){
        return courseID;
    }
    public String getFacultyID(){
        return facultyID;
    }
    public String getName(){
        return name;
    }

}
