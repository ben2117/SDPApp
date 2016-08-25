package com.e.sdp.sdpapp;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

//make sure sdk tools are updated including google play services
import com.google.firebase.database.*;


public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        //writing to database
        DatabaseReference myRef = database.getReference("message");
        myRef.setValue("Hello, World!");
    }
}

