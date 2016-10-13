package com.e.sdp.sdpapp;

/**
 * Created by kisungtae on 25/09/2016.
 */
public enum Tag {
    MAINACTIVITY("MainActivity"),
    MAINPAGEACTIVITY("MainPageActivity"),
    MYBOOKINGFRAGMENT("MyBookingFragment"),
    SEARCHFRAGMENT("SearchFragment"),
    WAITLISTFRAGMENT("WaitlistFragment"),
    BLANKTAG("BlankFragment");

    private String mTag;

    private Tag(String tag) {
        mTag = tag;
    }

    @Override
    public String toString() {
        return mTag;
    }
}
