package com.e.sdp.sdpapp;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Rect;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import butterknife.Bind;
import butterknife.ButterKnife;
import model.Session;
import model.Student;

//make sure sdk tools are updated including google play services
import com.google.firebase.database.*;

public class MainActivity extends AppCompatActivity {

    private ProgressDialog progressDialog;

    //intent key for caller activity
    private static final String CALLER = "caller";
    private static final String STUDENTID = "studentid";


    private final FirebaseDatabase database = FirebaseDatabase.getInstance();

    //bind email and password edit text and login btn
    @Bind(R.id.studentID_edtext) EditText studentIdEdText;
    @Bind(R.id.password_edtext) EditText passwordEdText;
    @Bind(R.id.login_btn) Button loginBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //butterknife binds this view to use @Bind()
        ButterKnife.bind(this);

        //set login button listener
        loginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                login();
            }
        });
    }

    private void login() {
        //if client side validation fails, then stop
        if(!clientSideValidate()) {
            //set error here as we dont want to make it known if a password is wrong or if a user name is wrong ~ Tex
            studentIdEdText.setError("Enter a valid student ID");
            passwordEdText.setError("Enter a valid password");
            //end of tex's silly buggers
            return;
        }

        //before server side validate(), start processDialog
        showProcessDialog();

        //server side validation
        final DatabaseReference studentsRef = database.getReference("student");
        final DatabaseReference prePopStudentsRef = database.getReference("prePopStudent");
        final String studentIDInput = studentIdEdText.getText().toString();
        final String passwordInput = passwordEdText.getText().toString();

        studentsRef.addListenerForSingleValueEvent(
                new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        //look through students in our database first
                        for(DataSnapshot child : dataSnapshot.getChildren()){
                            if(child.getKey().equals(studentIDInput)){
                                Student student = child.getValue(Student.class);

                                //for test remove me.
                                progressDialog.dismiss();
                                moveTo(MainPageActivity.class, studentIDInput);
                                return;

                                //why this is null fix me...
                                //for test, remove me
                               // Log.e("dddd", student.getPassword());
                                /*
                                if (student.getPassword().equals(passwordInput)) {
                                    progressDialog.dismiss();
                                    moveTo(MainPageActivity.class, studentIDInput);
                                    return;
                                } else {
                                    displayLoginFailMsg();
                                    return;
                                }
                                */
                            }
                        }
                        prePopStudentsRef.addListenerForSingleValueEvent(
                                new ValueEventListener() {
                                    @Override
                                    public void onDataChange(DataSnapshot dataSnapshot) {
                                        //if no students are found, look through pre pop students
                                        for(DataSnapshot child : dataSnapshot.getChildren()) {
                                            if(child.getKey().equals(studentIDInput)){
                                                Student student = child.getValue(Student.class);
                                                if (student.getPassword().equals(passwordInput)) {
                                                    progressDialog.dismiss();
                                                    moveTo(RegisterActivity.class, studentIDInput);
                                                    return;
                                                } else {
                                                    displayLoginFailMsg();
                                                    return;
                                                }
                                            }

                                        }
                                        //if id matches nothing, then display login fail
                                        displayLoginFailMsg();
                                    }

                                    @Override
                                    public void onCancelled(DatabaseError databaseError) {

                                    }
                                }
                        );
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                }
        );
    }

    private void displayLoginFailMsg() {
        progressDialog.dismiss();
        Toast.makeText(MainActivity.this, "Check your id and passwords", Toast.LENGTH_SHORT).show();
    }

    private void showProcessDialog() {
        progressDialog = new ProgressDialog(MainActivity.this, R.style.AppTheme_ProcessDialog);
        progressDialog.setIndeterminate(false);
        progressDialog.setProgressStyle(android.R.style.Widget_ProgressBar_Small);
        progressDialog.setCancelable(false);
        progressDialog.show();
    }

    //client validate can be put into 1 if statement and doesnt need the boolean "valid"
    private boolean clientSideValidate() {
        boolean valid = true;

        String studentID = studentIdEdText.getText().toString();
        String password = passwordEdText.getText().toString();

        //
        if(studentID.length() > 10 || studentID.length() < 8) {
            //studentIdEdText.setError("Enter a valid student ID");
            valid = false;
        } else {
            studentIdEdText.setError(null);
        }

        //we can add validation for special characters like "@" symbol
        if(password.length() > 16 || password.length() < 8) {
           // passwordEdText.setError("Enter a valid password");
            valid = false;
        } else {
            //Added a .contains checker for non valid cases such as @ ~ Tex
            String invalidChars = "!@#$%^&*()_+-=";
            for(int i = 0; i < password.length();i++){
                if(invalidChars.contains(Character.toString(password.charAt(i)))){
                    //passwordEdText.setError("bad input!");
                    valid = false;

                }


            }

            //end of tex's sillybuggers

            if(valid){
                passwordEdText.setError(null);
            }

        }

        //-----start test code, remove me ---------------
        //valid = true;


        return valid;
    }

    //move to another activity with slide animation
    private void moveTo(Class toClass, String studentID){
        Intent intent = new Intent(MainActivity.this, toClass);
        intent.putExtra(CALLER, Tag.MAINACTIVITY.toString());

        //put student database key in intent here?
        intent.putExtra(STUDENTID, studentID);

        startActivity(intent);
        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
    }

    //to prevent back button to navigate back to main page
    //when users are logged out and in login page
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if(keyCode == KeyEvent.KEYCODE_BACK) {
            moveTaskToBack(true);
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    //close the keyboard when tab outside edittext
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
}

