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
    private final FirebaseDatabase database = FirebaseDatabase.getInstance();

    //bind email and password edit text and login btn
    @Bind(R.id.studentID_edtext) EditText studentIdEdText;
    @Bind(R.id.password_edtext) EditText passwordEdText;
    @Bind(R.id.login_btn) Button loginBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        final FirebaseDatabase database = FirebaseDatabase.getInstance();

        DatabaseReference studentsRef = database.getReference("student");
        DatabaseReference sessionsRef = database.getReference("session");

        studentsRef.child("S00001").addListenerForSingleValueEvent(
                new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        Student student = dataSnapshot.getValue(Student.class);
                        Log.d("student i am", student.getName());
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
        // Query q = myRef.orderByKey();
        Log.d("hello", "hi");

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
            return;
        }

        //before server side validate(), start processDialog
        showProcessDialog();

        DatabaseReference studentsRef = database.getReference("student");

        studentsRef.child("S00001").addListenerForSingleValueEvent(
                new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        Student student = dataSnapshot.getValue(Student.class);
                        Log.e("FROM ME", student.getName());

                        if(!serverSideValidate()) {
                            progressDialog.dismiss();
                            Toast.makeText(MainActivity.this, getResources().getString(R.string.loginFailMsg), Toast.LENGTH_SHORT).show();
                            return;
                        }

                        //if server-side validation succeeds, then check registration of a student with HELPS
                        if(isRegistered()) {
                            //move directly to main page
                            progressDialog.dismiss();
                            moveTo(MainPageActivity.class);
                        } else {
                            //move to registration page
                            progressDialog.dismiss();
                            moveTo(RegisterActivity.class);
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                }
        );



    }

    private void showProcessDialog() {
        progressDialog = new ProgressDialog(MainActivity.this, R.style.AppTheme_ProcessDialog);
        progressDialog.setIndeterminate(false);
        progressDialog.setProgressStyle(android.R.style.Widget_ProgressBar_Small);
        progressDialog.setCancelable(false);
        progressDialog.show();
    }

    private boolean clientSideValidate() {
        boolean valid = true;

        String studentID = studentIdEdText.getText().toString();
        String password = passwordEdText.getText().toString();

        //
        if(studentID.length() > 10 || studentID.length() < 8) {
            studentIdEdText.setError("Enter a valid student ID");
            valid = false;
        } else {
            studentIdEdText.setError(null);
        }

        //we can add validation for special characters like "@" symbol
        if(password.length() > 16 || password.length() < 8) {
            passwordEdText.setError("Enter a valid password");
            valid = false;
        } else {
            passwordEdText.setError(null);
        }

        //-----start test code, remove me ---------------
        valid = true;


        return valid;
    }

    private boolean serverSideValidate() {
        boolean valid = true;

        //server side validation firebaseAuth.signInWithEmailAndPassword(email, password)??
        //task.isSucessful() --> valid = false;??
        //try and catch ???
        //need separate UTS student database ??

        return valid;
    }

    //check registration of a student with HELPS
    private boolean isRegistered() {
        boolean registration = false;

        return registration;
    }

    //move to another activity with slide animation
    private void moveTo(Class toClass){
        Intent intent = new Intent(MainActivity.this, toClass);
        intent.putExtra(CALLER, Tag.MAINACTIVITY.toString());

        //put student database key in intent here?


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

