package com4510.thebestphotogallery.Activities;

/**
 * Created by FrancisALR on 31/12/2017.
 */
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import android.graphics.Bitmap;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Set;
import com4510.thebestphotogallery.Database.ImageMetadata;
import com4510.thebestphotogallery.Util;

import com4510.thebestphotogallery.R;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private ImageMetadata im;
    protected ArrayList<ImageMetadata> imageList = new ArrayList<ImageMetadata>();


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

        // Add a marker in Sydney, Australia, and move the camera.
        LatLng sydney = new LatLng(-34, 151);
        mMap.addMarker(new MarkerOptions().position(sydney).title("Marker in Sydney"));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));

        for (ImageMetadata metadata : imageList) {
            LatLng location = new LatLng(metadata.getLatitude(), metadata.getLongitude() );
            mMap.addMarker(new MarkerOptions().position(location).title("Marker at location"));
        }
    }

}
