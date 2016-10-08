package com.e.sdp.sdpapp;

import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.Map;

import butterknife.Bind;
import butterknife.ButterKnife;
import model.Booking;
import model.Class;
import model.Session;

public class SessionDetailActivity extends AppCompatActivity {

    public interface Command {
        void runCommand();
    }

    @Bind(R.id.session_detail_back_btn) Button backBtn;

    //this button is going to be either "book now", "cancel", "Add to wait-list", or "drop session"
    @Bind(R.id.session_detail_btn) Button sessionDetailBtn;

    //text views for details of a session
    @Bind(R.id.session_detail_session_title_txview) TextView sessionTitleTxtview;
    @Bind(R.id.session_detail_session_date_txtview) TextView sessionDateTxtview;
    @Bind(R.id.session_detail_session_place_txtview) TextView sessionPlaceTxtview;
    @Bind(R.id.session_detail_session_cover_txtview) TextView sessionCoverTxtview;
    @Bind(R.id.session_detail_session_targetgroup_txtview) TextView sessionTargetTxtview;

    //add time table row to this layout
    @Bind(R.id.session_detail_timetable_layout) LinearLayout sessionTimetableLayout;

    //inflater to inflate timetable row layout
    private LayoutInflater layoutInflater;

    //intent keys for caller and session database key
    private static final String CALLER = "caller";
    private static final String SESSIONKEY = "sessionKey";

    //save previous tag to determine behaviour of session detail button
    //and session key for retrieve or delete query
    private String preTag;
    private String sessionKey;

    //just special tag when a session is full
    private static final String FULLSEARCHFRAGMENT = "FullSearchFragment";

    //map to hold button texts
    Map<String, String> btnTextMap;

