package com.e.sdp.sdpapp;

import android.content.Context;
import android.content.Intent;
import android.graphics.Rect;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.AttributeSet;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;


import model.Faq;

public class MainPageActivity extends AppCompatActivity {

    private DrawerLayout drawerLayout;
    private NavigationView navigationView;
    private FragmentManager fragmentManager;
    private FragmentTransaction fragmentTransaction;
    private android.support.v7.widget.Toolbar toolbar;
    private static boolean logined = true;
    private String studentId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_page);

        setDrawer();
        setNavigationViewListener();
        setTabFragment();


        //get student database key here from intent??
        studentId = getIntent().getStringExtra("studentid");
    }

    @Override
    protected void onResume() {
        super.onResume();
        logined = true;
    }

    private void setDrawer() {
        drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        toolbar = (Toolbar) findViewById(R.id.toolbar);

        //set toggle for drawer
        ActionBarDrawerToggle drawerToggle = new ActionBarDrawerToggle(this,
                drawerLayout,
                toolbar,
                R.string.app_name,
                R.string.app_name);

        drawerLayout.addDrawerListener(drawerToggle);
        drawerToggle.syncState();
    }

    private void setTabFragment() {
        fragmentManager = getSupportFragmentManager();
        fragmentTransaction = fragmentManager.beginTransaction();
        TabFragment tabFragment = new TabFragment();
        fragmentTransaction.replace(R.id.drawer_containerview, tabFragment).commit();
    }

    //navigation view for the menus in the drawer
    private void setNavigationViewListener() {
        navigationView = (NavigationView) findViewById(R.id.drawer_navview);

        //set drawer item selected listener
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                int id = item.getItemId();

                //move to my information page
                if(id == R.id.drawer_myinformation) {
                    moveTo(RegisterActivity.class);
                }

                //move to faq page
                else if(id == R.id.drawer_faq) {
                    moveTo(FaqActivity.class);
                }

                //logout then move to login page
                else if(id == R.id.drawer_logout) {
                    logout();
                }
                return false;
            }
        });
    }


    //logout process and then move to login page
    private void logout() {
        logined = false;
        moveTo(MainActivity.class);
    }

    private void moveTo(Class toClass) {
        Intent intent = new Intent(MainPageActivity.this, toClass);
        intent.putExtra("caller", Tag.MAINPAGEACTIVITY.toString());
        intent.putExtra("studentid", studentId);

        //put student database key in intent here?? just for when users
        //go to my information page
        startActivity(intent);
        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
    }

    //Hide keyboard and lose focus of edittext of search view
    //when tap outside edittext
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

    //back button to home screen
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if(keyCode == KeyEvent.KEYCODE_BACK) {
            moveTaskToBack(true);
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    public static boolean isLogined() {
        return logined;
    }


}
