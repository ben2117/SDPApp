package com.e.sdp.sdpapp;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.util.Log;

/**
 * Created by kisungtae on 13/09/2016.
 */
public class TabAdapter extends FragmentPagerAdapter {

    private final int PAGENUM = 4;

    public TabAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int position) {
        try {
            switch (position) {
                case 0 : {
                    return new MyBookingFragment();
                }
                case 1 : {
                    return new SearchFragment();
                }
                case 2 : {
                    return new WaitListFragment();
                }
                case 3 : {
                    return new PastBookingFragment();
                }
                default: {
                    return new MyBookingFragment();
                }
            }
        } catch (Exception e) {
            Log.e("error", e.getMessage());
            return null;
        }

    }

    @Override
    public int getCount() {
        return PAGENUM;
    }
}
