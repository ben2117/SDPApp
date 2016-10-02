package com.e.sdp.sdpapp;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ExpandableListView;

import java.util.ArrayList;

import butterknife.Bind;
import butterknife.ButterKnife;
import model.Faq;

public class FaqActivity extends AppCompatActivity {

    @Bind(R.id.faq_back_btn) Button backBtn;
    @Bind(R.id.faq_expandable_listview) ExpandableListView exList;

    private ArrayList<Faq> faqs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_faq);

        ButterKnife.bind(this);

        //set arraylist of faqs
        //jsut for test, remove me and use retrieveFaqData()
        faqs = new ArrayList<Faq>();
        setArrayOfFaq();

        //retrieveFaqData();

        setExpandableListView();
        setBackBtnListener();

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

    private void setExpandableListView() {
        FaqExpndAdapter faqExpndAdapter = new FaqExpndAdapter(FaqActivity.this, faqs);
        exList.setAdapter(faqExpndAdapter);
    }


    //only for test, remove me and use retrieveFaqData() after database setup
    private void setArrayOfFaq() {
        Faq faq1 = new Faq();
        faq1.setQuestion("Who can use HELPS?");
        faq1.setAnswer("Any student enrolled in any faculty at UTS, and all members of UTS staff");
        Faq faq2 = new Faq();
        faq2.setQuestion("Where is HELPS?");
        faq2.setAnswer("HELPS is located on Building 1, Level 3 , room 8 (opposite the Careers service)");
        Faq faq3 = new Faq();
        faq3.setQuestion("How much does it cost?");
        faq3.setAnswer("Services are free of tuition fees for non-credit workshops and individual consultations.");
        faqs.add(faq1);
        faqs.add(faq2);
        faqs.add(faq3);
    }
}
