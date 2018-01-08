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
import com4510.thebestphotogallery.ImageMetadataList;
import com4510.thebestphotogallery.MyAdapter;
import com4510.thebestphotogallery.R;
import com4510.thebestphotogallery.Tasks.UpdateImageMetadataTask;

/**
 * Created by George on 02-Jan-18.
 */

public class EditDetailsActivity extends DetailsActivity {

    private ImageMetadata currentImageMetadata = null;
    private Integer imageIndex = null;

    public EditDetailsActivity() {
        super("Edit Details", R.id.editimagedetails_toolbar);
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
                intent.putExtra("position", imageIndex);
                break;
            case R.id.btn_description:
                intent = new Intent(this, EditDescActivity.class);
                intent.putExtra("position", imageIndex);
                break;
            default:
                intent = new Intent(this, EditNameActivity.class);
                intent.putExtra("position", imageIndex);
                Log.v("Geo", "Geo button selected");
        }
        intent.putExtra("metadata", currentImageMetadata);
        startActivity(intent);
    }

}
