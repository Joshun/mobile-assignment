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
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MapStyleOptions;
import com.google.android.gms.maps.model.MarkerOptions;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import com4510.thebestphotogallery.Database.ImageMetadata;
import com4510.thebestphotogallery.Listeners.OnScrollChangedListener;
import com4510.thebestphotogallery.R;
import com4510.thebestphotogallery.Images.ShowImageAsync;
import com4510.thebestphotogallery.ShowImageScrollView;
import com4510.thebestphotogallery.Util;

public class ShowImageActivity extends AppCompatActivity implements OnScrollChangedListener, OnMapReadyCallback {
    private final int UPDATE_DATA = 1;

    private GoogleMap map;
    private List<Bitmap> bitmapMipMaps;

    private ShowImageScrollView detailsView;
    private View imageContainer;
    private ImageView imageView;

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
    public void onBackPressed() {
        Intent intent = new Intent();
        intent.putExtra("position", imageIndex);
        intent.putExtra("metadata", element);
        setResult(RESULT_OK, intent);
        finish();
        super.onBackPressed();
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
                startActivityForResult(intent, UPDATE_DATA);
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
        element = (ImageMetadata) getIntent().getSerializableExtra("metadata");
//        element = ImageMetadataList.getInstance().get(imageIndex);
        detailsView.setOnScrollChangedListener(this);
        setDetails(element);

        //Map
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.show_map);
        mapFragment.getMapAsync(this);

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

        ShowImageAsync imageAsync = new ShowImageAsync(imageView, loadingView, detailsView, bitmapMipMaps, element.file);
        imageAsync.execute();

        if (toolbar != null) {
            toolbar.setTitle("");
            setSupportActionBar(toolbar);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }
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
        updateMap();
    }

    private void updateMap() {

        if (map != null) {
            final float ZOOM = 16.0f;
            final LatLng POSITION = new LatLng(element.getLatitude(), element.getLongitude());
            final String TITLE = "Location";
            CameraUpdate camera = CameraUpdateFactory.newLatLngZoom(POSITION, ZOOM);
            map.addMarker(new MarkerOptions().position(POSITION).title(TITLE));
            map.moveCamera(camera);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == UPDATE_DATA) {
            element = (ImageMetadata) data.getSerializableExtra("metadata");
            setDetails(element);
            updateMap();
        }
    }

    private void setDetails(final ImageMetadata data) {
        final TextView name = findViewById(R.id.view_name_text);
        final TextView description = findViewById(R.id.view_description_text);
        final TextView dimensions = findViewById(R.id.view_dimensions_text);
        final TextView geo = findViewById(R.id.view_geo_text);
        final TextView filesize = findViewById(R.id.view_filesize_text);
        final TextView altitude = findViewById(R.id.view_altitude_text);
        final TextView date = findViewById(R.id.view_date_text);
        final View map = findViewById(R.id.show_map_container);

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
        if (geo != null && (data.getLatitude() != 0.0 || data.getLongitude() != 0.0)) {
            map.setVisibility(View.VISIBLE);
            String join = " " + getResources().getString(R.string.details_join) + " ";
            String s = Util.roundDP(data.getLatitude(), 6) + join + Util.roundDP(data.getLongitude(), 6);
            geo.setText(s);
        }
        else {
            map.setVisibility(View.GONE);
        }

        if (altitude != null) {
            String s = data.getAltitude() + "m";
            altitude.setText(s);
        }

        if (filesize != null) {
            int fSize = data.getFileSize();
            String s;
            if (fSize < 1000*1000f) {
                s = Util.round2DP(fSize / 1000f) + "KB";
            }
            else {
                s = Util.round2DP(fSize / (1000*1000f)) + "MB";
            }

            filesize.setText(s);
        }

        if (date != null) {
            DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm");
            String dateText = dateFormat.format(data.getDateAdded());
            date.setText(dateText);
        }
    }

}
