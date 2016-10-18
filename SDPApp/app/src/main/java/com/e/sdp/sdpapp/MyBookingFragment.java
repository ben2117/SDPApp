package com.e.sdp.sdpapp;

import android.app.AlarmManager;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

import model.Booking;
import model.BookingLine;
import model.Class;
import model.Session;

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
    private static final String BOOKINGKEY = "bookingKey";
    private static final String STUDENTID = "studentid";
    private static final String CLASSKEY = "classKey";

    //test code remove or fix me
    private DatabaseReference bookingRef;
    private FirebaseDatabase database;

    //test code remove or leave me
    private Map<String, BookingLine> bookingKeySessionMap = new HashMap<>();

    //alarm manager for reminder
    private Reminder reminder;

    private String studentId;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setup();

        try {
            setBookingDatabaseListener();
        } catch (Exception e) {
            Toast.makeText(getActivity(), "try again", Toast.LENGTH_SHORT).show();
        }

    }

    private void setup() {
        studentId = getActivity().getIntent().getStringExtra(STUDENTID);
        reminder = new Reminder(getActivity());
        database = FirebaseDatabase.getInstance();
        bookingRef = database.getReference("booking");
    }

    private void setBookingDatabaseListener() {
        Query bookingQuery = bookingRef.orderByChild("studentID").equalTo(studentId);
        bookingQuery.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                BookingLine bookingLine = dataSnapshot.getValue(BookingLine.class);
                bookingLine.setBookingID(dataSnapshot.getKey());
                bookingKeySessionMap.put(bookingLine.getSessionID(), bookingLine);
                addBookingRow(bookingLine.getSessionID());
                reminder.addReminderItem(dataSnapshot.getKey(), bookingLine.getSessionID());
                setNextClass(bookingLine.getSessionID());
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {
                BookingLine bookingLine = dataSnapshot.getValue(BookingLine.class);
                String sessionId = bookingLine.getSessionID();
                BookingLine tag = bookingKeySessionMap.get(sessionId);

                String alarmType = (String) dataSnapshot.child("alarmType").getValue();
                Toast.makeText(getActivity(), alarmType, Toast.LENGTH_SHORT).show();

                View view = getView().findViewWithTag(tag);
                ImageView img = (ImageView) view.findViewById(R.id.booking_row_alarm_imageview);
                setAlarmImgView(img, alarmType);
            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {
                //bookingKeySessionMAp remove logic here
                BookingLine bookingLine = dataSnapshot.getValue(BookingLine.class);
                String sessionId = bookingLine.getSessionID();
                BookingLine removingRowTag = bookingKeySessionMap.get(sessionId);
                removeBookingRow(removingRowTag);
                bookingKeySessionMap.remove(sessionId);

                //test remove or leave me
                reminder.removeReminderItem(dataSnapshot.getKey());
                View view = nextBookedClassLayout.getChildAt(0);
                if(view != null) {
                    Class currentNextClass = (Class) view.getTag();
                    if(currentNextClass.getSessionID().equals(sessionId)) {
                        nextBookedClassLayout.removeView(view);
                    }
                }

                Toast.makeText(getActivity(), "Booking Cancled", Toast.LENGTH_SHORT).show();

            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }



    private void setNextClass(String sessionKey) {
        Log.e("finNextClass", "i am called");
        DatabaseReference classRef = database.getReference("class");
        Query query = classRef.orderByChild("sessionID").equalTo(sessionKey);
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Class nextClass;
                for(DataSnapshot ds : dataSnapshot.getChildren()) {
                    Class currentClass = ds.getValue(Class.class);
                    if(!currentClass.bookingIsPast()) {
                        nextClass = currentClass;
                        currentClass.setClassID(ds.getKey());
                        addNextClassRow(nextClass);
                        return;
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void addNextClassRow(final Class nextClass) {
        DatabaseReference sessionRef = database.getReference("session");
        sessionRef.child(nextClass.getSessionID()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                boolean isBefore = isCurrentNextClassBefore(nextClass);
                Session session = dataSnapshot.getValue(Session.class);
                if(!isBefore) {
                    LayoutInflater layoutInflater1 = LayoutInflater.from(getContext());
                    final LinearLayout nextBookedRow = (LinearLayout) layoutInflater1.inflate(R.layout.next_class_row, null);
                    nextBookedRow.setTag(nextClass);
                    populateNextClassInfo(nextBookedRow, nextClass, session.getTitle());
                    nextBookedClassLayout.addView(nextBookedRow);
                    nextBookedRow.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            Log.e("i am working", "i am touched");
                            Class aClass = (Class) view.getTag();
                            String classKey = aClass.getClassID();
                            showVerficationPopup(classKey);
                        }
                    });
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void populateNextClassInfo(LinearLayout nextBookedRow, Class nextClass, String sessionName) {
        TextView classDateTxtview = (TextView) nextBookedRow.findViewById(R.id.next_booked_class_class_date_txtview);
        TextView classTimeTxtview = (TextView) nextBookedRow.findViewById(R.id.next_booked_class_class_location_txtview);
        TextView classLocationTxtview = (TextView) nextBookedRow.findViewById(R.id.next_booked_class_class_location_txtview);
        TextView classTitleTxtview = (TextView) nextBookedRow.findViewById(R.id.next_booked_class_session_title_txtview);

        classDateTxtview.setText(nextClass.getDate());
        classLocationTxtview.setText(nextClass.getRoom());
        classTimeTxtview.setText(nextClass.getTime());
        classTitleTxtview.setText(sessionName);
    }

    private boolean isCurrentNextClassBefore(Class nextClass) {
        boolean isBefore;
        View currentNextClassLayout = nextBookedClassLayout.getChildAt(0);
        if(currentNextClassLayout == null) {
            isBefore = false;
        } else {
            Class currentNextClass = (Class) currentNextClassLayout.getTag();
            if(currentNextClass.getDateObject().after(nextClass.getDateObject())) {
                isBefore = false;
                nextBookedClassLayout.removeView(currentNextClassLayout);
            } else {
                isBefore = true;
            }
        }
        return isBefore;
    }

    //take booking object and database key like "B000002" to set tag for each booking view????
    private void addBookingRow(String sessionID) {

        final FirebaseDatabase database = FirebaseDatabase.getInstance();
        final DatabaseReference sessionRef = database.getReference("session");
        final Query sessionQuery = sessionRef.child(sessionID);

        sessionQuery.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                final View myBookingRow = layoutInflater.inflate(R.layout.my_booking_row, myBookingLayout, false);
                String sessionID = dataSnapshot.getKey();

                BookingLine bookingLine = bookingKeySessionMap.get(sessionID);
                myBookingRow.setTag(bookingLine);

                Session session = dataSnapshot.getValue(Session.class);

                //needs to fix???
                Booking myBooking = new Booking(session.getLocation(), session.getTitle(), session.getDate(), "see details", bookingLine.getAlarmType());

                populateSessionInfo(myBooking, myBookingRow);
                final ImageView imageView = (ImageView) myBookingRow.findViewById(R.id.booking_row_alarm_imageview);
                setAlarmImgView(imageView, bookingLine.getAlarmType());
                setAlarmImgViewListener(imageView);

                myBookingRow.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(getActivity(), SessionDetailActivity.class);
                        BookingLine bookingLineTag = (BookingLine) v.getTag();
                        intent.putExtra(SESSIONKEY, bookingLineTag.getSessionID());
                        intent.putExtra(BOOKINGKEY, bookingLineTag.getBookingID());
                        moveTo(intent);
                    }
                });

                myBookingLayout.addView(myBookingRow);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }

    private void removeBookingRow(BookingLine key) {
        View view = getView().findViewWithTag(key);
        myBookingLayout.removeView(view);
    }

    //here populate the session info to view
    private void populateSessionInfo(Booking booking, View myBookingRow) {
        TextView room =  (TextView) myBookingRow.findViewById(R.id.booking_row_session_location);
        room.setText(booking.getRoom());
        TextView title =  (TextView) myBookingRow.findViewById(R.id.booking_row_session_title_txtview);
        title.setText(booking.getTitle());
        TextView date = (TextView) myBookingRow.findViewById(R.id.booking_row_session_date);
        date.setText(booking.getDate());
        TextView time = (TextView) myBookingRow.findViewById(R.id.booking_row_session_time);
        time.setText("see details");
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

                BookingLine bookingLine = (BookingLine) parent.getTag();
                String bookingID = bookingLine.getBookingID();

                if(isAlarmSet) {
                    //update alarm type to none on database
                    bookingRef.child(bookingID).child("alarmType").setValue("none");
                    reminder.updateReminderItem(bookingID, false, false, false);

                } else {
                    //show alarm dialog
                    showAlarmDialog(bookingID);
                    }

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

    private void showAlarmDialog(String keyTag) {
        alarmPopupDialog = new AlarmPopupDialog();
        alarmPopupDialog.setTargetFragment(this, 0);
        alarmPopupDialog.show(getFragmentManager(), keyTag);
    }

    //it is fired when the ok button in alarm popup is pressed
    @Override
    public void onAlarmOkClick(String alarmType, Boolean sevenFlag, Boolean oneFlag, Boolean tenFlag) {
        String tag = alarmPopupDialog.getTag();
        bookingRef.child(tag).child("alarmType").setValue(alarmType);
        reminder.updateReminderItem(alarmPopupDialog.getTag(),
                sevenFlag,
                oneFlag,
                tenFlag);

    }

    private void moveTo(Intent intent){
        intent.putExtra(CALLER, Tag.MYBOOKINGFRAGMENT.toString());
        intent.putExtra(STUDENTID, studentId);
        startActivity(intent);
        getActivity().overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
    }

    private void showVerficationPopup(String classId) {
        //test remove after add verification field to
        //class database table
        String tempVerificationCode = "giveup";

        VerificationCodePopup verificationCodePopup = new VerificationCodePopup();
        Bundle bundle = new Bundle();
        bundle.putString(CLASSKEY, classId);
        verificationCodePopup.setArguments(bundle);
        verificationCodePopup.show(getFragmentManager(), tempVerificationCode);

    }
}
