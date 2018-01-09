/*
 * Copyright (c) 2017. This code has been developed by Fabio Ciravegna, The University of Sheffield. All rights reserved. No part of this code can be used without the explicit written permission by the author
 */

package com4510.thebestphotogallery.Activities;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
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
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MapStyleOptions;
import com.google.android.gms.maps.model.MarkerOptions;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

import com4510.thebestphotogallery.Database.ImageMetadata;
import com4510.thebestphotogallery.ImageMetadataList;
import com4510.thebestphotogallery.Listeners.OnScrollChangedListener;
import com4510.thebestphotogallery.Listeners.ServerResponseListener;
import com4510.thebestphotogallery.R;
import com4510.thebestphotogallery.ServerComm;
import com4510.thebestphotogallery.ServerData;
import com4510.thebestphotogallery.Images.ShowImageAsync;
import com4510.thebestphotogallery.ShowImageScrollView;
import com4510.thebestphotogallery.VolleyMultipartRequest;

public class ShowImageActivity extends AppCompatActivity implements OnScrollChangedListener, OnMapReadyCallback, ServerResponseListener {

    private GoogleMap map;
    private Bitmap currentImage = null;
    private String currentImageFile = "file.png";
    private List<Bitmap> bitmapMipMaps;

    private ShowImageScrollView detailsView;
    private View imageContainer;
    private ImageView imageView;
    private ServerComm serverComm;

    Integer imageIndex = null;
    private ImageMetadata element;

    private static final String SERVER_URI = "http://jmoey.com:8091";

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.image_view, menu);
        return true;
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
    public boolean onOptionsItemSelected(MenuItem item) {
        Intent intent;
        switch (item.getItemId()) {
            case R.id.edit_image_details:
                Log.v(getClass().getName(), "edit detail option selected");
                intent = new Intent(this, EditDetailsActivity.class);
                intent.putExtra("metadata", element);
                intent.putExtra("position", imageIndex);
                startActivity(intent);
                return true;
            case R.id.upload_image:
                uploadToServer();
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
        imageIndex  = getIntent().getExtras().getInt("position");
//        element = (ImageMetadata) getIntent().getSerializableExtra("metadata");
        element = ImageMetadataList.getInstance().get(imageIndex);
        detailsView.setOnScrollChangedListener(this);
        setDetails(element);

        //Map
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.show_map);
        mapFragment.getMapAsync(this);

        serverComm = new ServerComm(this, getCacheDir());

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

    private void uploadToServer() {
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

    @Override
    public void onMapReady(GoogleMap googleMap) {
        map = googleMap;
        map.setMapStyle(MapStyleOptions.loadRawResourceStyle(this, R.raw.silver_map));
        map.getUiSettings().setAllGesturesEnabled(false);

        final float ZOOM = 16.0f;
        final LatLng POSITION = new LatLng(element.getLatitude(), element.getLongitude());
        final String TITLE = "Location";
        CameraUpdate camera = CameraUpdateFactory.newLatLngZoom(POSITION, ZOOM);
        map.addMarker(new MarkerOptions().position(POSITION).title(TITLE));
        map.moveCamera(camera);
    }

    @Override
    protected void onResume() {
        super.onResume();
        ImageMetadata im = ImageMetadataList.getInstance().get(imageIndex);
        setDetails(im);
    }

    private void setDetails(final ImageMetadata data) {
        final TextView name = findViewById(R.id.view_name_text);
        final TextView description = findViewById(R.id.view_description_text);
        final TextView dimensions = findViewById(R.id.view_dimensions_text);
        final TextView geo = findViewById(R.id.view_geo_text);
        final TextView filesize = findViewById(R.id.view_filesize_text);
        final TextView altitude = findViewById(R.id.view_altitude_text);

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
            String s = roundDP(data.getLatitude(), 6) + join + roundDP(data.getLongitude(), 6);
            geo.setText(s);
        }

        if (altitude != null) {
            String s = data.getAltitude() + "m";
            altitude.setText(s);
        }

        if (filesize != null) {
            int fSize = data.getFileSize();
            String s;
            if (fSize < 1000*1000f) {
                s = round2DP(fSize / 1000f) + "KB";
            }
            else {
                s = round2DP(fSize / (1000*1000f)) + "MB";
            }

            filesize.setText(s);
        }
    }

    private static double roundDP(double x, int dp) {
        StringBuilder sb = new StringBuilder();
        sb.append("#.");
        for (int i=0; i<dp; i++) {
            sb.append("#");
        }
        DecimalFormat df = new DecimalFormat(sb.toString());
        return Double.valueOf(df.format(x));
    }


    private static double round2DP(float x) {
        return roundDP(x, 2);
    }

}
