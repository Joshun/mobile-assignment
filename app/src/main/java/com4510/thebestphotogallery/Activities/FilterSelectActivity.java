package com4510.thebestphotogallery.Activities;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.TextInputEditText;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;

import com4510.thebestphotogallery.R;

/**
 * The activity for altering the date filter
 * Created by joshua on 11/01/17.
 */

public class FilterSelectActivity extends AppCompatActivity {

    private Calendar startDate;
    private Calendar endDate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_filter_select);

        //Setting up toolbar
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        if (toolbar != null) {
            setSupportActionBar(toolbar);
            getSupportActionBar().setTitle("Filter by Date");
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
        else {
            Log.w(getClass().getName(), "toolbar is null");
        }

        //Grabbing views
        TextInputEditText startDateEntry = findViewById(R.id.startdate_entry);
        TextInputEditText endDateEntry = findViewById(R.id.enddate_entry);
        Button filterActionBtn = findViewById(R.id.filter_action_btn);

        // disable keyboard input since we only want input from the datepickers
        startDateEntry.setKeyListener(null);
        endDateEntry.setKeyListener(null);

        Calendar currentStartDate = (Calendar) getIntent().getExtras().get("startDate");
        Calendar currentEndDate = (Calendar) getIntent().getExtras().get("endDate");

        // if the filters were not previously set, set them to be over the last day
        if (currentStartDate == null || currentEndDate == null) {
            startDate = Calendar.getInstance();
            endDate = Calendar.getInstance();
            // set start date to yesterday's date
            startDate.add(Calendar.DATE, -1);
        }
        // otherwise, set them to the previous values
        else {
            startDate = currentStartDate;
            endDate = currentEndDate;
        }

        setupDatePicker(startDateEntry, startDate);
        setupDatePicker(endDateEntry, endDate);

        final Activity activity = this;

        // when filter button is clicked, validate dates and return to MainActivity if vailid
        filterActionBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // if date is not valid, show dialog,
                if (endDate.before(startDate)) {
                    AlertDialog dialog = new AlertDialog.Builder(activity)
                            .setTitle("Cannot have end before start!")
                            .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.cancel();
                                }
                            })
                            .create();
                    dialog.show();
                }
                // if valid, return back to MainActivity with filtering options
                else {
                    Intent intent = new Intent();
                    intent.putExtra("startDate", startDate);
                    intent.putExtra("endDate", endDate);
                    setResult(RESULT_OK, intent);
                    finish();
                }
            }
        });
    }


    private void setupDatePicker(final TextInputEditText textInput, final Calendar date) {
        final Activity activity = this;
        // converts Date object to formatted date string
        final DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");


        textInput.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DatePickerDialog datePickerDialog = new DatePickerDialog(activity, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                        date.set(year, month, dayOfMonth);
                        textInput.setText(dateFormat.format(date.getTime()));
                    }
                }, date.get(Calendar.YEAR),date.get(Calendar.MONTH), date.get(Calendar.DAY_OF_MONTH));
                datePickerDialog.show();
            }
        });

        // override onFocusChangeListener - we want entering focus event to behave as a click instead
        textInput.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) textInput.performClick();
            }
        });


        // set the TextInput to the provided date
        textInput.setText(dateFormat.format(date.getTime()));
    }

    @Override
    public void onBackPressed() {
        // if back is pressed, we want filtering to be cancelled
        Intent intent = new Intent();
        setResult(RESULT_CANCELED, intent);
        finish();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
