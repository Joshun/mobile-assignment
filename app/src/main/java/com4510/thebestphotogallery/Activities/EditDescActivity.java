package com4510.thebestphotogallery.Activities;

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
import com4510.thebestphotogallery.Listeners.UpdateImageMetadataListener;
import com4510.thebestphotogallery.R;
import com4510.thebestphotogallery.Tasks.UpdateImageMetadataTask;

/**
 * Created by George on 08-Jan-18.
 */

public class EditDescActivity extends DetailsActivity implements UpdateImageMetadataListener {

    private Integer imageIndex;
    private ImageMetadata currentImageMetadata = null;
    private TextInputEditText descriptionInput;

    public EditDescActivity() {
        super("Edit Description", R.id.edit_description_toolbar);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setContentView(R.layout.activity_editdescription);
        super.onCreate(savedInstanceState);

        imageIndex = getIntent().getExtras().getInt("position");
        currentImageMetadata = (ImageMetadata) getIntent().getSerializableExtra("metadata");
        TextView oldName = findViewById(R.id.edit_description_text_old);
        descriptionInput = findViewById(R.id.edit_description_text);
        oldName.setText(currentImageMetadata.getDescription());
        descriptionInput.setText(currentImageMetadata.getDescription());
    }

    @Override
    protected void onResume() {
        super.onResume();
        descriptionInput.postDelayed(new Runnable() {
            public void run() {
                descriptionInput.setFocusableInTouchMode(true);
                descriptionInput.requestFocusFromTouch();
                descriptionInput.setSelection(descriptionInput.getText().length());
                InputMethodManager lManager = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
                lManager.showSoftInput(descriptionInput, 0);
            }
        }, 300);
    }

    @Override
    public void imageUpdated(ImageMetadata imageMetadata) {
        Log.v(getClass().getName(), "Image " + imageMetadata.getFilePath() + " metadata update successful");
        Toast.makeText(getApplicationContext(), "Description Updated", Toast.LENGTH_SHORT).show();
        EditDescActivity.super.onBackPressed();
    }

    public void onSavePressed(View view) {
        onBackPressed();
    }

    @Override
    public void onBackPressed() {
        if (currentImageMetadata != null) {
            currentImageMetadata.setDescription(descriptionInput.getText().toString());

            Log.v(getClass().getName(), "Updating metadata for image " + currentImageMetadata.getFilePath());
            UpdateImageMetadataTask updateImageMetadataTask = new UpdateImageMetadataTask(this);
            UpdateImageMetadataTask.UpdateMetadataParam updateMetadataParam = new UpdateImageMetadataTask.UpdateMetadataParam();
            updateMetadataParam.activity = this;
            updateMetadataParam.imageMetadata = currentImageMetadata;
            updateImageMetadataTask.execute(updateMetadataParam);

            Intent intent = new Intent();
            intent.putExtra("metadata", currentImageMetadata);
            setResult(RESULT_OK, intent);
            finish();
        }
        super.onBackPressed();
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
