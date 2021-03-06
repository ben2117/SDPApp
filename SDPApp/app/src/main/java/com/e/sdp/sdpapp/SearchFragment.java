package com.e.sdp.sdpapp;

import android.app.DatePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.util.LongSparseArray;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ExpandableListView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.concurrent.ExecutionException;

import model.Class;
import model.Session;
import model.Workshop;

/**
 * Created by kisungtae on 15/09/2016.
 */
public class SearchFragment extends Fragment implements View.OnFocusChangeListener,
    TextWatcher {

    //the index of date and time in search bar menu
    private final static int TUTORPOSITION = 0;
    private final static int TOPICPOSITION = 1;
    private final static int DATEPOSITION = 2;
    private final static int LOCATIONPOSITION = 3;

    //to save current selected spinner item
    private static int SPINNERSTATE = 0;

    //variables for datepicker
    private int mYear, mMonth, mDay;

    private DatePickerDialog datePickerDialog;
    private EditText searchBarEdTxtview;
    private Spinner searchBarSpnr;
    private ImageView searchBarCancel;
    private ExpandableListView exListView;
    private ListView searchFilterListView;
    private ArrayList<Session> sessions;
    private ArrayList<Session> filteredSessions;
    private SearchFilterListViewAdapter searchFilterListViewAdapter;


    private static final String SESSIONKEY = "sessionKey";
    private static final String CALLER = "caller";
    private static final String STUDENTID = "studentid";

    //for test remove or leave me
    private int counter = 0;
    private long workshopDsSize;
    private String studentId;


    //for test remove me or leave


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View searchView = inflater.inflate(R.layout.search_fragment_view, null);

        bindViews(searchView);

        //set search filter spinner in the search bar
        setSpinner();

        //set listeners
        setSearchBarCancelListener();

        searchBarEdTxtview.addTextChangedListener(this);
        searchBarEdTxtview.setOnFocusChangeListener(this);

        studentId = getActivity().getIntent().getStringExtra(STUDENTID);


        final ArrayList<Workshop> workshops = new ArrayList<>();
        sessions = new ArrayList<>();


        FirebaseDatabase database = FirebaseDatabase.getInstance();
        final DatabaseReference workshospRef = database.getReference("workshop");
        final DatabaseReference sessionsRef = database.getReference("session");

        workshospRef.addListenerForSingleValueEvent(
                new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        workshopDsSize = dataSnapshot.getChildrenCount();
                        for(DataSnapshot child : dataSnapshot.getChildren()) {
                            final Workshop workshop = child.getValue(Workshop.class);
                            final String workshopId = child.getKey();
                            sessionsRef.addListenerForSingleValueEvent(
                                    new ValueEventListener() {
                                        @Override
                                        public void onDataChange(DataSnapshot dataSnapshot) {
                                            for (DataSnapshot child : dataSnapshot.getChildren()) {
                                                final Session session = child.getValue(Session.class);

                                                //for search filer list view


                                                if (session.getWorkshopID().equals(workshopId)) {
                                                    final String sessionId = child.getKey();
                                                    session.setSeesionId(sessionId);
                                                    workshop.addSession(session);
                                                    sessions.add(session);
                                                }
                                            }
                                            workshops.add(workshop);
                                            counter++;
                                            if(workshopDsSize == counter) {
                                                setWorkshopList(workshops, sessions);
                                                counter = 0;
                                            }
                                            //Log.e("workshop list size", workshops.size());
                                        }

                                        @Override
                                        public void onCancelled(DatabaseError databaseError) {
                                        }
                                    }
                            );

                        }

                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                    }
                }
        );
        return searchView;
    }

    private void setWorkshopList(ArrayList<Workshop> _workshops, ArrayList<Session> _sessions) {
        SearchExpndAdapter searchExpndAdapter = new SearchExpndAdapter(getActivity(), _workshops);
        exListView.setAdapter(searchExpndAdapter);
        exListView.setOnChildClickListener(new ExpandableListView.OnChildClickListener() {
            @Override
            public boolean onChildClick(ExpandableListView parent, View v, int groupPosition, int childPosition, long id) {
                moveTo(SessionDetailActivity.class, v.getTag().toString());
                return false;
            }
        });


        //searchil filter listview
        filteredSessions = new ArrayList<Session>(_sessions);
        filteredSessions = new ArrayList<>();
        searchFilterListViewAdapter = new SearchFilterListViewAdapter(getActivity(), filteredSessions);
        searchFilterListView.setAdapter(searchFilterListViewAdapter);
        searchFilterListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                moveTo(SessionDetailActivity.class, view.getTag().toString());
            }
        });
    }

    private void setSearchBarCancelListener() {
        searchBarCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                searchBarEdTxtview.getText().clear();
            }
        });
    }

    private void bindViews(View searchView) {
        searchBarEdTxtview = (EditText) searchView.findViewById(R.id.search_bar_edtxtview);
        searchBarSpnr = (Spinner) searchView.findViewById(R.id.search_bar_spinner);
        searchBarCancel = (ImageView) searchView.findViewById(R.id.search_bar_cancel_imgview);
        exListView = (ExpandableListView) searchView.findViewById(R.id.search_listview);
        searchFilterListView = (ListView) searchView.findViewById(R.id.search_fragment_searchfilter_listview);
    }

    //set spinner with adapter
    private void setSpinner() {
        //if you want, you can use and override the style of simple drop down item for the spinner
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(getActivity().getApplicationContext(),
                R.array.search_menus,
                R.layout.spinner_row);
        searchBarSpnr.setAdapter(adapter);

        //set item selected listener for the spinner
        searchBarSpnr.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                int selectedItemPosition = parent.getSelectedItemPosition();

                switch (selectedItemPosition) {
                    case TUTORPOSITION : {
                        setSpinnerState(selectedItemPosition, getString(R.string.defaultSearchBarHint));
                        break;
                    }
                    case TOPICPOSITION : {
                        setSpinnerState(selectedItemPosition, getString(R.string.defaultSearchBarHint));
                        break;
                    }
                    case DATEPOSITION : {
                        setSpinnerState(selectedItemPosition, getString(R.string.dateSearchBarHint));
                        break;
                    }
                    case LOCATIONPOSITION : {
                        setSpinnerState(selectedItemPosition, getString(R.string.timeSearchBarHint));
                        break;
                    }
                    default: {}
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    private void setSpinnerState(int seletedItemPosition, String txtViewHint) {
        SPINNERSTATE = seletedItemPosition;
        searchBarEdTxtview.getText().clear();
        searchBarEdTxtview.setHint(txtViewHint);
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

    }

    @Override
    public void afterTextChanged(Editable s) {

    }

    //text change listener for edit text in the search bar
    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {

        //reset the search filter if text is ""
        if(searchBarEdTxtview.getText().toString().isEmpty()) {

            //hide cancel btn in the search bar
            searchBarCancel.setVisibility(View.INVISIBLE);
            searchFilterListView.setVisibility(View.INVISIBLE);
            exListView.setVisibility(View.VISIBLE);
        } else {
            exListView.setVisibility(View.INVISIBLE);
            searchFilterListView.setVisibility(View.VISIBLE);
            searchBarCancel.setVisibility(View.VISIBLE);
            filterOn();

        }
    }

    private void filterOn() {
        String searchKeyword = searchBarEdTxtview.getText().toString().toLowerCase();

        if(filteredSessions.size() != 0) {
            filteredSessions.clear();
        }

        //filter on tutor name
        if(SPINNERSTATE == TUTORPOSITION) {
            for(Session session : sessions) {
                if(session.getTutor().replace(" ","").toLowerCase().contains(searchKeyword)) {
                    filteredSessions.add(session);
                }
            }
        }

        //filter on topic
        else if(SPINNERSTATE == TOPICPOSITION) {
            for(Session session : sessions) {
                if(session.getTitle().replace(" ", "").toLowerCase().contains(searchKeyword)) {
                    filteredSessions.add(session);
                }
            }
        }

        //filter on location
        else if(SPINNERSTATE == LOCATIONPOSITION) {
            for(Session session : sessions) {
                if(session.getLocation().replace(" ", "").toLowerCase().contains(searchKeyword)) {
                    filteredSessions.add(session);
                }
            }
        }

        searchFilterListViewAdapter.notifyDataSetChanged();

    }

    private void showDatePicker() {
        setDatePickerDialog();
        setDatePickerButtons();
        datePickerDialog.show();
    }

    private void setDatePickerDialog() {
        datePickerDialog = new DatePickerDialog(getActivity(), null, mYear, mMonth, mDay);
        //set minimum date as today
        datePickerDialog.getDatePicker().setMinDate(new Date().getTime());
        datePickerDialog.setCancelable(true);
        datePickerDialog.setCanceledOnTouchOutside(true);
    }

    private void setDatePickerButtons() {
        //edittext in search bar loses its focus when back button is pressed in the date dialog context
        datePickerDialog.setOnKeyListener(new DialogInterface.OnKeyListener() {
            @Override
            public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {
                if(keyCode == KeyEvent.KEYCODE_BACK) {
                    searchBarEdTxtview.clearFocus();
                }
                return false;
            };
        });

        //outside touch to cancel, not to fire anything
        //cancel button to make date picker clearer to cancel, remove or customize it if you want
        //onclick is fired when ok button is clicked
        datePickerDialog.setButton(DialogInterface.BUTTON_POSITIVE, "OK",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        searchBarEdTxtview.clearFocus();
                        searchBasedOnSelectedDate();
                    }
                });

        datePickerDialog.setButton(DialogInterface.BUTTON_NEGATIVE, "Cancel",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        searchBarEdTxtview.clearFocus();
                    }
                });
    }

    private void searchBasedOnSelectedDate() {
        //i think setMinDate(new Date().getTime()) throws error at second time
        //when i save selected dates to global variables of yeaer, month, and day.
        DatePicker dp = datePickerDialog.getDatePicker();
        int year = dp.getYear();
        int month = dp.getMonth();
        int day = dp.getDayOfMonth();


        //date format and display in edit text
        setDateOnEditTextWith(year, month, day);

        //filter results on dates
        filterOnDates(year, month, day);

    }

    private void filterOnDates(final int year, final int month, final int day) {
        Toast.makeText(getActivity(), "filter on dates", Toast.LENGTH_SHORT).show();
        FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();

        if(filteredSessions.size() != 0) {
            filteredSessions.clear();
        }


        DatabaseReference classRef = firebaseDatabase.getReference("class");
        classRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                try {
                    SimpleDateFormat format = new SimpleDateFormat("dd/MM/yyyy");
                    Date selectedDate = format.parse(getFormattedDate(year, month, day));
                    String preSessionKey = "";
                    for(DataSnapshot ds : dataSnapshot.getChildren()) {
                        Class currentClass = ds.getValue(Class.class);
                        if(currentClass.getDateObject().equals(selectedDate)) {
                            if(!preSessionKey.equals(currentClass.getSessionID())) {
                                preSessionKey = currentClass.getSessionID();
                                Session session = getSessionFromArrayWithId(currentClass.getSessionID());
                                if(session != null) {
                                    filteredSessions.add(session);
                                }

                            }
                        }
                    }

                    searchFilterListViewAdapter.notifyDataSetChanged();
                } catch (Exception e) {

                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private Session getSessionFromArrayWithId(String sessionId) {
        for(Session session : sessions) {
            if(session.getSessionId().equals(sessionId)) {
                return session;
            }
        }
        return null;
    }

    private void setDateOnEditTextWith(int year, int month, int day) {
        String date = getFormattedDate(year, month, day);
        searchBarEdTxtview.setText(date);
    }

    private String getFormattedDate(int year, int month, int day) {
        Calendar calendar = Calendar.getInstance();
        calendar.set(year, month, day);
        SimpleDateFormat format = new SimpleDateFormat("dd/MM/yyyy");
        String formattedDate = format.format(calendar.getTime());
        return formattedDate;
    }

    //for time and date to show dialog when edit text is focused
    @Override
    public void onFocusChange(View v, boolean hasFocus) {
        int selectedItemPosition = searchBarSpnr.getSelectedItemPosition();
        if(hasFocus) {
            if(selectedItemPosition == DATEPOSITION) {
                showDatePicker();
            }
        }
    }

    private void moveTo(java.lang.Class toClass, String sessionKey){
        Intent intent = new Intent(getActivity(), toClass);
        intent.putExtra(STUDENTID, studentId);
        intent.putExtra(CALLER, Tag.SEARCHFRAGMENT.toString());
        intent.putExtra(SESSIONKEY, sessionKey);
        startActivity(intent);
        getActivity().overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
    }
}
