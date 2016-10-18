package com.e.sdp.sdpapp;


import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

import model.Attendance;
import model.BookingLine;
import model.Class;
import model.PastBooking;
import model.Session;

/**
 * Created by kisungtae on 18/09/2016.
 */
public class PastBookingFragment extends Fragment {


    final FirebaseDatabase database = FirebaseDatabase.getInstance();
    final DatabaseReference bookingRef = database.getReference("booking");
    final DatabaseReference sessionRef = database.getReference("session");
    final DatabaseReference classRef = database.getReference("class");
    final DatabaseReference attendanceRef = database.getReference("attendance");

    private PastBookingListAdapter pastBookingListAdapter;
    private ArrayList<Class> pastBookings = new ArrayList<>();
    private ListView pastBookingListView;

    //intent key
    private final static String CLASSKEY = "classKey";


    private static final String STUDENTID = "studentid";
    private String studentID;


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        studentID = getActivity().getIntent().getStringExtra(STUDENTID);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View pastBookingView = inflater.inflate(R.layout.past_booking_fragment_view, null);
        setup(pastBookingView);

        try {
            loadBookings(studentID);
        } catch (Exception e) {
            Toast.makeText(getActivity(), "Invalid Request", Toast.LENGTH_SHORT).show();
        }

        setListener();
        return pastBookingView;
    }

    private void setListener() {
        pastBookingListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String classKey = (String) view.getTag();
                Class aClass = null;
                for(int i = 0; i < pastBookings.size(); i++) {
                    if(pastBookings.get(i).getClassID().equals(classKey)) {
                        aClass = pastBookings.get(i);
                    }
                }

                if(aClass != null) {
                    if(aClass.isAttendance()) {
                        moveTo(TextEditor.class, aClass.getClassID());
                    } else {
                        Toast.makeText(getActivity(), "Unattended class", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });
    }

    private void setup(View pastBookingView) {
        pastBookingListView = (ListView) pastBookingView.findViewById(R.id.past_booking_view_listview);
        pastBookingListAdapter = new PastBookingListAdapter(getActivity(), pastBookings);
        pastBookingListView.setAdapter(pastBookingListAdapter);
    }

    public void loadBookings(String studentId){
        Query bookingQuery = bookingRef.orderByChild("studentID").equalTo(studentId);
        bookingQuery.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                BookingLine bookingLine = dataSnapshot.getValue(BookingLine.class);
                loadClasses(bookingLine.getSessionID());
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void loadClasses(final String sessionId) {
        final Query classQuery = classRef.orderByChild("sessionID").equalTo(sessionId);
        classQuery.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                for(DataSnapshot child : dataSnapshot.getChildren()){
                    try {
                        final model.Class aClass = child.getValue(model.Class.class);
                        Log.e("dddddddd", child.getKey() + "   " + aClass.getDate());
                        if(aClass.bookingIsPast()) {
                            aClass.setClassID(child.getKey());
                            Log.e("dfasdfg", "past booking key" + aClass.getClassID());
                            sessionRef.child(sessionId).addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(DataSnapshot dataSnapshot) {
                                    if(dataSnapshot.exists()) {
                                        Session session = dataSnapshot.getValue(Session.class);
                                        aClass.setSessionName(session.getTitle());
                                        aClass.setTutor(session.getTutor());
                                        pastBookings.add(aClass);
                                        checkAttendance();
                                    }
                                }

                                @Override
                                public void onCancelled(DatabaseError databaseError) {

                                }
                            });

                        }
                    } catch (Exception e) {
                        Log.e("load classes", "a null date object");
                    }
                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void checkAttendance() {
        if(pastBookings.size() != 0) {
            Query query = attendanceRef.orderByChild("studentID").equalTo(studentID);
            query.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    for(DataSnapshot ds : dataSnapshot.getChildren()) {
                        Attendance attendance = ds.getValue(Attendance.class);
                        for(int i = 0; i < pastBookings.size(); i++) {
                            Log.e("keysss", pastBookings.get(i).getClassID() + "     "  + attendance.getClassID());
                            if(pastBookings.get(i).getClassID().equals(attendance.getClassID())) {
                                pastBookings.get(i).setAttendance(true);
                            }
                        }
                    }
                    pastBookingListAdapter.notifyDataSetChanged();
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
        }
    }

    private void moveTo(java.lang.Class toClass, String classID) {
        Intent intent = new Intent(getActivity(), toClass);
        intent.putExtra(CLASSKEY, classID);
        startActivity(intent);
        getActivity().overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
    }

    /*
    public void loadSession(final String sessionId){

        final Query sessionQuery = sessionRef.child(sessionId);

        sessionQuery.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Session session = dataSnapshot.getValue(Session.class);
                loadClasses(sessionId, session);

            }
            @Override
            public void onCancelled(DatabaseError databaseError) {

            }

        });


    }

    public void loadClasses(String sessionId, Session session){
        final Query classQuery = classRef.orderByChild("sessionID").equalTo(sessionId);
        classQuery.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                for(DataSnapshot child : dataSnapshot.getChildren()){
                    try {
                        model.Class aClass = child.getValue(model.Class.class);
                        if(aClass.bookingIsPast()) {
                            Log.e("get date ", "this data is after today");
                        } else {
                            Log.e("get date ", "this data is before today");
                        }
                    } catch (Exception e) {
                        Log.e("load classes", "a null date object");
                    }

                }


            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }
       */
}
