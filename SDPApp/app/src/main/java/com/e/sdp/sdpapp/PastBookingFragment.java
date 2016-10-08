package com.e.sdp.sdpapp;


import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;

/**
 * Created by kisungtae on 18/09/2016.
 */
public class PastBookingFragment extends Fragment {

    private static final String STUDENTID = "studentid";

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View pastBookingView = inflater.inflate(R.layout.past_booking_fragment_view, null);

        final String studentId = getActivity().getIntent().getStringExtra(STUDENTID);

        final FirebaseDatabase database = com.google.firebase.database.FirebaseDatabase.getInstance();
        final DatabaseReference bookingRef = database.getReference("booking");
        Query bookingQuery = bookingRef.orderByChild("studentID").equalTo(studentId);




        return pastBookingView;
    }
}
