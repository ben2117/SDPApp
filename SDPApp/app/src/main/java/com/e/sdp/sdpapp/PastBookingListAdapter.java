package com.e.sdp.sdpapp;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

import java.util.ArrayList;

import model.PastBooking;

/**
 * Created by kisungtae on 11/10/2016.
 */
public class PastBookingListAdapter extends ArrayAdapter<PastBooking> {
    public PastBookingListAdapter(Context context, ArrayList<PastBooking> pastBookings) {
        super(context, R.layout.past_booking_fragment_view, pastBookings);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater layoutInflater = LayoutInflater.from(getContext());
        View view = layoutInflater.inflate(R.layout.past_booking_row, null);

        //populate info here




        return view;
    }
}
