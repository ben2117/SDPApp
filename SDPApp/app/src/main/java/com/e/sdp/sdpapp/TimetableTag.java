package com.e.sdp.sdpapp;

/**
 * Created by kisungtae on 14/10/2016.
 */
public class TimetableTag {
    private String classID;
    private boolean passsed;

    public TimetableTag(String _classID, boolean _passsed) {
        this.classID = _classID;
        this.passsed = _passsed;
    }

    public boolean isPasssed() {
        return passsed;
    }

    public void setPasssed(boolean passsed) {
        this.passsed = passsed;
    }

    public String getClassID() {
        return classID;
    }

    public void setClassID(String classID) {
        this.classID = classID;
    }
}
