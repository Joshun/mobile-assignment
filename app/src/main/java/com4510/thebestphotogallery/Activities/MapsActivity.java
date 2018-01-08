package com4510.thebestphotogallery.Activities;

/**
 * Created by FrancisALR on 31/12/2017.
 */
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.graphics.Bitmap;
import android.app.AlertDialog;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;


import com.google.android.gms.maps.GoogleMap.InfoWindowAdapter;


import android.view.View;
import android.widget.TextView;


import java.util.ArrayList;
import java.util.Set;
import java.util.List;

import com4510.thebestphotogallery.Database.ImageMetadata;
import com4510.thebestphotogallery.Util;

import com4510.thebestphotogallery.R;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private ImageMetadata im;
    protected ArrayList<ImageMetadata> imageList = new ArrayList<ImageMetadata>();
    private CameraUpdate cu;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Bundle b = getIntent().getExtras();

        Set<String> keys = b.keySet();

        for (String imkey : keys) {
            im = (ImageMetadata) getIntent().getSerializableExtra(imkey);
            imageList.add(im);
        }



        setContentView(R.layout.activity_maps);
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        LatLngBounds.Builder builder = new LatLngBounds.Builder();
        List<Marker> markersList = new ArrayList<Marker>();

        mMap.clear();

        int i = 0;
        for (ImageMetadata metadata : imageList) {
            i += 5;
            LatLng location = new LatLng(metadata.getLatitude() + i, metadata.getLongitude() + i);
            Marker marker1 = mMap.addMarker(new MarkerOptions().position(location));
            if (metadata.getTitle() != null) {
                marker1.setTitle(metadata.getTitle());
            } else {
                marker1.setTitle("Marker at location");
            }
            if (metadata.getDescription() != null) {
                marker1.setSnippet(metadata.getDescription());
            }
            markersList.add(marker1);
        }
        for (Marker m : markersList) {
            builder.include(m.getPosition());
        }
        LatLngBounds bounds = builder.build();
        int width = getResources().getDisplayMetrics().widthPixels;
        int height = getResources().getDisplayMetrics().heightPixels;
        int padding = (int) (width * 0.10); // offset from edges of the map 10% of screen

        cu = CameraUpdateFactory.newLatLngBounds(bounds, width, height, padding);

        mMap.setOnMapLoadedCallback(new GoogleMap.OnMapLoadedCallback() {
            @Override
            public void onMapLoaded() {
                /**set animated zoom camera into map*/
                mMap.animateCamera(cu);

            }
        });


    }


}
