package com4510.thebestphotogallery.Activities;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.design.widget.TextInputEditText;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com4510.thebestphotogallery.Database.ImageMetadata;
import com4510.thebestphotogallery.Database.UpdateImageMetadataListener;
import com4510.thebestphotogallery.ImageMetadataList;
import com4510.thebestphotogallery.Listeners.ServerResponseListener;
import com4510.thebestphotogallery.MyAdapter;
import com4510.thebestphotogallery.R;
import com4510.thebestphotogallery.ServerComm;
import com4510.thebestphotogallery.ServerData;
import com4510.thebestphotogallery.Tasks.UpdateImageMetadataTask;

/**
 * Created by George on 02-Jan-18.
 */

public class EditDetailsActivity extends DetailsActivity implements ServerResponseListener {

    private ImageMetadata currentImageMetadata = null;
    private Integer imageIndex = null;

    public EditDetailsActivity() {
        super("Edit Details", R.id.editimagedetails_toolbar);
    }

    @Override
    public void onServerSuccess() {
        Toast.makeText(this, "Server upload successful", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onServerFailure() {
        Toast.makeText(this, "Server upload failed", Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setContentView(R.layout.activity_editimagedetails);
        super.onCreate(savedInstanceState);

        currentImageMetadata = (ImageMetadata) getIntent().getSerializableExtra("metadata");
        imageIndex = getIntent().getExtras().getInt("position");
        currentImageMetadata = ImageMetadataList.getInstance().get(imageIndex);
        setDetails();

    }

    private void setDetails() {
        TextView name = findViewById(R.id.btn_edit_name_text);
        TextView desc = findViewById(R.id.btn_edit_description_text);
        name.setText(currentImageMetadata.getTitle());
        desc.setText(currentImageMetadata.getDescription());
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
    protected void onResume() {
        super.onResume();
        setDetails();
    }

    public void btnSelected(View view) {
        Intent intent;
        switch (view.getId()) {
            case R.id.btn_name:
                intent = new Intent(this, EditNameActivity.class);
                launchActivity(intent);
                break;
            case R.id.btn_description:
                intent = new Intent(this, EditDescActivity.class);
                launchActivity(intent);
                break;
            case R.id.btn_backup:
                uploadToServer();
                break;
            default:
                intent = new Intent(this, EditNameActivity.class);
                launchActivity(intent);
        }
    }

    private void launchActivity(final Intent intent) {
        intent.putExtra("position", imageIndex);
        intent.putExtra("metadata", currentImageMetadata);
        startActivity(intent);
    }

    private void uploadToServer() {
        final ServerComm serverComm = new ServerComm(this, getCacheDir());

        Log.v(getClass().getName(), "upload to server option selected");
        Toast.makeText(this, "Uploading...", Toast.LENGTH_LONG).show();
        ImageMetadata imageMetadata = ImageMetadataList.getInstance().get(imageIndex);
        ServerData serverData = new ServerData();
        serverData.date = "01/01/1970";
        serverData.imageFilename = imageMetadata.getFilePath();
        serverData.description = imageMetadata.getDescription();
        serverData.title = imageMetadata.getTitle();
        serverData.latitude = String.valueOf(imageMetadata.getLatitude());
        serverData.imageData = BitmapFactory.decodeFile(imageMetadata.getFilePath());
        serverComm.sendData(serverData);
    }

}
