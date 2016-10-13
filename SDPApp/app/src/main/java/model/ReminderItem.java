package model;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by kisungtae on 10/10/2016.
 */
public class ReminderItem implements Serializable{

    private String bookingId;
    private String sessionId;
    private String sessionName;
    private ArrayList<Class> classes;
    private Boolean sevenDayActive;
    private Boolean oneDayActive;
    private Boolean tenMinuteActive;

    public ReminderItem(String _bookingId, String _sessionId, Boolean _sevenDayActive, Boolean _oneDayActive, Boolean _tenMinuteActive) {
        bookingId = _bookingId;
        sessionId = _sessionId;
        sevenDayActive = _sevenDayActive;
        oneDayActive = _oneDayActive;
        tenMinuteActive = _tenMinuteActive;
        classes = new ArrayList<>();
    }

    public void addToClassArray(Class aClass) {
        classes.add(aClass);
    }


    public String getBookingId() {
        return bookingId;
    }

    public void setBookingId(String bookingId) {
        this.bookingId = bookingId;
    }

    public String getSessionId() {
        return sessionId;
    }

    public void setSessionId(String sessionId) {
        this.sessionId = sessionId;
    }

    public String getSessionName() {
        return sessionName;
    }

    public void setSessionName(String sessionName) {
        this.sessionName = sessionName;
    }

    public ArrayList<Class> getClasses() {
        return classes;
    }

    public void setClasses(ArrayList<Class> classes) {
        this.classes = classes;
    }

    public Boolean getSevenDayActive() {
        return sevenDayActive;
    }

    public void setSevenDayActive(Boolean sevenDayActive) {
        this.sevenDayActive = sevenDayActive;
    }

    public Boolean getOneDayActive() {
        return oneDayActive;
    }

    public void setOneDayActive(Boolean oneDayActive) {
        this.oneDayActive = oneDayActive;
    }

    public Boolean getTenMinuteActive() {
        return tenMinuteActive;
    }

    public void setTenMinuteActive(Boolean tenMinuteActive) {
        this.tenMinuteActive = tenMinuteActive;
    }
}
