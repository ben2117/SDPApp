package com.e.sdp.sdpapp;

import android.app.DatePickerDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * Created by kisungtae on 15/09/2016.
 */
public class SearchFragment extends Fragment implements View.OnFocusChangeListener,
    TextWatcher {

    //the index of date and time in search bar menu
    private final static int TUTORPOSITION = 0;
    private final static int TOPICPOSITION = 1;
    private final static int DATEPOSITION = 2;
    private final static int TIMEPOSITION = 3;

    //to save current selected spinner item
    //otherwise we should get selected item every time text changes in the textchange listener?
    private static int SPINNERSTATE = 0;

    //variables for datepicker
    private int mYear, mMonth, mDay;

    private DatePickerDialog datePickerDialog;
    private EditText searchBarEdTxtview;
    private Spinner searchBarSpnr;
    private ImageView searchBarCancel;

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

        return searchView;
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
                    case TIMEPOSITION : {
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

            //logic to reset the filtered results here?

            //hide cancel btn in the search bar
            searchBarCancel.setVisibility(View.INVISIBLE);
        } else {
            searchBarCancel.setVisibility(View.VISIBLE);

            //filter on tutor name
            if(SPINNERSTATE == TUTORPOSITION) {
                //some logic here
            }

            //filter on topic
            else if(SPINNERSTATE == TOPICPOSITION) {
                //some logic here
            }
        }
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

    private void filterOnDates(int year, int month, int day) {
        //filter logic here?

        //test, remove me
        Toast.makeText(getActivity(), "filter on dates", Toast.LENGTH_SHORT).show();
    }

    private void setDateOnEditTextWith(int year, int month, int day) {
        Calendar calendar = Calendar.getInstance();
        calendar.set(year, month, day);
        SimpleDateFormat format = new SimpleDateFormat("dd-MM-yyyy");
        String date = format.format(calendar.getTime());
        searchBarEdTxtview.setText(date);
    }

    //for time and date to show dialog when edit text is focused
    @Override
    public void onFocusChange(View v, boolean hasFocus) {
        int selectedItemPosition = searchBarSpnr.getSelectedItemPosition();
        if(hasFocus) {
            if(selectedItemPosition == DATEPOSITION) {
                showDatePicker();
            }
            else if (selectedItemPosition == TIMEPOSITION) {
                //show time dialog?
            }
        }
    }

}
