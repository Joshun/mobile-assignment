package com4510.thebestphotogallery.Activities;

import android.os.Bundle;
import android.widget.TextView;

import com4510.thebestphotogallery.Database.ImageMetadata;
import com4510.thebestphotogallery.MyAdapter;
import com4510.thebestphotogallery.R;

/**
 * Created by George on 02-Jan-18.
 */

public class ViewDetailsActivity extends DetailsActivity {
    private ImageMetadata currentImageMetadata = null;

    public ViewDetailsActivity() {
        super("View Details", R.id.viewimagedetails_toolbar);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setContentView(R.layout.activity_viewimagedetails);
        super.onCreate(savedInstanceState);

        currentImageMetadata = (ImageMetadata) getIntent().getSerializableExtra("metadata");

        TextView nameView = findViewById(R.id.view_name);
        TextView descView = findViewById(R.id.view_description);
        nameView.setText(currentImageMetadata.getTitle());
        descView.setText(currentImageMetadata.getDescription());
    }

}
