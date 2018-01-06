/*
 * Copyright (c) 2017. This code has been developed by Fabio Ciravegna, The University of Sheffield. All rights reserved. No part of this code can be used without the explicit written permission by the author
 */

package com4510.thebestphotogallery.Activities;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import com4510.thebestphotogallery.Database.ImageMetadata;
import com4510.thebestphotogallery.Listeners.OnScrollChangedListener;
import com4510.thebestphotogallery.R;
import com4510.thebestphotogallery.ServerComm;
import com4510.thebestphotogallery.ServerData;
import com4510.thebestphotogallery.Images.ShowImageAsync;
import com4510.thebestphotogallery.ShowImageScrollView;

public class ShowImageActivity extends AppCompatActivity implements OnScrollChangedListener {

    private Bitmap currentImage = null;
    private String currentImageFile = "file.png";
    private List<Bitmap> bitmapMipMaps;

    private ShowImageScrollView detailsView;
    private View imageContainer;
    private ImageView imageView;
    private ServerComm serverComm;

    Integer imageIndex = null;
    private ImageMetadata element;

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.image_view, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Intent intent;
        switch (item.getItemId()) {
            case R.id.edit_image_details:
                Log.v(getClass().getName(), "edit detail option selected");
                intent = new Intent(this, EditDetailsActivity.class);
                intent.putExtra("metadata", element);
                startActivity(intent);
                return true;
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
        setContentView(R.layout.activity_showimage);

        final Toolbar toolbar = findViewById(R.id.show_toolbar);
        final View loadingView = findViewById(R.id.image_loading);
        detailsView = findViewById(R.id.details);
        imageContainer = findViewById(R.id.image_container);
        imageView = findViewById(R.id.image);
        bitmapMipMaps = new ArrayList<>();

        detailsView.setVisibility(View.GONE);
        element = (ImageMetadata) getIntent().getSerializableExtra("metadata");
        detailsView.setOnScrollChangedListener(this);
        setDetails(element);

        serverComm = new ServerComm(getCacheDir());

        if (savedInstanceState != null) {
            Log.v(getClass().getName(), "loaded instance state");
            imageIndex = savedInstanceState.getInt("position");

        }
        else {
            Bundle b = getIntent().getExtras();
            //            int position = -1;
            imageIndex = -1;
            if (b != null) {
                // this is the image position in the itemList
                imageIndex = b.getInt("position");
            }
        }

        currentImageFile = element.file.getAbsolutePath();
        ShowImageAsync imageAsync = new ShowImageAsync(imageView, loadingView, detailsView, bitmapMipMaps, element.file);
        imageAsync.execute();

        if (toolbar != null) {
            toolbar.setTitle("");
            setSupportActionBar(toolbar);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }

        imageView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                Log.v(getClass().getName(), "touch event");
                if (currentImage != null) {
                    Log.v(getClass().getName(), "attempting to send to server...");
                    ServerData serverData = new ServerData();
                    serverData.imageData = v.getDrawingCache();
                    serverData.title = "title";
                    serverData.longitude = "0.0";
                    serverData.latitude = "0.0";
                    serverData.description = "description";
                    serverData.imageFilename = currentImageFile;
                    serverData.date = "1/1/1";
                    serverComm.sendData(serverData);
                }
                return false;
            }
        });
    }

    @Override
    public void onScrollChanged(final int deltaX, final int deltaY) {
        if (detailsView != null && imageContainer != null && imageView != null) {
            final float PARALLAX_MULTIPLIER = 0.25f;
//            final int PARALLAX_MIP_BOUNDARY = imageView.getHeight() / bitmapMipMaps.size();
            final int scrollY = detailsView.getScrollY();
//            final int mipLevel = Math.min(bitmapMipMaps.size() - 1, scrollY / PARALLAX_MIP_BOUNDARY);

            imageContainer.setTranslationY(scrollY * PARALLAX_MULTIPLIER);
//            imageView.setImageBitmap(bitmapMipMaps.get(mipLevel));
        }
    }

    private void setDetails(final ImageMetadata data) {
        final TextView name = findViewById(R.id.view_name_text);
        final TextView description = findViewById(R.id.view_description_text);
        final TextView dimensions = findViewById(R.id.view_dimensions_text);
        final TextView geo = findViewById(R.id.view_geo_text);
        final TextView filesize = findViewById(R.id.view_filesize_text);

        if (name != null && data.getTitle() != null && !data.getTitle().equals("")) {
            name.setText(data.getTitle());
        }
        else if (name != null) {
            name.setText(data.file.getName());
        }
        if (description != null && data.getDescription() != null && !data.getDescription().equals("")) {
            description.setText(data.getDescription());
        }
        if (dimensions != null) {
            final int w = data.getWidth();
            final int h = data.getHeight();
            String s = w + "x" + h + "px";
            dimensions.setText(s);
        }
        if (geo != null) {
            String join = " " + getResources().getString(R.string.details_join) + " ";
            String s = data.getLatitude() + join + data.getLongitude();
            geo.setText(s);
        }
        if (filesize != null) {
            String s = data.getFileSize() + "MB";
            filesize.setText(s);
        }
    }

}
