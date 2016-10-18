package com.e.sdp.sdpapp;

import android.content.Intent;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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
import model.BookingLine;
import model.Class;
import model.Session;
import model.WaitingSession;

public class SessionDetailActivity extends AppCompatActivity {

    public interface Command {
        void runCommand();
    }

    //this button is going to be either "book now", "cancel", "Add to wait-list", or "drop session"
    @Bind(R.id.session_detail_btn)
    Button sessionDetailBtn;
    @Bind(R.id.session_detail_back_btn)
    Button backBtn;

    //text views for details of a session
    @Bind(R.id.session_detail_session_title_txview)
    TextView sessionTitleTxtview;
    @Bind(R.id.session_detail_session_date_txtview)
    TextView sessionDateTxtview;
    @Bind(R.id.session_detail_session_place_txtview)
    TextView sessionPlaceTxtview;
    @Bind(R.id.session_detail_session_cover_txtview)
    TextView sessionCoverTxtview;
    @Bind(R.id.session_detail_session_targetgroup_txtview)
    TextView sessionTargetTxtview;

    //add time table row to this layout
    @Bind(R.id.session_detail_timetable_layout)
    LinearLayout sessionTimetableLayout;

    //intent keys for caller and session database key
    private static final String CALLER = "caller";
    private static final String SESSIONKEY = "sessionKey";
    private static final String STUDENTID = "studentid";
    private static final String BOOKINGKEY = "bookingKey";
    private static final String CLASSKEY = "classKey";

    //save previous tag to determine behaviour of session detail button
    //and session key for retrieve or delete query
    private String preTag;
    private String sessionKey;
    private String studentId;
    private String bookingKey;
    private String sessionName = "";

    //inflater to inflate timetable row layout
    private LayoutInflater layoutInflater;

    //just special tag when a session is full
    private static final String FULLSEARCHFRAGMENT = "FullSearchFragment";

    //map to hold button texts
    Map<String, String> btnTextMap;

    //map to hold method call based on tag
    Map<String, Command> methodMap;

