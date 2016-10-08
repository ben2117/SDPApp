package com.e.sdp.sdpapp;


import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

/**
 * Created by kisungtae on 18/09/2016.
 */
public class PastBookingFragment extends Fragment {
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View pastBookingView = inflater.inflate(R.layout.past_booking_fragment_view, null);

        return pastBookingView;
    }
}
