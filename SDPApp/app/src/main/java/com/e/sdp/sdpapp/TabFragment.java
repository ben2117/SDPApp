package com.e.sdp.sdpapp;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

/**
 * Created by kisungtae on 13/09/2016.
 */
public class TabFragment extends Fragment {
    private static TabLayout tabLayout;
    private static ViewPager viewPager;
    private Toolbar toolbar;
    private static final String[] pageTitles = {"My Booking", "Search", "Wait-List", "Past Booking"};

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View tabView = inflater.inflate(R.layout.tab_view, container, false);

        setViewPager(tabView);
        setTabLayout(tabView);

        toolbar = (Toolbar) getActivity().findViewById(R.id.toolbar);

        //set toolbar title as my booking at first
        toolbar.setTitle(pageTitles[0]);

        return tabView;
    }

    private void setViewPager(View tabView) {
        viewPager = (ViewPager) tabView.findViewById(R.id.view_pager);
        viewPager.setAdapter(new TabAdapter(getChildFragmentManager()));

        //set limit to 3 pages so that fragment's oncreate or other method are not called
        //multiple times
        viewPager.setOffscreenPageLimit(3);

        //set subpages change listener
        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                switch (position) {
                    case 0: {
                        tabLayout.getTabAt(0).setIcon(R.drawable.my_booking_black);
                        tabLayout.getTabAt(1).setIcon(R.drawable.search_session);
                        tabLayout.getTabAt(2).setIcon(R.drawable.waitlist);
                        tabLayout.getTabAt(3).setIcon(R.drawable.booking_history);
                        break;
                    }
                    case 1: {
                        tabLayout.getTabAt(0).setIcon(R.drawable.my_booking);
                        tabLayout.getTabAt(1).setIcon(R.drawable.search_session_black);
                        tabLayout.getTabAt(2).setIcon(R.drawable.waitlist);
                        tabLayout.getTabAt(3).setIcon(R.drawable.booking_history);
                        break;
                    }
                    case 2: {
                        tabLayout.getTabAt(0).setIcon(R.drawable.my_booking);
                        tabLayout.getTabAt(1).setIcon(R.drawable.search_session);
                        tabLayout.getTabAt(2).setIcon(R.drawable.waitlist_black);
                        tabLayout.getTabAt(3).setIcon(R.drawable.booking_history);
                        break;
                    }
                    case 3: {
                        tabLayout.getTabAt(0).setIcon(R.drawable.my_booking);
                        tabLayout.getTabAt(1).setIcon(R.drawable.search_session);
                        tabLayout.getTabAt(2).setIcon(R.drawable.waitlist);
                        tabLayout.getTabAt(3).setIcon(R.drawable.booking_history_black);
                        break;
                    }
                }

                //change the toolbar title accordingly
                toolbar.setTitle(pageTitles[position]);
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
    }

    private void setTabLayout(View tabView) {
        tabLayout = (TabLayout) tabView.findViewById(R.id.tab);
        tabLayout.setupWithViewPager(viewPager);

        //set icons in tabs
        tabLayout.getTabAt(0).setIcon(R.drawable.my_booking_black);
        tabLayout.getTabAt(1).setIcon(R.drawable.search_session);
        tabLayout.getTabAt(2).setIcon(R.drawable.waitlist);
        tabLayout.getTabAt(3).setIcon(R.drawable.booking_history);
    }
}
