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
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.GenericTypeIndicator;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

import butterknife.Bind;
import butterknife.ButterKnife;
import model.EducationalBackground;
import model.Student;


public class RegisterActivity extends AppCompatActivity{

    private int counterForEduback = 1;

    final static String INIEDBACKDATABASEKEY = "EB001";

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

        //get previous tag and student database key
        setup();

        //set back and submit button listener
        setButtonListeners();

        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference studentsRef = database.getReference("prePopStudent");
        studentsRef.child("SS001").child("facultyID").setValue("F9087");


        //populate information about student
        populate();
    }

    private void setup() {
        ButterKnife.bind(this);
        preActivityTag = getIntent().getStringExtra(CALLER);
        studentId = getIntent().getStringExtra(STUDENTID);
        Log.e("preActivityTag", studentId);
    }

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

    //pre tag == login, then only prepopulate
    //pre tag == mainpage, then both prepopulate and populateRegisteredStinfo
    private void populate() {
        prepopulate();
        if(preActivityTag.equals(Tag.MAINPAGEACTIVITY.toString())) {
            populateRegisteredStudentInfo();
            populateEducationBackground();
        }
    }

    //prepopulate the UTS student information
    private void prepopulate() {
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference studentsRef = database.getReference("prePopStudent");

        //for test, change "SS001" to studentID after fixing database
        studentsRef.child(studentId).addListenerForSingleValueEvent(
                new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        Student student = dataSnapshot.getValue(Student.class);
                        stnameTextview.setText(student.getName());
                        stemailTextview.setText(student.getEmail());
                        homephoneTextview.setText(student.getHomePhone());
                        mobileTextview.setText(student.getMobile());
                        dateofbirthTextview.setText(student.getDob());
                        setDataFromDatabasePath("course", student.getCourseID() + "/courseName", courseTextview);
                        setDataFromDatabasePath("faculty", student.getFacultyID() + "/facultyName", facultyTextview);
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                }
        );
    }

    //just for course and faculty txtviews
    private void setDataFromDatabasePath(String ref, String childPath, final TextView txtView) {
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference dataRef = database.getReference(ref);
        dataRef.child(childPath).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                txtView.setText(dataSnapshot.getValue().toString());
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    //populate registered student information
    private void populateRegisteredStudentInfo() {
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        Log.e("hi", " i am pop reg");
        DatabaseReference studentsRef = database.getReference("student");
        studentsRef.child(studentId).addListenerForSingleValueEvent(
                new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        Student student = dataSnapshot.getValue(Student.class);

                        //preferred first name and gender
                        //non-compulsory fields
                        String preferFirstName = student.getPreferredFirstName();
                        if(!preferFirstName.isEmpty()) {
                            preferedNameEd.setText(preferFirstName);
                        }

                        String gender = student.getGender().toLowerCase();
                        if (!gender.isEmpty()) {
                          setRadioBtnCheck(gender, genderRadioGrp);
                        }

                        //best contact, degree, status,
                        //academic year, first language, country of birth
                        //compulsory fields
                        bestContactEd.setText(student.getBestContactNo());
                        setRadioBtnCheck(student.getStatus().toLowerCase(), statusRadioGrp);
                        academicYearSpnr.setSelection(getSelectedItemPosition(academicYearSpnr, String.valueOf(student.getYear())));
                        countrySpnr.setSelection(getSelectedItemPosition(countrySpnr, student.getCountryOfOrigin()));
                        firstLanguageSpnr.setSelection(getSelectedItemPosition(firstLanguageSpnr, student.getFirstLanguage()));

                        //For test, commnet in after degree to the database
                        //setRadioBtnCheck(student.getDegree());
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                }
        );
    }

    //for setting spinner selected item
    private int getSelectedItemPosition(Spinner spnr, String value) {
        int index = 0;
        for(int i = 0; i < spnr.getCount(); i++) {
            if(spnr.getItemAtPosition(i).equals(value)) {
                index = i;
            }
        }
        return index;
    }

    private void setRadioBtnCheck(String id, RadioGroup radioGroup) {
        RadioButton rdobtn = (RadioButton) radioGroup.findViewById(getResources().getIdentifier(id, "id", getPackageName()));
        rdobtn.setChecked(true);
    }

    //submit method to send data to database here? with validation
    private void submit() {
        if(!validate()) {
            Toast.makeText(RegisterActivity.this, "Please enter required information", Toast.LENGTH_SHORT).show();
            return;
        }

        //save the data to the database
        saveToDatabase();

        //either to directly move to main page or to display Toast message based on pre tag?
        moveToPageOnPreTag();
    }

    private boolean validate() {
        boolean valid = true;

        if(bestContactEd.getText().toString().isEmpty()) {
            valid = false;
        }

        //getCheckedRadioButtonId returns -1 if nothing is checked
        if(degreeRadioGrp.getCheckedRadioButtonId() == -1) {
            valid = false;
        } else {
        }

        if(statusRadioGrp.getCheckedRadioButtonId() == -1) {
            valid = false;
        }

        if(academicYearSpnr.getSelectedItem().toString().isEmpty()) {
            valid = false;
        }

        if(firstLanguageSpnr.getSelectedItem().toString().isEmpty()) {
            valid = false;
        }

        if(countrySpnr.getSelectedItem().toString().isEmpty()) {
            valid = false;
        }

        return valid;
    }

    //save to database
    private void saveToDatabase() {
        String preferredFirstName = preferedNameEd.getText().toString();
        String bestContact = bestContactEd.getText().toString();
        String academicYear = academicYearSpnr.getSelectedItem().toString();
        String firstLanguage = firstLanguageSpnr.getSelectedItem().toString();
        String country = countrySpnr.getSelectedItem().toString();
        //String degree = getStringFromRdoBtn(degreeRadioGrp);
        String status = getStringFromRdoBtn(statusRadioGrp);
        String gender = getStringFromRdoBtn(genderRadioGrp);

        Student student = new Student();
        student.setPreferredFirstName(preferredFirstName);
        student.setBestContactNo(bestContact);
        student.setGender(gender);
        student.setStatus(status);
        student.setYear(Integer.valueOf(academicYear));
        student.setFirstLanguage(firstLanguage);
        student.setCountryOfOrigin(country);

        //For test, comment in after fixing database
        //student.setDegree(degree);

        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference studentsRef = database.getReference("student");
        studentsRef.child(studentId).setValue(student);

        saveEducationalBackgroundToDatabase();
    }

    private void saveEducationalBackgroundToDatabase() {

        final ArrayList<EducationalBackground> educationalBackgrounds = getValidEdBackgounds();
        if(educationalBackgrounds.size() != 0) {
            FirebaseDatabase database = FirebaseDatabase.getInstance();
            final DatabaseReference educationalBackgroundRef = database.getReference("educationalBackground");

            Query query = educationalBackgroundRef.orderByChild("studentID").equalTo(studentId);
            query.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    if(dataSnapshot.hasChildren()) {
                        for(DataSnapshot ds : dataSnapshot.getChildren()) {
                            EducationalBackground ed = ds.getValue(EducationalBackground.class);
                            int index = getIndexForSameTypeEduBackground(educationalBackgrounds, ed);
                            if(index != -1) {
                                educationalBackgroundRef.child(ds.getKey()).setValue(educationalBackgrounds.get(index));
                                educationalBackgrounds.remove(index);
                            }
                        }
                    }


                    if(educationalBackgrounds.size() != 0) {
                        counterForEduback = 1;
                        educationalBackgroundRef.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                for(DataSnapshot ds : dataSnapshot.getChildren()) {
                                    if(counterForEduback == dataSnapshot.getChildrenCount()) {
                                        saveNewEdBackgrounds(educationalBackgrounds, ds.getKey());
                                    }
                                    counterForEduback++;
                                }
                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {

                            }
                        });
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });

        }
    }

    private void saveNewEdBackgrounds(ArrayList<EducationalBackground> edBackgrounds, String lastKey) {
        ArrayList<String> databaseKeys = new ArrayList<String>();

        FirebaseDatabase database = FirebaseDatabase.getInstance();
        final DatabaseReference educationalBackgroundRef = database.getReference("educationalBackground");

        for(int i = 0; i < edBackgrounds.size(); i++) {
            if(i == 0) {
                String nextKey = FirebaseNodeEntryGenerator.generateKey(lastKey);
                databaseKeys.add(nextKey);
            } else {
                String nextKeyy = FirebaseNodeEntryGenerator.generateKey(databaseKeys.get(i - 1));
                databaseKeys.add(nextKeyy);
            }
        }

        for(int i = 0; i < edBackgrounds.size(); i++) {
            educationalBackgroundRef.child(databaseKeys.get(i)).setValue(edBackgrounds.get(i));
        }

    }

    private int getIndexForSameTypeEduBackground(ArrayList<EducationalBackground> edBackgrounds, EducationalBackground ed) {
        int index = -1;
        for(int i = 0; i < edBackgrounds.size(); i++) {
            if(edBackgrounds.get(i).getType().equals(ed.getType().toLowerCase())) {
                index = i;
            }
        }
        return index;
    }

    private ArrayList<EducationalBackground> getValidEdBackgounds() {

        ArrayList<EducationalBackground> educationalBackgrounds = new ArrayList<EducationalBackground>();

        for(int i = 0; i < checkBoxLayout.getChildCount(); i++) {
            View childView = checkBoxLayout.getChildAt(i);
            if(childView instanceof CheckBox) {
                CheckBox childCheckBox = (CheckBox) childView;
                if(childCheckBox.isChecked()) {
                    String type = String.valueOf(childCheckBox.getText()).replace(" ", "").toLowerCase();
                    int checkBoxId = getResources().getIdentifier(type, "id", getPackageName());
                    String tag = getResources().getResourceName(checkBoxId);
                    EditText editTxt = (EditText) checkBoxLayout.findViewWithTag(tag);
                    String mark = String.valueOf(editTxt.getText());

                    EducationalBackground educationalBackground = new EducationalBackground();
                    educationalBackground.setStudentID(studentId);
                    educationalBackground.setType(type);
                    educationalBackground.setMark(mark);
                    educationalBackgrounds.add(educationalBackground);

                }
            }
        }
        return educationalBackgrounds;
    }

    private String getStringFromRdoBtn(RadioGroup rdoGrp) {
        int checkRdoBtnId = rdoGrp.getCheckedRadioButtonId();
        RadioButton rdoBtn = (RadioButton) rdoGrp.findViewById(checkRdoBtnId);
        return rdoBtn.getText().toString().toLowerCase();
    }

    private void populateEducationBackground() {
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference educationalBackgroundRef = database.getReference("educationalBackground");
        Query query = educationalBackgroundRef.orderByChild("studentID").equalTo(studentId);
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot.hasChildren()) {
                    for(DataSnapshot ds : dataSnapshot.getChildren()) {
                        EducationalBackground ed = ds.getValue(EducationalBackground.class);
                        setEducationalBackground(ed);

                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void setEducationalBackground(EducationalBackground ed) {
        if(!ed.getMark().isEmpty()) {
            String type = ed.getType().toLowerCase();
            int checkBoxId = getResources().getIdentifier(type, "id", getPackageName());
            String tag = getResources().getResourceName(checkBoxId);
            CheckBox checkBox = (CheckBox) findViewById(checkBoxId);
            checkBox.setChecked(true);
            addEditTextBelowCheckBox(checkBox);
            EditText edit = (EditText) checkBoxLayout.findViewWithTag(tag);
            edit.setText(ed.getMark());
        }
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
        Log.e("dd", checkBox.getResources().getResourceName(checkBox.getId()));
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
        Intent intent = new Intent(RegisterActivity.this, toClass);
        intent.putExtra(STUDENTID, studentId);
        startActivity(intent);
        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
    }

    //if previous activity is login, then move to main page directly
    //if previous activity is main page, then just display update success message
    private void moveToPageOnPreTag() {
        if(preActivityTag.equals(Tag.MAINACTIVITY.toString())) {
            moveTo(MainPageActivity.class);
        } else {
            Toast.makeText(RegisterActivity.this, "Successfully updated", Toast.LENGTH_SHORT).show();
        }
    }


}
