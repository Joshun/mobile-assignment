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
import com4510.thebestphotogallery.Database.UpdateImageMetadataListener;
import com4510.thebestphotogallery.ImageMetadataList;
import com4510.thebestphotogallery.R;
import com4510.thebestphotogallery.Tasks.UpdateImageMetadataTask;

/**
 * The activity for editing image name
 * Created by George on 08-Jan-18.
 */

public class EditNameActivity extends DetailsActivity implements UpdateImageMetadataListener {

    private Integer imageIndex;
    private ImageMetadata currentImageMetadata = null;
    private TextInputEditText nameInput;

    public EditNameActivity() {
        super("Edit Name", R.id.edit_name_toolbar);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setContentView(R.layout.activity_editname);
        super.onCreate(savedInstanceState);

        imageIndex = getIntent().getExtras().getInt("position");
        currentImageMetadata = (ImageMetadata) getIntent().getSerializableExtra("metadata");

        TextView oldName = findViewById(R.id.edit_name_text_old);
        nameInput = findViewById(R.id.edit_name_text);
        oldName.setText(currentImageMetadata.getTitle());
        nameInput.setText(currentImageMetadata.getTitle());
    }

    @Override
    protected void onResume() {
        super.onResume();

        //After 300 milliseconds, the input field is focused and the keyboard appears
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

    /**
     * Callback for when the metadata has successfully updated in the DB
     * @param imageMetadata the updated metadata
     */
    @Override
    public void imageUpdated(ImageMetadata imageMetadata) {
        Log.v(getClass().getName(), "Image " + imageMetadata.getFilePath() + " metadata update successful");
        Toast.makeText(getApplicationContext(), "Name Updated", Toast.LENGTH_SHORT).show();
        EditNameActivity.super.onBackPressed();
    }

    /**
     * Used in the XML onClick parameter for when the save button is pressed
     * @param view the button view
     */
    public void onSavePressed(View view) {
        onBackPressed();
    }

    /**
     * Method for when the back button is pressed
     */
    @Override
    public void onBackPressed() {
        if (currentImageMetadata != null) {
            //Updating the metadata with the new data
            currentImageMetadata.setTitle(nameInput.getText().toString());

            //Starting an asynchronous task to update the DB entry
            Log.v(getClass().getName(), "Updating metadata for image " + currentImageMetadata.getFilePath());
            UpdateImageMetadataTask updateImageMetadataTask = new UpdateImageMetadataTask(this);
            UpdateImageMetadataTask.UpdateMetadataParam updateMetadataParam = new UpdateImageMetadataTask.UpdateMetadataParam();
            updateMetadataParam.activity = this;
            updateMetadataParam.imageMetadata = currentImageMetadata;
            updateImageMetadataTask.execute(updateMetadataParam);

            //Passing the updated metadata to the parent activity
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
