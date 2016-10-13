package com.e.sdp.sdpapp;

import android.view.View;
import android.view.WindowManager;

import java.util.TimerTask;

/**
 * Created by kisungtae on 10/10/2016.
 */
public class CustomTimerTask extends TimerTask {


    private WindowManager windowManager;
    private View view;

    public CustomTimerTask(WindowManager _windowManager, View _view) {
        windowManager = _windowManager;
        view = _view;
    }

    @Override
    public void run() {
        windowManager.removeView(view);
    }



}


