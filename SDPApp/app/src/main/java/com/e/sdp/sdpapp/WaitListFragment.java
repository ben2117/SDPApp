package com.e.sdp.sdpapp;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

/**
 * Created by kisungtae on 18/09/2016.
 */
public class WaitListFragment extends Fragment {
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View waitListView = inflater.inflate(R.layout.wait_list_fragment_view, null);

        return waitListView;
    }
}
