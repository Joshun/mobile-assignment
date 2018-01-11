package com4510.thebestphotogallery.Activities;

/**
 * Created by FrancisALR on 31/12/2017.
 */
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.View;
import android.app.Activity;
import android.support.v7.widget.SearchView;
import android.widget.TextView;
import android.widget.ImageView;
import android.graphics.Bitmap;
import android.location.Address;
import android.location.Geocoder;


import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.maps.android.MarkerManager;
import com.google.maps.android.clustering.Cluster;
import com.google.maps.android.clustering.ClusterManager;

import com4510.thebestphotogallery.ClusterMarker;
import com4510.thebestphotogallery.Listeners.LoadMarkerResponseListener;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.HashMap;

import com4510.thebestphotogallery.Database.ImageMetadata;
import com4510.thebestphotogallery.ImageMetadataList;
import com4510.thebestphotogallery.R;
import com4510.thebestphotogallery.Tasks.MapLoadTask;
import com4510.thebestphotogallery.Util;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback, LoadMarkerResponseListener, ClusterManager.OnClusterItemClickListener<ClusterMarker> {

    private GoogleMap mMap;
    protected ArrayList<ImageMetadata> imageList = new ArrayList<ImageMetadata>();
    private CameraUpdate cu;
    protected ImageMetadataList metadataList;

    private LatLngBounds.Builder builder;
    private String currentFilePath = ""; //This is gross but I couldn't think how else to do it
    private int numMarkersLoaded = 0;
    private int numToLoad = 0;
    private ClusterManager<ClusterMarker> manager;

    @Override
    public void markerLoaded(ImageMetadata metadata, ClusterMarker marker) {
        manager.addItem(marker);
        builder.include(marker.getPosition());
        ++numMarkersLoaded;
        if (numMarkersLoaded == numToLoad / 4 || numMarkersLoaded == numToLoad) {
            markerBatchLoaded();
        }
    }

    public void markerBatchLoaded() {
        LatLngBounds bounds = builder.build();
        int width = getResources().getDisplayMetrics().widthPixels;
        int height = getResources().getDisplayMetrics().heightPixels;
        int padding = (int) (width * 0.15); // offset from edges of the map 10% of screen

        cu = CameraUpdateFactory.newLatLngBounds(bounds, width, height, padding);
        mMap.animateCamera(cu);
    }

    public void searchAddresses(String query) {
        Geocoder geocoder = new Geocoder(getBaseContext());

        try {
            // Getting 3 address to match input using geocoder
            List<Address> addressList = geocoder.getFromLocationName(query, 1);
            if (addressList != null && !addressList.equals(""))
                search(addressList);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);

        metadataList = (ImageMetadataList) getIntent().getSerializableExtra("FullList");
        final SearchView searchView = findViewById(R.id.search);
//        int searchId = getResources().getIdentifier("android:id/search_button", null, null);
//        ImageView searchButton = (ImageView) searchView.findViewById(searchId);
//        searchButton.setImageResource(R.drawable.ic_search);

        searchView.setOnSearchClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                searchAddresses(searchView.getQuery().toString());
                searchView.clearFocus();
            }
        });
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {
                searchAddresses(searchView.getQuery().toString());
                searchView.clearFocus();
                return false;
            }

            @Override
            public boolean onQueryTextChange(String s) {
                return false;
            }
        });

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);

        mapFragment.getMapAsync(this);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        //Instantiate the map
        mMap = googleMap;
        mMap.clear();

        //Create manager
        manager = new ClusterManager<ClusterMarker>(this, mMap);
        mMap.setOnCameraIdleListener(manager);
        mMap.setOnMarkerClickListener(manager);
        mMap.setInfoWindowAdapter(manager.getMarkerManager());
        manager.setOnClusterItemClickListener(this);

        CustomInfoWindowAdapter adapter = new CustomInfoWindowAdapter(this);
        manager.getMarkerCollection().setOnInfoWindowAdapter(adapter);

        // Create Bounds for camera movement
        builder = new LatLngBounds.Builder();
        List<Marker> markersList = new ArrayList<Marker>();

        for (ImageMetadata metadata : metadataList.getList()) {
            if (metadata != null && (metadata.getLatitude() != 0.0 || metadata.getLongitude() != 0.0)) {
                ++numToLoad;
                MapLoadTask task = new MapLoadTask(metadata, this);
                task.execute();
            }
        }

//        if (markersList.size() > 0) {
//            mMap.setOnMapLoadedCallback(new GoogleMap.OnMapLoadedCallback() {
//                @Override
//                public void onMapLoaded() {
//                LatLngBounds bounds = builder.build();
//                int width = getResources().getDisplayMetrics().widthPixels;
//                int height = getResources().getDisplayMetrics().heightPixels;
//                int padding = (int) (width * 0.15); // offset from edges of the map 10% of screen
//
//                cu = CameraUpdateFactory.newLatLngBounds(bounds, width, height, padding);
//                mMap.animateCamera(cu);
//                }
//            });
//        }
    }



    protected void search(List<Address> addressList) {

        Address address = (Address) addressList.get(0);

        LatLng latLng = new LatLng(address.getLatitude(), address.getLongitude());

        mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
        mMap.animateCamera(CameraUpdateFactory.zoomTo(15));

    }

    @Override
    public boolean onClusterItemClick(ClusterMarker marker) {
        currentFilePath = marker.getFilepath();
        return false;
    }

    private class CustomInfoWindowAdapter implements GoogleMap.InfoWindowAdapter {

        private Activity context;

        public CustomInfoWindowAdapter(Activity context) {
            this.context = context;
        }

        @Override
        public View getInfoWindow(Marker marker) {
            return null;
        }

        @Override
        public View getInfoContents(Marker marker) {
            View view = context.getLayoutInflater().inflate(R.layout.custominfowindow, null);

            ImageView infoImage = (ImageView) view.findViewById(R.id.info_image);
            TextView infoTitle = (TextView) view.findViewById(R.id.info_title);
            TextView infoDescription = (TextView) view.findViewById(R.id.info_description);

            infoTitle.setText(marker.getTitle());
            infoDescription.setText(marker.getSnippet());

            if (currentFilePath != "") {
                // Only run when marker is tapped on
                Bitmap bitmap = Util.loadBitmap(new File(currentFilePath), 512);
                infoImage.setImageBitmap(bitmap);
            }

            return view;
        }
    }


}
