package com.e.sdp.sdpapp;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;

import model.Faq;

/**
 * Created by kisungtae on 15/09/2016.
 */
public class FaqExpndAdapter extends BaseExpandableListAdapter {

    private Context context;
    private ArrayList<Faq> faqs;
    private LayoutInflater inflater;
    private final static int NUMANSWER = 1;

    public FaqExpndAdapter(Context _context, ArrayList<Faq> _faqs) {
        this.context = _context;
        this.faqs = _faqs;
        inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getGroupCount() {
        return faqs.size();
    }

    @Override
    public int getChildrenCount(int groupPosition) {
        //each question has just one answer
        return NUMANSWER;
    }

    @Override
    public Object getGroup(int groupPosition) {
        return faqs.get(groupPosition);
    }

    @Override
    public Object getChild(int groupPosition, int childPosition) {
        return faqs.get(groupPosition).getAnswer();
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
            convertView = inflater.inflate(R.layout.expandable_list_row, null);
        }

        customizeGroupView(convertView);
        setQuestion(convertView, groupPosition);
        setExpandIcon(convertView, isExpanded);

        return convertView;
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

    private void setQuestion(View convertView, int groupPosition) {
        //set question
        TextView questiontxtview = (TextView) convertView.findViewById(R.id.expandable_list_row_title);
        Faq faq = (Faq) getGroup(groupPosition);
        String question = faq.getQuestion();
        questiontxtview.setText(question);
    }

    private void customizeGroupView(View convertView) {
        //remove the number of session textview in the layout
        LinearLayout removeLayout = (LinearLayout) convertView.findViewById(R.id.expandable_list_row_remove_layout);
        LinearLayout parentLayout = (LinearLayout) convertView.findViewById(R.id.expandable_list_row_parent_layout);
        parentLayout.removeView(removeLayout);

        //change the weight of question  from 0.7 to 0.9
        LinearLayout titleLayout = (LinearLayout) convertView.findViewById(R.id.expandable_list_title_layout);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                0, ViewGroup.LayoutParams.WRAP_CONTENT, 0.9f
        );
        titleLayout.setLayoutParams(params);
    }

    @Override
    public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {
        if(convertView == null) {
            convertView = inflater.inflate(R.layout.faq_answer_view, null);
        }

        //set answer
        TextView answerTxtview = (TextView) convertView.findViewById(R.id.faq_answer_textview);
        String answer = (String) getChild(groupPosition, childPosition);
        answerTxtview.setText(answer);

        return convertView;
    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return false;
    }
}
