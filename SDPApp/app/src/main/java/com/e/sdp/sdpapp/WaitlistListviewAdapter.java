package com.e.sdp.sdpapp;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

import model.WaitingSession;

/**
 * Created by kisungtae on 11/10/2016.
 */
public class WaitlistListviewAdapter extends ArrayAdapter<WaitingSession> {
    public WaitlistListviewAdapter(Context context, ArrayList<WaitingSession> waitingSessions) {
        super(context, R.layout.wait_list_row ,waitingSessions);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater layoutInflater = LayoutInflater.from(getContext());
        View view = layoutInflater.inflate(R.layout.wait_list_row, null);

        WaitingSession waitingSession = getItem(position);

        view.setTag(waitingSession.getSessionID());
        TextView titleTextview = (TextView) view.findViewById(R.id.wait_list_session_title_txtview);
        TextView numberOfStuTextview = (TextView) view.findViewById(R.id.wait_list_number_of_student);

        //for test, comment in after database update
        //titleTextview.setText(waitingSession.getSessionTitle());

        numberOfStuTextview.setText(String.valueOf(waitingSession.getQueuePosition()));

        return view;
    }
}
