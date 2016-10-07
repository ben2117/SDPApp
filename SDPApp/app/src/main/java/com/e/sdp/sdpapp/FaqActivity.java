package com.e.sdp.sdpapp;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ExpandableListView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

import butterknife.Bind;
import butterknife.ButterKnife;
import model.Faq;

public class FaqActivity extends AppCompatActivity {

    @Bind(R.id.faq_back_btn) Button backBtn;
    @Bind(R.id.faq_expandable_listview) ExpandableListView exList;

    private ArrayList<Faq> faqs;
    private final FirebaseDatabase database = FirebaseDatabase.getInstance();
    private final String faqDatabaseRefKey = "faq";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_faq);

        ButterKnife.bind(this);
        setArrayOfFaq();
        setBackBtnListener();
    }

    private void setArrayOfFaq() {
        faqs = new ArrayList<Faq>();
        final DatabaseReference faqRef = database.getReference(faqDatabaseRefKey);
        faqRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for(DataSnapshot ds : dataSnapshot.getChildren()) {
                    Faq faq = ds.getValue(Faq.class);
                    faqs.add(faq);
                }
                setExpandableListView();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void setExpandableListView() {
        FaqExpndAdapter faqExpndAdapter = new FaqExpndAdapter(FaqActivity.this, faqs);
        exList.setAdapter(faqExpndAdapter);
    }

    private void setBackBtnListener() {
        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
                overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
            }
        });
    }
}
