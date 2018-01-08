package com4510.thebestphotogallery.Activities;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.design.widget.TextInputEditText;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Toast;

import com4510.thebestphotogallery.Database.ImageMetadata;
import com4510.thebestphotogallery.Database.UpdateImageMetadataListener;
import com4510.thebestphotogallery.ImageMetadataList;
import com4510.thebestphotogallery.MyAdapter;
import com4510.thebestphotogallery.R;
import com4510.thebestphotogallery.Tasks.UpdateImageMetadataTask;

/**
 * Created by George on 02-Jan-18.
 */

public class EditDetailsActivity extends DetailsActivity implements UpdateImageMetadataListener {

    private ImageMetadata currentImageMetadata = null;
    private TextInputEditText nameInput;
    private Integer imageIndex = null;

    public EditDetailsActivity() {
        super("Edit Details", R.id.editimagedetails_toolbar);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setContentView(R.layout.activity_editimagedetails);
        super.onCreate(savedInstanceState);

//        currentImageMetadata = (ImageMetadata) getIntent().getSerializableExtra("metadata");
        imageIndex = getIntent().getExtras().getInt("position");
        currentImageMetadata = ImageMetadataList.getInstance().get(imageIndex);

        nameInput = findViewById(R.id.edit_name_text);
        TextInputEditText descInput = findViewById(R.id.edit_description_text);
        nameInput.setText(currentImageMetadata.getTitle());
        descInput.setText(currentImageMetadata.getDescription());
    }

    @Override
    protected void onResume() {
        super.onResume();
        nameInput.postDelayed(new Runnable() {
            public void run() {
                nameInput.setFocusableInTouchMode(true);
                nameInput.requestFocusFromTouch();
                nameInput.setSelection(nameInput.getText().length());
                InputMethodManager lManager = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
                lManager.showSoftInput(nameInput, 0);
            }
        }, 300);
    }

    @Override
    public void imageUpdated(ImageMetadata imageMetadata) {
        Log.v(getClass().getName(), "Image " + imageMetadata.getFilePath() + " metadata update successful");
        final EditDetailsActivity that = this;

        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(getApplicationContext(), "Image details updated", Toast.LENGTH_SHORT).show();
                EditDetailsActivity.super.onBackPressed();
            }
        });
    }

    @Override
    public void onBackPressed() {
        if (currentImageMetadata != null) {
            TextInputEditText nameInput = findViewById(R.id.edit_name_text);
            TextInputEditText descInput = findViewById(R.id.edit_description_text);
            currentImageMetadata.setTitle(nameInput.getText().toString());
            currentImageMetadata.setDescription(descInput.getText().toString());

            Log.v(getClass().getName(), "Updating metadata for image " + currentImageMetadata.getFilePath());
            UpdateImageMetadataTask updateImageMetadataTask = new UpdateImageMetadataTask(this);
            UpdateImageMetadataTask.UpdateMetadataParam updateMetadataParam = new UpdateImageMetadataTask.UpdateMetadataParam();
            updateMetadataParam.activity = this;
            updateMetadataParam.imageMetadata = currentImageMetadata;
            updateImageMetadataTask.execute(updateMetadataParam);
        }
//        super.onBackPressed();
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
