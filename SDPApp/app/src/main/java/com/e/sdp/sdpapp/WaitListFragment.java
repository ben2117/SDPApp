package com.e.sdp.sdpapp;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import model.WaitingSession;

/**
 * Created by kisungtae on 18/09/2016.
 */
public class WaitListFragment extends Fragment {

    private static final String CALLER = "caller";
    private static final String SESSIONKEY = "sessionKey";
    private static final String STUDENTID = "studentid";

    private String studentId;

    private ArrayList<WaitingSession> waitingSessions = new ArrayList<>();
    private WaitlistListviewAdapter waitlistListviewAdapter;
    private ListView waitListListview;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        studentId = getActivity().getIntent().getStringExtra(STUDENTID);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View waitListView = inflater.inflate(R.layout.wait_list_fragment_view, null);

        setListview(waitListView);

        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference waitlistRef = database.getReference("waiting");

        //for test, change 4567 to studentId variable
        Query query = waitlistRef.orderByChild("studentID").equalTo(studentId);

        query.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                if(dataSnapshot.exists()) {
                    WaitingSession waitingSession = dataSnapshot.getValue(WaitingSession.class);
                    waitingSession.setWaitingSessionID(dataSnapshot.getKey());
                    waitingSessions.add(waitingSession);
                    waitlistListviewAdapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        return waitListView;
    }

    private void setListview(View view) {
        waitListListview = (ListView) view.findViewById(R.id.wait_list_listview);
        waitlistListviewAdapter = new WaitlistListviewAdapter(getActivity(), waitingSessions);
        waitListListview.setAdapter(waitlistListviewAdapter);
        waitListListview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String sessionKey = (String) view.getTag();
                moveTo(SessionDetailActivity.class, sessionKey);
            }
        });
    }

    private void moveTo(Class toClass, String sessionKey){
        Intent intent = new Intent(getActivity(), toClass);
        intent.putExtra(SESSIONKEY, sessionKey);
        intent.putExtra(CALLER, Tag.WAITLISTFRAGMENT.toString());
        intent.putExtra(STUDENTID, studentId);
        startActivity(intent);
        getActivity().overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
    }
}
