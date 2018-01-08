package com4510.thebestphotogallery.Activities;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.TextInputEditText;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.TextView;
import android.widget.Toast;

import com4510.thebestphotogallery.Database.ImageMetadata;
import com4510.thebestphotogallery.Database.UpdateImageMetadataListener;
import com4510.thebestphotogallery.MyAdapter;
import com4510.thebestphotogallery.R;
import com4510.thebestphotogallery.Tasks.UpdateImageMetadataTask;

/**
 * Created by George on 02-Jan-18.
 */

public class EditDetailsActivity extends DetailsActivity {

    private ImageMetadata currentImageMetadata = null;
    private TextView nameInput;

    public EditDetailsActivity() {
        super("Edit Details", R.id.editimagedetails_toolbar);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setContentView(R.layout.activity_editimagedetails);
        super.onCreate(savedInstanceState);

        currentImageMetadata = (ImageMetadata) getIntent().getSerializableExtra("metadata");

        nameInput = findViewById(R.id.btn_edit_name_text);
//        TextInputEditText descInput = findViewById(R.id.edit_description_text);
        nameInput.setText(currentImageMetadata.getTitle());
//        descInput.setText(currentImageMetadata.getDescription());
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

    public void btnSelected(View view) {
        Intent intent;
        switch (view.getId()) {
            case R.id.btn_name:
                intent = new Intent(this, EditNameActivity.class);
            default:
                intent = new Intent(this, EditNameActivity.class);
        }
        intent.putExtra("metadata", currentImageMetadata);
        startActivity(intent);
    }

}
