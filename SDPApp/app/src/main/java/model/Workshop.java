package model;

import java.util.ArrayList;

/**
 * Created by kisungtae on 22/09/2016.
 */
public class Workshop {

    //might be easier to have these in booking database??
 //   private String sessionID;


    private String title;
    private String type;
    private ArrayList<Session> sessions;

    public Workshop() {
        sessions = new ArrayList<>();
    }


    public void addSession(Session session){
        this.sessions.add(session);
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}
