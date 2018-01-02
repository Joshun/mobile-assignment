package com4510.thebestphotogallery.Activities;

import android.os.Bundle;

import com4510.thebestphotogallery.R;

/**
 * Created by George on 02-Jan-18.
 */

public class EditDetailsActivity extends DetailsActivity {

    public EditDetailsActivity() {
        super("Edit Details", R.id.editimagedetails_toolbar);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_editimagedetails);
    }

}
