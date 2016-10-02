package com.e.sdp.sdpapp;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.Spinner;


/**
 * Created by kisungtae on 18/09/2016.
 */
public class AlarmPopupDialog extends android.support.v4.app.DialogFragment implements View.OnClickListener {

    //call back to mybooking fragment to change the image view for alarm icon
    private OnAlarmOkClickListener callback;

    //interface to connect between this and mybooking fragment
    public interface OnAlarmOkClickListener {
        public void onAlarmOkClick(String alarmType);
    }


    private Spinner alarmTypeSpnr;
    private static final int POPUPWIDTH = 300;
    private static final int POPUPHEIGHT = 250;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //set callback, target fragment is set when dialog is created in my booking fragment
        try {
            callback = (OnAlarmOkClickListener) getTargetFragment();
        } catch (ClassCastException e) {
            throw new ClassCastException("error");
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.alarm_popup_view, container, false);

        //remove title in the popup window
        getDialog().getWindow().requestFeature(Window.FEATURE_NO_TITLE);

        //set alarm type spinner
        alarmTypeSpnr = (Spinner) view.findViewById(R.id.alarm_popup_spinner);

        setButtons(view);

        return view;
    }

    private void setButtons(View view) {
        Button cancelBtn = (Button) view.findViewById(R.id.alarm_popup_cancelbtn);
        Button okBtn = (Button) view.findViewById(R.id.alarm_popup_okbtn);
        cancelBtn.setOnClickListener(this);
        okBtn.setOnClickListener(this);
    }

    @Override
    public void onResume() {
        super.onResume();
        setWindowLayout();
    }

    private void setWindowLayout() {
        //set the width and height of popup
        int width = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, POPUPWIDTH, getResources().getDisplayMetrics());
        int height = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, POPUPHEIGHT, getResources().getDisplayMetrics());
        Window window = getDialog().getWindow();
        window.setLayout(width, height);
        window.setGravity(Gravity.CENTER);
    }

    @Override
    public void onClick(View v) {
        if(v.getId() == R.id.alarm_popup_okbtn) {
            //callback to my booking fragment
            callback.onAlarmOkClick(alarmTypeSpnr.getSelectedItem().toString());
        }
        //close popup window
        dismiss();
    }
}
