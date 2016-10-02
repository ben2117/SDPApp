package com.e.sdp.sdpapp;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

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
    }

    @Override
    public int getCount() {
        return PAGENUM;
    }
}
