package com4510.thebestphotogallery.Activities;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TextInputEditText;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.DatePicker;
import android.widget.Filter;
import android.widget.TextView;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

import com4510.thebestphotogallery.R;

public class FilterSelectActivity extends AppCompatActivity {

    private Calendar startDate = Calendar.getInstance();
    private Calendar endDate = Calendar.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_filter_select);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        final TextInputEditText startDateEntry = findViewById(R.id.startdate_entry);
        TextInputEditText endDateEntry = findViewById(R.id.enddate_entry);

        startDateEntry.setKeyListener(null);
        endDateEntry.setKeyListener(null);


        // set start date to yesterday's date
        startDate.add(Calendar.DATE, -1);

        setupDatePicker(startDateEntry, startDate);
        setupDatePicker(endDateEntry, endDate);

    }

    private void setupDatePicker(final TextInputEditText textInput, final Calendar date) {
        final Activity that = this;
        final DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");


        textInput.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DatePickerDialog datePickerDialog = new DatePickerDialog(that, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                        date.set(year, month, dayOfMonth);
                        textInput.setText(dateFormat.format(date.getTime()));
                    }
                }, date.get(Calendar.YEAR),date.get(Calendar.MONTH), date.get(Calendar.DAY_OF_MONTH));
                datePickerDialog.show();
            }
        });

        textInput.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) textInput.performClick();
            }
        });


        textInput.setText(dateFormat.format(date.getTime()));
    }


}
