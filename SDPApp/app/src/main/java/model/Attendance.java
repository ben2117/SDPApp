package model;

/**
 * Created by kisungtae on 13/10/2016.
 */
public class Attendance {
    private String studentID;
    private String classID;

    public Attendance() {

    }

    public Attendance(String _studentID, String _classID) {
        studentID = _studentID;
        classID = _classID;
    }

    public String getStudentID() {
        return studentID;
    }

    public void setStudentID(String studentID) {
        this.studentID = studentID;
    }

    public String getClassID() {
        return classID;
    }

    public void setClassID(String classID) {
        this.classID = classID;
    }
}
