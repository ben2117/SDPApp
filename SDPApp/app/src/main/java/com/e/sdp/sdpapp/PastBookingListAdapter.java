package com.e.sdp.sdpapp;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

import model.Class;
import model.PastBooking;

/**
 * Created by kisungtae on 11/10/2016.
 */
public class PastBookingListAdapter extends ArrayAdapter<Class> {



    public PastBookingListAdapter(Context context, ArrayList<Class> pastBookings) {
        super(context, R.layout.past_booking_fragment_view, pastBookings);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater layoutInflater = LayoutInflater.from(getContext());
        View view = layoutInflater.inflate(R.layout.past_booking_row, null);

        Class currentClass = getItem(position);
        view.setTag(currentClass.getClassID());
        TextView titleTxtview = (TextView) view.findViewById(R.id.past_booking_row_title);
        TextView timeTxtview = (TextView) view.findViewById(R.id.past_booking_row_time);
        TextView tutorTxtview = (TextView) view.findViewById(R.id.past_booking_row_tutor);
        TextView locationTxtview = (TextView) view.findViewById(R.id.past_booking_row_location);
        TextView dateTxtview = (TextView) view.findViewById(R.id.past_booking_row_date);
        ImageView pencilImageview = (ImageView) view.findViewById(R.id.past_booking_row_pencil_imageview);

        titleTxtview.setText(currentClass.getSessionName());
        timeTxtview.setText(currentClass.getTime());
        tutorTxtview.setText(currentClass.getTutor());
        locationTxtview.setText(currentClass.getRoom());
        dateTxtview.setText(currentClass.getDate());


        if(currentClass.isAttendance()) {
            pencilImageview.setImageResource(R.drawable.pencil_active);
        }


        return view;
    }
}
