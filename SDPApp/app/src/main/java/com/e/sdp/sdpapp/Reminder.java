package com.e.sdp.sdpapp;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Calendar;

import model.Class;
import model.ReminderItem;
import model.Session;


public class Reminder {

    private Context context;
    private AlarmManager alarmManager;
    public static ArrayList<ReminderItem> reminderItems = new ArrayList<>();

    //8 == 8am every day
    private final static int DAILYCHECKTIME = 8;

    public Reminder(Context _context) {
        this.context = _context;
        setAlarmManager();
    }

    private void setAlarmManager() {
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY, DAILYCHECKTIME);


        PendingIntent pendingIntent = PendingIntent.getBroadcast(
                context.getApplicationContext(),
                100,
                getIntent(),
                PendingIntent.FLAG_UPDATE_CURRENT);

        alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);


        alarmManager.setRepeating(AlarmManager.RTC_WAKEUP,
                System.currentTimeMillis(),
                5 * 1000,
                pendingIntent);
        /*
        alarmManager.setRepeating(AlarmManager.RTC_WAKEUP,
                calendar.getTimeInMillis(),
                AlarmManager.INTERVAL_DAY,
                pendingIntent);

        */
    }

    public void addReminderItem(String bookingKey, final String sessionKey) {
        final FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference classRef = database.getReference("class");
        final DatabaseReference sessionRef = database.getReference("session");
        Query query = classRef.orderByChild("sessionID").equalTo(sessionKey);

        final ReminderItem reminderItem = new ReminderItem(bookingKey, sessionKey, false, false, false);
        reminderItems.add(reminderItem);

        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot.hasChildren()) {
                    for(DataSnapshot ds : dataSnapshot.getChildren()) {
                        Class aClass = ds.getValue(Class.class);
                        ReminderItem currentReminderItem = getReminderItem(aClass.getSessionID());
                        if(currentReminderItem != null) {
                            currentReminderItem.addToClassArray(aClass);
                        }
                    }
                }

                sessionRef.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        for(DataSnapshot ds : dataSnapshot.getChildren()) {
                            Session session = ds.getValue(Session.class);
                            ReminderItem reminderItem1 = getReminderItem(ds.getKey());
                            if(reminderItem1 != null) {
                                reminderItem1.setSessionName(session.getTitle());
                            }
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }

    public void removeReminderItem(String bookingKey) {
        if(reminderItems.size() != 0) {
            for(int i = 0; i < reminderItems.size(); i++) {
                if(reminderItems.get(i).getBookingId().equals(bookingKey)) {
                    reminderItems.remove(i);
                }
            }
        }
    }

    private ReminderItem getReminderItem(String sessionKey) {
        for(int i = 0; i < reminderItems.size(); i++) {
            if(reminderItems.get(i).getSessionId().equals(sessionKey)) {
                return reminderItems.get(i);
            }
        }
        return null;
    }

    public void updateReminderItem(String bookingId, Boolean sevenFlag, Boolean oneFlag, Boolean tenFlag) {
        for(int i = 0; i < reminderItems.size(); i++) {
            if(reminderItems.get(i).getBookingId().equals(bookingId)) {
                reminderItems.get(i).setSevenDayActive(sevenFlag);
                reminderItems.get(i).setOneDayActive(oneFlag);
                reminderItems.get(i).setTenMinuteActive(tenFlag);
            }
        }
        context.startActivity(getIntent());
    }

    public static ArrayList<ReminderItem> getReminderArray() {
        return reminderItems;
    }

    private Intent getIntent() {
        Intent intent = new Intent(context.getApplicationContext(), NotificationReciver.class);
        return intent;
    }

    //test remove me
    private void display() {

        for(int i = 0; i < reminderItems.size(); i++) {
            ReminderItem reminderItem = reminderItems.get(i);

            Log.e("dispp", reminderItem.getBookingId());
            Log.e("dispp", reminderItem.getSessionId());
            Log.e("dispp", reminderItem.getSessionName());
            Log.e("flag", String.valueOf(reminderItem.getSevenDayActive()));
            Log.e("flag", String.valueOf(reminderItem.getOneDayActive()));
            Log.e("flag", String.valueOf(reminderItem.getTenMinuteActive()));

            for(int j = 0; j < reminderItem.getClasses().size(); j++) {
                Class aclasss = reminderItem.getClasses().get(j);
                Log.e("sessionclass", aclasss.getDate());
                Log.e("sessionclass", aclasss.getTime());

            }
        }
    }

}