    //map to hold method call based on tag
    Map<String, Command> methodMap;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_session_detail);


        //load data here?

        setup();
        setButtonTextMap();
        setMethodMap();
        setButtonText();
        setButtonListener();


        String sessionKey = getIntent().getStringExtra(SESSIONKEY);
        final FirebaseDatabase database = FirebaseDatabase.getInstance();
        final DatabaseReference sessionRef = database.getReference("session");
        final DatabaseReference classRef = database.getReference("class");

        Query classQuery = classRef.orderByChild("sessionID").equalTo(sessionKey);

        Log.e("session key", sessionKey);

        sessionRef.child(sessionKey).addListenerForSingleValueEvent(
                new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        Session session = dataSnapshot.getValue(Session.class);
                        sessionTitleTxtview.setText(session.getTitle());
                        sessionDateTxtview.setText(session.getDate());
                        sessionPlaceTxtview.setText("see classes");
                        sessionCoverTxtview.setText(session.getDescription());
                        sessionTargetTxtview.setText(session.getTargetGroup());
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                }
        );

        /*
        //populate session info method needed
        String sessionKey = getIntent().getStringExtra(SESSIONKEY);
        Log.e("booking", bookingKey);
        final FirebaseDatabase database = FirebaseDatabase.getInstance();
        final DatabaseReference bookingRef = database.getReference("booking");

        bookingRef.child(bookingKey).addListenerForSingleValueEvent(
                new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        Booking myBooking = dataSnapshot.getValue(Booking.class);
                        addTimetableRow(myBooking, "key");
                        Log.e("i was called", "here i am");
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                }
        );
        */

                        //test remove me
       /* Class aclass = new Class();
        aclass.setDate("21/21/2016");
        aclass.setRoom("CB01.05.10C");
        aclass.setTime("12:00 ~ 13:00");
        addTimetableRow(aclass, "key");

        */
    }

    private void addTimetableRow(Booking aClass, String classKey) {
        View sessionTimetableRow = getSessionTimetableLayout(aClass);
        sessionTimetableRow.setTag(classKey);


        populateTimetableInfo(aClass, sessionTimetableRow);
        sessionTimetableLayout.addView(sessionTimetableRow);
    }

    private View getSessionTimetableLayout(Booking aClass) {
        View sessionTimetableRow = layoutInflater.inflate(R.layout.session_timetable_row, sessionTimetableLayout, false);
        ImageView pencilImgView = (ImageView) sessionTimetableRow.findViewById(R.id.session_timetable_row_pencil_imgview);

        //if users come from my booking fragment and if the session has not yet passed
        //then it will be clickable
        //if users come from other fragments, then remove the pencil image view
        if(preTag.equals(Tag.MYBOOKINGFRAGMENT.toString())) {
            if(!isPassed(aClass.getDate())) {
                sessionTimetableRow.setClickable(true);

                //based on session and current time
                //set pencil imageview's image to active or inactive one needed

            }
        } else {
            pencilImgView.setVisibility(View.INVISIBLE);
        }

        return sessionTimetableRow;
    }

    //check if the class has already passed
    private Boolean isPassed(String classDate) {
        Boolean passed = true;


        return passed;
    }

    private void populateTimetableInfo(Booking aClass, View sessionTimetableRow) {
        TextView txtView = null;

        Boolean passed = isPassed(aClass.getDate());

        txtView = (TextView) sessionTimetableRow.findViewById(R.id.session_timetable_row_date_txtview);
        setTimetableTextView(txtView, aClass.getDate(), passed);

        txtView = (TextView) sessionTimetableRow.findViewById(R.id.session_timetable_row_time_txtview);
        setTimetableTextView(txtView, aClass.getTime(), passed);

        txtView = (TextView) sessionTimetableRow.findViewById(R.id.session_timetable_row_room_txtview);
        setTimetableTextView(txtView, aClass.getRoom(), passed);
    }

    private void setTimetableTextView(TextView textview, String text, Boolean passed) {

        //text color to light gray for past class
        if(passed) {
            textview.setTextColor(ContextCompat.getColor(SessionDetailActivity.this, R.color.inactiveText));
        }
        textview.setText(text);
    }


    //set method calls based on tag
    private void setMethodMap() {
        methodMap = new HashMap<String, Command>();
        methodMap.put(Tag.MYBOOKINGFRAGMENT.toString(), new Command() {
            @Override
            public void runCommand() {

            }
        });

        methodMap.put(Tag.SEARCHFRAGMENT.toString(), new Command() {
            @Override
            public void runCommand() {

            }
        });

        methodMap.put(Tag.WAITLISTFRAGMENT.toString(), new Command() {
            @Override
            public void runCommand() {

            }
        });

        methodMap.put(FULLSEARCHFRAGMENT, new Command() {
            @Override
            public void runCommand() {

            }
        });
    }

    private void setButtonListener() {
        //set back button listener
        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
                overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
            }
        });

        //set session detail button listener
        sessionDetailBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                methodMap.get(preTag).runCommand();
            }
        });
    }

    //set button test in the map based on the tags
    private void setButtonTextMap() {
        btnTextMap = new HashMap<String, String>();
        btnTextMap.put(Tag.MYBOOKINGFRAGMENT.toString(), "Cancel");
        btnTextMap.put(Tag.SEARCHFRAGMENT.toString(), "Book Now");
        btnTextMap.put(Tag.WAITLISTFRAGMENT.toString(), "Cancel Waiting-list");
        btnTextMap.put(FULLSEARCHFRAGMENT, "Add to Waiting-list");
    }

    private void setup() {
        ButterKnife.bind(this);

        //get previous activity tag
        //when session is full, then use special tag of FULLSEARCHFRAGMENT
        preTag = getIntent().getStringExtra(CALLER);
        if(preTag.equals( Tag.SEARCHFRAGMENT.toString()) && isFull()) {
            preTag = FULLSEARCHFRAGMENT;
        }
        sessionKey = getIntent().getStringExtra(SESSIONKEY);
        layoutInflater = LayoutInflater.from(SessionDetailActivity.this);
    }

    private void setButtonText() {
        String btnText = btnTextMap.get(preTag);
        sessionDetailBtn.setText(btnText);
    }

    //determine if the session is full
    //need to fix
    private boolean isFull() {
        Boolean isFull = false;

        //logic to see if session is full or not here

        return isFull;
    }
}
