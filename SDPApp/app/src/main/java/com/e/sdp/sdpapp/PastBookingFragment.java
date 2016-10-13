package com.e.sdp.sdpapp;


import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

import model.BookingLine;
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

    private PastBookingListAdapter pastBookingListAdapter;
    private ArrayList<PastBooking> pastBookings = new ArrayList<>();
    private ListView pastBookingListView;




    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View pastBookingView = inflater.inflate(R.layout.past_booking_fragment_view, null);


        pastBookingListView = (ListView) pastBookingView.findViewById(R.id.past_booking_view_listview);


        //for test remove me
        PastBooking pastBooking = new PastBooking();
        pastBookings.add(pastBooking);



        pastBookingListAdapter = new PastBookingListAdapter(getActivity(), pastBookings);


        pastBookingListView.setAdapter(pastBookingListAdapter);


        return pastBookingView;
    }

    public void loadBookings(String studentId){
        Query bookingQuery = bookingRef.orderByChild("studentID").equalTo(studentId);

        bookingQuery.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                BookingLine bookingLine = dataSnapshot.getValue(BookingLine.class);
                Log.e("hey", bookingLine.getSessionID());
                loadSession(bookingLine.getSessionID());

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
}
