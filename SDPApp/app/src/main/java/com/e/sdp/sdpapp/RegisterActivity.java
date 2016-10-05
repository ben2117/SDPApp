package com.e.sdp.sdpapp;

import android.content.Context;
import android.content.Intent;
import android.graphics.Rect;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import butterknife.Bind;
import butterknife.ButterKnife;
import model.Student;


public class RegisterActivity extends AppCompatActivity {

    //save previous activity tag to determine if it comes directly
    //from login or main page activity
    private String preActivityTag;

    private String studentId;

    //intent key for caller activity
    private static final String CALLER = "caller";
    private static final String STUDENTID = "studentid";

    //bind pre-populated info views
    @Bind(R.id.stname_textview) TextView stnameTextview;
    @Bind(R.id.faculty_textview) TextView facultyTextview;
    @Bind(R.id.course_textview) TextView courseTextview;
    @Bind(R.id.email_textview) TextView stemailTextview;
    @Bind(R.id.homephone_textview) TextView homephoneTextview;
    @Bind(R.id.mobile_textview) TextView mobileTextview;
    @Bind(R.id.dateofbirth_textview) TextView dateofbirthTextview;

    //bind edittext views required to be entered in registration form
    @Bind(R.id.bestcontactnumber_edittext) EditText bestContactEd;
    @Bind(R.id.degree_radiogroup) RadioGroup degreeRadioGrp;
    @Bind(R.id.status_radiogroup) RadioGroup statusRadioGrp;
    @Bind(R.id.academicyear_spinner) Spinner academicYearSpnr;
    @Bind(R.id.firstlanguage_spinner) Spinner firstLanguageSpnr;
    @Bind(R.id.country_spinner) Spinner countrySpnr;
    @Bind(R.id.preferedname_edittext) EditText preferedNameEd;
    @Bind(R.id.gender_radiogroup) RadioGroup genderRadioGrp;

    //bind buttons and ....
    @Bind(R.id.back_btn) Button backBtn;
    @Bind(R.id.register_submit_btn) Button submitBtn;
    @Bind(R.id.english_score_checkboxes_layout) LinearLayout checkBoxLayout;


    //need code to manage english score check boxes


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        setup();

        //set back and submit button listener
        setButtonListeners();

        //prepopulate user details
        populate();
    }

    private void setup() {
        ButterKnife.bind(this);
        preActivityTag = getIntent().getStringExtra(CALLER);

        //get student database key from intent here?
        studentId = getIntent().getStringExtra(STUDENTID);
        Log.e("preActivityTag", studentId);
    }

    //set submit and back button listener
    private void setButtonListeners() {
        submitBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                submit();
            }
        });
        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
                overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
            }
        });
    }

    //non-registered user can only get to registration page from login page
    //if it is from login page, then only prepopulate UTS student information
    //if it is from mainpage which means registered student,
    //then populate both UTS student information and HELPS student information
    private void populate() {
        prepopulate();
        if(preActivityTag.equals(Tag.MAINPAGEACTIVITY.toString())) {
            populateRegisteredStudentInfo();
        }
    }

    //prepopulate the UTS student information
    private void prepopulate() {
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference studentsRef = database.getReference("prePopStudent");
        studentsRef.child(studentId).addListenerForSingleValueEvent(
                new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        Student student = dataSnapshot.getValue(Student.class);
                        stnameTextview.setText(student.getName());

                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                }
        );

    }

    //populate registered student information
    private void populateRegisteredStudentInfo() {
        Log.e("hi", " i am pop reg");
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference studentsRef = database.getReference("student");
        studentsRef.child(studentId).addListenerForSingleValueEvent(
                new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        Student student = dataSnapshot.getValue(Student.class);
                        stnameTextview.setText(student.getName());

                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                }
        );

    }

    //submit method to send data to database here? with validation
    private void submit() {

        //---------start test, change validate() --> !validate()--------------
        if(validate()) {
            Toast.makeText(RegisterActivity.this, "Please enter required information", Toast.LENGTH_SHORT).show();
            return;
        }


        //try and catch in the saveToDatabase?
        //so that we can stop here if saving to database fails
        saveToDatabase();

        //either to directly move to main page or to display Toast message based on pre tag?
        moveToPageOnPreTag();
    }

    //validate input details in registration form
    //to make sure all the compulsory questions are answered
    private boolean validate() {
        boolean valid = true;

        String bestContact = bestContactEd.getText().toString();
        int degreeRadioBtnID = degreeRadioGrp.getCheckedRadioButtonId();
        int statusRadioBtnId = statusRadioGrp.getCheckedRadioButtonId();
        String academicYear = academicYearSpnr.getSelectedItem().toString();
        String firstLanguage = firstLanguageSpnr.getSelectedItem().toString();
        String country = countrySpnr.getSelectedItem().toString();

        if(bestContact.isEmpty()) {
            valid = false;
        }

        //getCheckedRadioButtonId returns -1 if nothing is checked
        if(degreeRadioBtnID == -1) {
            valid = false;
        } else {
        }

        if(statusRadioBtnId == -1) {
            valid = false;
        }

        if(academicYear.isEmpty()) {
            valid = false;
        }

        if(firstLanguage.isEmpty()) {
            valid = false;
        }

        if(country.isEmpty()) {
            valid = false;
        }

        return valid;
    }


    //if previous activity is login, then move to main page directly
    //if previous activity is main page, then just display update success message
    //to allow users to check updated information in registration page??????
    private void moveToPageOnPreTag() {
        if(preActivityTag.equals(Tag.MAINACTIVITY.toString())) {
            moveTo(MainPageActivity.class);
        } else {
            Toast.makeText(RegisterActivity.this, "Successfully updated", Toast.LENGTH_SHORT).show();
        }
    }

    //save to database
    private void saveToDatabase() {
    }

    //set checkbox listener to show and hide corresponding edittext view
    public void onCheckboxClicked(View view) {
        CheckBox checkBox = (CheckBox)view;
        boolean checked = checkBox.isChecked();
        if(checked) {
          addEditTextBelowCheckBox(checkBox);
        } else {
            removeEditTextBelowCheckBox(checkBox);
        }
    }

    //put checkbox id in the edit text so that we can easily remove it
    // or get data from the views later
    private void addEditTextBelowCheckBox(CheckBox checkBox) {
        int position = checkBoxLayout.indexOfChild(checkBox);
        EditText ed = new EditText(RegisterActivity.this);
        ed.setMaxLines(1);
        ed.setTag(checkBox.getResources().getResourceName(checkBox.getId()));
        checkBoxLayout.addView(ed, position + 1);
    }

    private void removeEditTextBelowCheckBox(CheckBox checkBox) {
        String tag = checkBox.getResources().getResourceName(checkBox.getId());
        EditText ed = (EditText) checkBoxLayout.findViewWithTag(tag);
        checkBoxLayout.removeView(ed);
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        if(ev.getAction() == MotionEvent.ACTION_DOWN) {
            View view = getCurrentFocus();
            if( view instanceof EditText) {
                Rect outRect = new Rect();
                view.getGlobalVisibleRect(outRect);
                if(!outRect.contains((int)ev.getRawX(), (int)ev.getRawY())) {
                    view.clearFocus();
                    InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    inputMethodManager.hideSoftInputFromWindow(view.getWindowToken(), 0);
                }
            }
        }
        return super.dispatchTouchEvent(ev);
    }

    private void moveTo(Class toClass) {
        Intent intentToMainPage = new Intent(RegisterActivity.this, toClass);
        startActivity(intentToMainPage);

        //put student database key in intent here?

        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
    }
}
