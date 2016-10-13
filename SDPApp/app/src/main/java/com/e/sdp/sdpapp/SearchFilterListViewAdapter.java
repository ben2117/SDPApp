package com.e.sdp.sdpapp;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;

import model.Session;

/**
 * Created by kisungtae on 08/10/2016.
 */
public class SearchFilterListViewAdapter extends ArrayAdapter<Session> {
    public SearchFilterListViewAdapter(Context context, ArrayList<Session> sessions) {
        super(context, R.layout.expandable_session_row , sessions);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater layoutInflater = LayoutInflater.from(getContext());
        View customView = layoutInflater.inflate(R.layout.expandable_session_row, parent, false);
        customView.setTag(getItem(position).getSessionId());

        TextView titleTxtview = (TextView) customView.findViewById(R.id.expand_list_session_row_titletxtview);
        TextView dateTxtview = (TextView) customView.findViewById(R.id.expand_list_session_row_datetxtview);
        TextView locationTxtview = (TextView) customView.findViewById(R.id.expand_list_session_row_locationview);
        TextView tutorTxtview = (TextView) customView.findViewById(R.id.expand_list_session_row_tutortxtview);

        titleTxtview.setText(getItem(position).getTitle());
        dateTxtview.setText(getItem(position).getDate());
        tutorTxtview.setText(getItem(position).getTutor());

        return customView;
    }
}