    private FirebaseDatabase database;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_session_detail);

        setup();
        setButtonTextMap();
        setMethodMap();
        setButtonListener();
        loadSessionData();

    }

    private void setup() {
        ButterKnife.bind(this);
        layoutInflater = LayoutInflater.from(SessionDetailActivity.this);
        database = FirebaseDatabase.getInstance();
        Intent fromIntent = getIntent();
        sessionKey = fromIntent.getStringExtra(SESSIONKEY);
        studentId = fromIntent.getStringExtra(STUDENTID);

        //at first, tag is blank then to whatever it is
        //for network connection
        preTag = Tag.BLANKTAG.toString();

        if (fromIntent.hasExtra(BOOKINGKEY)) {
            bookingKey = fromIntent.getStringExtra(BOOKINGKEY);
        }
    }

    //set button test in the map based on the tags
    private void setButtonTextMap() {
        btnTextMap = new HashMap<String, String>();
        btnTextMap.put(Tag.MYBOOKINGFRAGMENT.toString(), "Cancel");
        btnTextMap.put(Tag.SEARCHFRAGMENT.toString(), "Book Now");
        btnTextMap.put(Tag.WAITLISTFRAGMENT.toString(), "Already Added to List");
        btnTextMap.put(FULLSEARCHFRAGMENT, "Add to waiting list");
    }

    private void loadSessionData() {
        final DatabaseReference sessionRef = database.getReference("session");
        final DatabaseReference classRef = database.getReference("class");
        final Query classQuery = classRef.orderByChild("sessionID").equalTo(sessionKey);


        sessionRef.child(sessionKey).addListenerForSingleValueEvent(
                new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        Session session = dataSnapshot.getValue(Session.class);
                        setUpPreTag(session);
                        sessionName = session.getTitle();
                        sessionTitleTxtview.setText(session.getTitle());
                        sessionDateTxtview.setText(session.getDate());
                        long availPlace = session.getMaxAttendance() - session.getCurrentAttendance();
                        sessionPlaceTxtview.setText(String.valueOf(availPlace));
                        sessionCoverTxtview.setText(session.getDescription());
                        sessionTargetTxtview.setText(session.getTargetGroup());

                        classQuery.addListenerForSingleValueEvent(
                                new ValueEventListener() {
                                    @Override
                                    public void onDataChange(DataSnapshot dataSnapshot) {
                                        for (DataSnapshot child : dataSnapshot.getChildren()) {
                                            Class aClass = child.getValue(Class.class);
                                            addTimetableRow(aClass, child.getKey());
                                        }
                                    }

                                    @Override
                                    public void onCancelled(DatabaseError databaseError) {

                                    }
                                }
                        );
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                }
        );
    }

    private void setUpPreTag(Session session) {
        //get previous activity tag
        //when session is full, then use special tag of FULLSEARCHFRAGMENT
        preTag = getIntent().getStringExtra(CALLER);
        checkBooking(session);

    }

    private void checkBooking(final Session session) {
        DatabaseReference bookingRef = database.getReference("booking");
        Query query = bookingRef.orderByChild("studentID").equalTo(studentId);
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                boolean isBooked = false;
                for (DataSnapshot ds : dataSnapshot.getChildren()) {
                    BookingLine bookingLine = ds.getValue(BookingLine.class);
                    if (bookingLine.getSessionID().equals(sessionKey)) {
                        preTag = Tag.MYBOOKINGFRAGMENT.toString();
                        isBooked = true;
                        setButtonText();
                        return;
                    }
                }
                if (!isBooked) {
                    checkWaitAndFull(session);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void checkWaitAndFull(Session session) {
        if (preTag.equals(Tag.SEARCHFRAGMENT.toString()) && isFull(session)) {
            DatabaseReference waitRef = database.getReference("waiting");
            Query query = waitRef.orderByChild("studentID").equalTo(studentId);
            query.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    preTag = FULLSEARCHFRAGMENT;
                    if (dataSnapshot.exists()) {
                        for (DataSnapshot ds : dataSnapshot.getChildren()) {
                            WaitingSession waitingSession = ds.getValue(WaitingSession.class);
                            if (waitingSession.getSessionID().equals(sessionKey)) {
                                preTag = Tag.WAITLISTFRAGMENT.toString();
                            }
                        }
                    }
                    setButtonText();
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });

        } else {
            setButtonText();
        }
    }

    private void setButtonText() {
        String btnText = btnTextMap.get(preTag);
        sessionDetailBtn.setText(btnText);
        if (preTag.equals(Tag.WAITLISTFRAGMENT.toString())) {
            sessionDetailBtn.setClickable(false);
        }
    }


    private void addTimetableRow(Class aClass, String classKey) {
        View sessionTimetableRow = getSessionTimetableLayout(aClass, classKey);

        populateTimetableInfo(aClass, sessionTimetableRow);
        sessionTimetableRow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //here need logic to check if it is during session time or not
                if (preTag.equals(Tag.MYBOOKINGFRAGMENT.toString())) {
                    TimetableTag timetableTag = (TimetableTag) v.getTag();
                    if (timetableTag.isPasssed()) {
                        Toast.makeText(SessionDetailActivity.this, "already passed", Toast.LENGTH_SHORT).show();
                    } else {
                        showVerficationPopup(timetableTag.getClassID());
                    }

                }
            }
        });
        sessionTimetableLayout.addView(sessionTimetableRow);
    }

    private View getSessionTimetableLayout(Class aClass, String classKey) {
        View sessionTimetableRow = layoutInflater.inflate(R.layout.session_timetable_row, null);
        ImageView pencilImgView = (ImageView) sessionTimetableRow.findViewById(R.id.session_timetable_row_pencil_imgview);

        TimetableTag timetableTag = new TimetableTag(classKey, aClass.bookingIsPast());
        sessionTimetableRow.setTag(timetableTag);

        if (preTag.equals(Tag.MYBOOKINGFRAGMENT.toString())) {

        } else {
            pencilImgView.setVisibility(View.INVISIBLE);
        }

        return sessionTimetableRow;
    }

    private void populateTimetableInfo(Class aClass, View sessionTimetableRow) {
        TextView txtView = null;

        Boolean passed = aClass.bookingIsPast();

        txtView = (TextView) sessionTimetableRow.findViewById(R.id.session_timetable_row_date_txtview);
        setTimetableTextView(txtView, aClass.getDate(), passed);

        txtView = (TextView) sessionTimetableRow.findViewById(R.id.session_timetable_row_time_txtview);
        setTimetableTextView(txtView, aClass.getTime(), passed);

        txtView = (TextView) sessionTimetableRow.findViewById(R.id.session_timetable_row_room_txtview);
        setTimetableTextView(txtView, aClass.getRoom(), passed);
    }

    private void showVerficationPopup(String classId) {
        //test remove after add verification field to
        //class database table
        String tempVerificationCode = "giveup";

        VerificationCodePopup verificationCodePopup = new VerificationCodePopup();
        Bundle bundle = new Bundle();
        bundle.putString(CLASSKEY, classId);
        bundle.putString(STUDENTID, studentId);
        verificationCodePopup.setArguments(bundle);
        verificationCodePopup.show(getSupportFragmentManager(), tempVerificationCode);

    }

    private void setTimetableTextView(TextView textview, String text, Boolean passed) {
        if (passed) {
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

                //24 hours before the session time? logic here

                //cancel logic
                final DatabaseReference bookingRef = database.getReference("booking");
                final DatabaseReference sessionRef = database.getReference("session");

                bookingRef.child(bookingKey).removeValue();
                sessionRef.child(sessionKey).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        Session session = dataSnapshot.getValue(Session.class);
                        long availablePlace = session.getCurrentAttendance() - 1;
                        sessionRef.child(dataSnapshot.getKey()).child("currentAttendance").setValue(availablePlace);
                        decrementForWaitingList(sessionKey);
                        finishWithAni();
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
            }
        });

        methodMap.put(Tag.SEARCHFRAGMENT.toString(), new Command() {
            @Override
            public void runCommand() {

                //logic for date chekcer??
                //logic for duplicate booking
                final DatabaseReference bookingRef = database.getReference("booking");
                Query query = bookingRef.orderByChild("studentID").equalTo(studentId);
                query.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {

                        boolean valid = true;
                        if (dataSnapshot.hasChildren()) {
                            for (DataSnapshot ds : dataSnapshot.getChildren()) {
                                BookingLine bookingLine = ds.getValue(BookingLine.class);
                                if (bookingLine.getSessionID().equals(sessionKey)) {
                                    valid = false;
                                    Toast.makeText(SessionDetailActivity.this, "You are already in this session", Toast.LENGTH_SHORT).show();
                                }
                            }
                        }

                        if (valid) {
                            saveBookingToDatabase();
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });


            }
        });

        methodMap.put(FULLSEARCHFRAGMENT, new Command() {
            @Override
            public void runCommand() {

                final WaitingSession session = new WaitingSession();
                session.setStudentID(studentId);
                session.setSessionID(sessionKey);
                session.setSessionName(sessionName);

                final DatabaseReference waitingRef = database.getReference("waiting");

                Query query = waitingRef.orderByChild("sessionID").equalTo(sessionKey);
                query.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        long queueposition = dataSnapshot.getChildrenCount() + 1;

                        session.setQueuePosition(queueposition);
                        addWaitingList(session);

                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });


            }
        });

        methodMap.put(Tag.BLANKTAG.toString(), new Command() {
            @Override
            public void runCommand() {
                Toast.makeText(SessionDetailActivity.this, "check network", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void addWaitingList(final WaitingSession session){
        final DatabaseReference waitingRef = database.getReference("waiting");
        waitingRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String lastKey = "";
                for (DataSnapshot ds : dataSnapshot.getChildren()) {
                    if (ds.exists()) {
                        lastKey = ds.getKey();
                    }
                }
                String finalKey = FirebaseNodeEntryGenerator.generateKey(lastKey);

                waitingRef.child(finalKey).setValue(session);
                Log.e("data snap shot childre", ""+ session.getQueuePosition());
                Toast.makeText(SessionDetailActivity.this, "added to waiting", Toast.LENGTH_SHORT).show();
                finishWithAni();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void saveBookingToDatabase() {
        final DatabaseReference bookingRef = database.getReference("booking");
        final DatabaseReference sessionRef = database.getReference("session");
        bookingRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String lastKey = "";
                if (dataSnapshot.hasChildren()) {
                    for (DataSnapshot ds : dataSnapshot.getChildren()) {
                        if (ds.exists()) {
                            lastKey = ds.getKey();
                        }
                    }
                }

                String newBookingKey = FirebaseNodeEntryGenerator.generateKey(lastKey);

                BookingLine newBookingLine = new BookingLine(sessionKey, studentId, "none");
                bookingRef.child(newBookingKey).setValue(newBookingLine);
                Toast.makeText(SessionDetailActivity.this, "you are booked", Toast.LENGTH_SHORT).show();


                sessionRef.child(sessionKey).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        if (dataSnapshot.exists()) {
                            Session session = dataSnapshot.getValue(Session.class);
                            long availablePlace = session.getCurrentAttendance() + 1;
                            sessionRef.child(dataSnapshot.getKey()).child("currentAttendance").setValue(availablePlace);
                            finishWithAni();
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });


            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void setButtonListener() {
        //set back button listener
        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finishWithAni();
            }
        });

        //set session detail button listener
        sessionDetailBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (preTag.equals(Tag.WAITLISTFRAGMENT.toString())) {

                } else {
                    methodMap.get(preTag).runCommand();
                }

            }
        });
    }

    //determine if the session is full
    private boolean isFull(Session session) {
        Boolean isFull = false;
        if ((session.getMaxAttendance() - session.getCurrentAttendance()) <= 0) {
            isFull = true;
        }
        return isFull;
    }

    private void finishWithAni() {
        finish();
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
    }

    private void decrementForWaitingList(String sessionId) {
        final DatabaseReference waitingRef = database.getReference("waiting");
        Log.e("i was here", "i was here");
        Query waitingQuery = waitingRef.orderByChild("sessionID").equalTo(sessionId);
        //Query waitingQuery = waitingRef.orderByChild("sessionID").equalTo("SE001");
        waitingQuery.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // Log.e("data snapshot key", dataSnapshot.getKey());
                for (DataSnapshot child : dataSnapshot.getChildren()) {
                    // Log.e("childs key", child.getKey());
                    WaitingSession waitingSession = child.getValue(WaitingSession.class);
                    long newPosition = waitingSession.getQueuePosition() - 1;
                    waitingRef.child(child.getKey()).child("queuePosition").setValue(newPosition);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

}
