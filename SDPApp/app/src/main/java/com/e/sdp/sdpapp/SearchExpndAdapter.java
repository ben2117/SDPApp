package com.e.sdp.sdpapp;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

import model.Session;
import model.Workshop;

/**
 * Created by kisungtae on 08/10/2016.
 */
public class SearchExpndAdapter extends BaseExpandableListAdapter{

    private LayoutInflater layoutInflater;
    private Context context;
    private ArrayList<Workshop> workshops;

    public SearchExpndAdapter(Context _context, ArrayList<Workshop> _workshops) {
        context = _context;
        workshops = _workshops;
        layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getGroupCount() {
        return workshops.size();
    }

    @Override
    public int getChildrenCount(int groupPosition) {
        return workshops.get(groupPosition).getSessions().size();
    }

    @Override
    public Object getGroup(int groupPosition) {
        return workshops.get(groupPosition);
    }

    @Override
    public Object getChild(int groupPosition, int childPosition) {
        return workshops.get(groupPosition).getSessions().get(childPosition);
    }

    @Override
    public long getGroupId(int groupPosition) {
        return 0;
    }

    @Override
    public long getChildId(int groupPosition, int childPosition) {
        return 0;
    }

    @Override
    public boolean hasStableIds() {
        return false;
    }

    @Override
    public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {
        if(convertView == null) {
            convertView = layoutInflater.inflate(R.layout.expandable_list_row, null);
        }
        setWorkshopInfo(groupPosition, convertView);
        setExpandIcon(convertView, isExpanded);
        return convertView;
    }

    private void setWorkshopInfo(int groupPosition, View convertView) {
        TextView titleTextView = (TextView) convertView.findViewById(R.id.expandable_list_row_title);
        TextView sessionNumTextView = (TextView) convertView.findViewById(R.id.expandable_list_row_session_number);
        String title = workshops.get(groupPosition).getTitle();
        String sessionNumber = String.valueOf(workshops.get(groupPosition).getSessions().size());
        titleTextView.setText(title);
        sessionNumTextView.setText(sessionNumber);
    }

    private void setExpandIcon(View convertView, boolean isExpanded) {
        //code for switch between expand and collapse images
        ImageView expandImage = (ImageView) convertView.findViewById(R.id.expand_image);
        if(isExpanded) {
            expandImage.setImageResource(R.drawable.collapse_arrow);
        } else {
            expandImage.setImageResource(R.drawable.expand_arrow);
        }
    }

    @Override
    public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {
        if(convertView ==  null) {
            convertView = layoutInflater.inflate(R.layout.expandable_session_row, null);
        }
        setSessionRow(convertView, groupPosition, childPosition);

        return convertView;
    }

    private void setSessionRow(View convertView, int groupPosition, int childPosition) {
        Session session = workshops.get(groupPosition).getSessions().get(childPosition);
        TextView titleTxtview = (TextView) convertView.findViewById(R.id.expand_list_session_row_titletxtview);
        TextView dateTxtview = (TextView) convertView.findViewById(R.id.expand_list_session_row_datetxtview);
        TextView locationTxtview = (TextView) convertView.findViewById(R.id.expand_list_session_row_locationview);
        TextView tutorTxtview = (TextView) convertView.findViewById(R.id.expand_list_session_row_tutortxtview);


        titleTxtview.setText(session.getTitle());
        dateTxtview.setText(session.getDate());
        tutorTxtview.setText(session.getTutor());
        locationTxtview.setText(session.getLocation());

        convertView.setTag(session.getSessionId());



    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return true;
    }
}


/*








 */