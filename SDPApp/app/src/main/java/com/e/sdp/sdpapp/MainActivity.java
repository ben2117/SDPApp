package com.e.sdp.sdpapp;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

//make sure sdk tools are updated including google play services
import com.google.firebase.database.*;

import java.util.ArrayList;
import java.util.List;


public class MainActivity extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        FirebaseDatabase database = FirebaseDatabase.getInstance();

        DatabaseReference studentsRef = database.getReference("student");
        DatabaseReference sessionsRef = database.getReference("session");

        studentsRef.child("S00001").addListenerForSingleValueEvent(
                new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        Student student = dataSnapshot.getValue(Student.class);
                        Log.d("student", student.getName());
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                }
        );

        sessionsRef.child("SE001").addListenerForSingleValueEvent(
                new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        Session session = dataSnapshot.getValue(Session.class);
                        Log.d("session", session.getDescription());
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                }
        );

        //
        //GenericTypeIndicator<List<String>> t = new GenericTypeIndicator<List<String>>() {};
        // List<String> yourStringArray = dataSnapshot.getValue(t);
        //
        //

        final GenericTypeIndicator<List<Student>> t = new GenericTypeIndicator<List<Student>>() {};

        studentsRef.addValueEventListener(
                new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        //Log.e("Count " ,""+dataSnapshot.);

                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                }
        );
       // Query q = myRef.orderByKey();

        DatabaseReference workshopsRef = database.getReference("workshop");

        workshopsRef.addValueEventListener(
                new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        for(DataSnapshot child : dataSnapshot.getChildren()){
                            Log.e("child ", child.getKey());
                            for(DataSnapshot attr : child.getChildren()){
                                Log.d("Attr", (String) attr.getValue());
                            }
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                }
        );
        Log.d("hello", "hi");



    }
}

