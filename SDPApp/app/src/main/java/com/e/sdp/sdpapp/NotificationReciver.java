package com.e.sdp.sdpapp;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.graphics.PixelFormat;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.TaskStackBuilder;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Timer;
import java.util.TimerTask;
import java.util.logging.Level;

import model.Class;
import model.ReminderItem;

/**
 * Created by kisungtae on 10/10/2016.
 */
public class NotificationReciver extends BroadcastReceiver {

    private ArrayList<ReminderItem> reminderItems;
    private Context context;
    private static int counter = 0;
    private static int reminderId = 0;

    @Override
    public void onReceive(Context context, Intent intent) {
        reminderItems = Reminder.getReminderArray();
        this.context = context;

        ArrayList<ReminderItem> sevenDayReminders = new ArrayList<>();
        ArrayList<ReminderItem> oneDayReminders = new ArrayList<>();
        for(int i = 0; i < reminderItems.size(); i++) {
            ReminderItem currentReminder = reminderItems.get(i);
            if(currentReminder.getSevenDayActive()) {
               if(checkReminder(currentReminder.getClasses(), 14)) {
                   sevenDayReminders.add(currentReminder);
               }
            }

            if(currentReminder.getOneDayActive()) {
                if(checkReminder(currentReminder.getClasses(), 17)) {
                    oneDayReminders.add(currentReminder);
                }
            }
        }

        publishNotification(sevenDayReminders, "7");
        publishNotification(oneDayReminders, "1");
    }

    private boolean checkReminder(ArrayList<Class> classes, int dayBefore) {

        try {
            Calendar calendar = GregorianCalendar.getInstance();
            calendar.setTime(new Date());
            calendar.add(Calendar.DAY_OF_YEAR, dayBefore);
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd/MM/yyyy");
            String date = simpleDateFormat.format(calendar.getTime());
            Date dateBefore = simpleDateFormat.parse(date);

            for(int i = 0; i < classes.size(); i++) {
                Class currentClass = classes.get(i);
                if(currentClass.getDateObject().equals(dateBefore)) {
                    return true;
                }
            }
            return false;
        } catch (Exception e) {
            return false;
        }
    }

    private void publishNotification(ArrayList<ReminderItem> reminders, String dayAfter) {
        if(counter == 2) {
            counter = 0;
            reminderId = 0;
        }

        for(int i = 0; i < reminders.size(); i++) {
            NotificationCompat.Builder mBuilder = getBuilder(reminders.get(i).getSessionName(), dayAfter);
            NotificationManager mNotificationManager =
                    (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
            mNotificationManager.notify(reminderId++, mBuilder.build());
        }

        counter++;
    }

    private NotificationCompat.Builder getBuilder(String sessionName, String dayAfter) {
        Intent intent = new Intent();
        /*
        if(MainPageActivity.isLogined()) {
            //intent = new Intent(context, MainPageActivity.class);
            //intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
        } else {
            //intent = new Intent(context, MainActivity.class);
        }

        */


        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0,
                intent, 0);

        NotificationCompat.Builder mBuilder =
                new NotificationCompat.Builder(context)
                        .setSmallIcon(R.drawable.helps_notifi_icon)
                        .setContentTitle("HELPS Notification")
                        .setContentText(sessionName
                                + " has booking after " + dayAfter + " day");
        mBuilder.setContentIntent(pendingIntent);
        mBuilder.setAutoCancel(true);
        return mBuilder;
    }
}



/*
        //login check logic here






        NotificationCompat.Builder notifiBuilder = new NotificationCompat.Builder(context);
        notifiBuilder.setSmallIcon(R.drawable.helps_notifi_icon);
        notifiBuilder.setContentTitle("Help: You have a session");
        notifiBuilder.setContentText("Help: ");
        TaskStackBuilder taskStackBuilder = TaskStackBuilder.create(context);

        PendingIntent pendingIntent = taskStackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);
        notifiBuilder.setContentIntent(pendingIntent);

        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(0, notifiBuilder.build());




        final WindowManager windowManager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);

        LayoutInflater layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        View view = layoutInflater.inflate(R.layout.reminder_popup_view, null);

        WindowManager.LayoutParams layoutParams = new WindowManager.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT, 0, 0,
                WindowManager.LayoutParams.TYPE_SYSTEM_OVERLAY,
                WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED
                        | WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD
                        | WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON,
                PixelFormat.RGBA_8888
        );

        windowManager.addView(view, layoutParams);

        Timer timer = new Timer();
        timer.schedule(new CustomTimerTask(windowManager, view), 3000);
        */
