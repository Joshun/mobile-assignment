package com4510.thebestphotogallery.Activities;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;

import com4510.thebestphotogallery.R;

/**
 * Parent activity for details-based activities
 * Created by joshua on 29/12/17.
 */

public abstract class DetailsActivity extends AppCompatActivity {

    private String title;
    private int toolbarId;

    public DetailsActivity(String title, int id) {
        this.title = title;
        this.toolbarId = id;
    }
    public DetailsActivity() {
        this("", -1);
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //Details activities all have a unique title
        Toolbar toolbar = findViewById(toolbarId);
        Log.v("Toolbar", toolbar == null ? "true" : "false");
        if (toolbar != null) {
            toolbar.setTitle(title);
            setSupportActionBar(toolbar);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
    }
}
