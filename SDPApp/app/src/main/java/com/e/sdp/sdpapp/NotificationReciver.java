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

import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;
import java.util.logging.Level;

import model.ReminderItem;

/**
 * Created by kisungtae on 10/10/2016.
 */
public class NotificationReciver extends BroadcastReceiver {

    private ArrayList<ReminderItem> reminderItems;
    private Context context;

    @Override
    public void onReceive(Context context, Intent intent) {
        reminderItems = Reminder.getReminderArray();
        final boolean loginValid = MainPageActivity.isLogined();
        this.context = context;
        if(loginValid) {
            publishNotification();
        } else {
            Log.e("dd", "logout");
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
    }

    private void publishNotification() {
        Intent nextIntent = new Intent(context, MyBookingFragment.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0,
                nextIntent, 0);

        NotificationCompat.Builder mBuilder =
                new NotificationCompat.Builder(context)
                        .setSmallIcon(R.drawable.helps_notifi_icon)
                        .setContentTitle("My notification")
                        .setContentText("Hello World!");
        mBuilder.setContentIntent(pendingIntent);
        mBuilder.setAutoCancel(true);
        NotificationManager mNotificationManager =
                (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        mNotificationManager.notify(1, mBuilder.build());

    }

}
