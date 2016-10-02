package com.e.sdp.sdpapp;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import model.Booking;

/**
 * Created by kisungtae on 15/09/2016.
 */

//need layout and code to display something like "nothing to show"
//when users have no bookings


public class MyBookingFragment extends Fragment implements AlarmPopupDialog.OnAlarmOkClickListener {

    private LinearLayout nextBookedClassLayout;
    private LinearLayout myBookingLayout;
    private LinearLayout oneToOneConsultationLayout;
    private LayoutInflater layoutInflater;
    private AlarmPopupDialog alarmPopupDialog;

    //intent keys for caller and session database key
    private static final String CALLER = "caller";
    private static final String SESSIONKEY = "sessionKey";

    //test code remove or fix me
    DatabaseReference bookingRef;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        //test code, remove or fix me
        final FirebaseDatabase database = FirebaseDatabase.getInstance();
        bookingRef = database.getReference("booking");
        Query query = bookingRef.orderByChild("studentID").equalTo("S00001");
        query.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                Booking myBooking = dataSnapshot.getValue(Booking.class);
                addBookingRow(myBooking, dataSnapshot.getKey());
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {
                //separate listener only for alarm type???
                String key = dataSnapshot.getKey();
                String alarmType = (String) dataSnapshot.child("alarmType").getValue();
                Toast.makeText(getActivity(), alarmType, Toast.LENGTH_SHORT).show();
                View view = getView().findViewWithTag(key);
                ImageView img = (ImageView) view.findViewById(R.id.booking_row_alarm_imageview);
                setAlarmImgView(img, alarmType);


            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {
                String tag = dataSnapshot.getKey();
                removeBookingRow(tag);

            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View myBookingView = inflater.inflate(R.layout.my_booking_fragment_view, container, false);
        layoutInflater = inflater;
        bindView(myBookingView);
        return myBookingView;
    }

    private void bindView(View view) {
        nextBookedClassLayout = (LinearLayout) view.findViewById(R.id.next_booked_class_linearlayout);
        myBookingLayout = (LinearLayout) view.findViewById(R.id.myBooking_linearlayout);
        oneToOneConsultationLayout = (LinearLayout) view.findViewById(R.id.one_to_one_linearlayout);
    }

    //take booking object and database key like "B000002" to set tag for each booking view????
    private void addBookingRow(Booking booking, String key) {
        View myBookingRow = layoutInflater.inflate(R.layout.my_booking_row, myBookingLayout, false);
        final ImageView imageView = (ImageView) myBookingRow.findViewById(R.id.booking_row_alarm_imageview);
        String alarmSet = booking.getAlarmType();

        //for test, remove or fix me
        //go to session detailed page
        myBookingRow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), SessionDetailActivity.class);

                //put session key to use for getting timetable??
                intent.putExtra(SESSIONKEY, (String)v.getTag());
                moveTo(intent);
            }
        });


        //set tag with the key for later reference
        myBookingRow.setTag(key);

        populateSessionInfo(booking, myBookingRow);
        setAlarmImgView(imageView, alarmSet);
        setAlarmImgViewListener(imageView);

        myBookingLayout.addView(myBookingRow);
    }

    private void removeBookingRow(String key) {
        View view = getView().findViewWithTag(key);
        myBookingLayout.removeView(view);
    }



    //here populate the session info to view
    private void populateSessionInfo(Booking booking, View myBookingRow) {

    }

    private void setAlarmImgView(ImageView imgView, String alarmType) {
        if(alarmType.equals("none")) {
            imgView.setImageResource(R.drawable.inactive_alarm);
            imgView.setTag(false);
        } else {
            imgView.setImageResource(R.drawable.active_alarm);
            imgView.setTag(true);
        }
    }

    private void setAlarmImgViewListener(ImageView imageView) {
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ImageView alarmImgView = (ImageView)v;
                Boolean isAlarmSet = (Boolean) alarmImgView.getTag();

                //alarmImgView.getRootView() goes all the way up......
                //need to fix
                LinearLayout parent = (LinearLayout) alarmImgView.getParent().getParent().getParent().getParent();
                String tag = (String)parent.getTag();

                if(isAlarmSet) {
                    //update alarm type to none on database
                    bookingRef.child(tag).child("alarmType").setValue("none");

                } else {
                    //show alarm dialog
                    showAlarmDialog(tag);}
            }
        });
    }

    private void showAlarmDialog(String keyTag) {
        alarmPopupDialog = new AlarmPopupDialog();
        alarmPopupDialog.setTargetFragment(this, 0);
        alarmPopupDialog.show(getFragmentManager(), keyTag);
    }

    //it is fired when the ok button in alarm popup is pressed
    @Override
    public void onAlarmOkClick(String alarmType) {
        String tag = alarmPopupDialog.getTag();
        bookingRef.child(tag).child("alarmType").setValue(alarmType);
    }

    private void moveTo(Intent intent){
        intent.putExtra(CALLER, Tag.MYBOOKINGFRAGMENT.toString());
        startActivity(intent);
        getActivity().overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
    }
}
