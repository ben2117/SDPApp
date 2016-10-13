package com.e.sdp.sdpapp;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;

/**
 * Created by kisungtae on 09/10/2016.
 */
public class VerificationCodePopup extends DialogFragment implements View.OnClickListener {

    private static final int POPUPWIDTH = 300;
    private static final int POPUPHEIGHT = 250;

    private static final String BOOKINGKEY = "bookingKey";
    private static final String CLASSKEY = "classKey";

    private EditText verificationCodeEdText;
    private String bookingId;
    private String classId;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        bookingId = getArguments().getString(BOOKINGKEY);
        classId = getArguments().getString(CLASSKEY);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.verification_code_popup_view, container, false);
        getDialog().getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        verificationCodeEdText = (EditText) view.findViewById(R.id.verification_code_popup_edtxtview);
        verificationCodeEdText.getBackground().clearColorFilter();
        setButtons(view);
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        setWindowLayout();
    }

    private void setButtons(View view) {
        Button cancelBtn = (Button) view.findViewById(R.id.verification_code_popup_cancel_btn);
        Button okBtn = (Button) view.findViewById(R.id.verification_code_popup_ok_btn);
        cancelBtn.setOnClickListener(this);
        okBtn.setOnClickListener(this);
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
        if(v.getId() == R.id.verification_code_popup_ok_btn) {
            boolean valid = checkInputAgainstVerifiCode();
            if(valid) {

                //go to edittext
                moveTo(TextEditor.class);



            } else {
                verificationCodeEdText.setError("Check Your Code");
            }
        } else {
            //close popup window
            dismiss();
        }
    }

    private boolean checkInputAgainstVerifiCode() {
        String input = verificationCodeEdText.getText().toString();
        if(input.equals(getTag())) {
            return true;
        }
        return false;
    }

    private void moveTo(Class toClass) {
        Intent intent = new Intent(getActivity(), toClass);
        intent.putExtra(BOOKINGKEY, bookingId);
        intent.putExtra(CLASSKEY, classId);
        startActivity(intent);
        getActivity().overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
    }
}
