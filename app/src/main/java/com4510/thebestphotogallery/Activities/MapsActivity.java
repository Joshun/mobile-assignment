package com4510.thebestphotogallery.Activities;

/**
 * Created by FrancisALR on 31/12/2017.
 */
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.app.Activity;
import android.widget.TextView;
import android.widget.ImageView;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com4510.thebestphotogallery.Listeners.LoadMarkerOptsResponseListener;

import java.util.ArrayList;
import java.util.Set;
import java.util.List;
import java.util.Map;
import java.util.HashMap;
import java.util.Random;

import android.os.Handler;

import com4510.thebestphotogallery.Database.ImageMetadata;
import com4510.thebestphotogallery.ImageMetadataList;
import com4510.thebestphotogallery.R;
import com4510.thebestphotogallery.Tasks.MapLoadTask;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback, LoadMarkerOptsResponseListener {

    private GoogleMap mMap;
    private ImageMetadata im;
    protected ArrayList<ImageMetadata> imageList = new ArrayList<ImageMetadata>();
    private CameraUpdate cu;
    protected ImageMetadataList metadataList = ImageMetadataList.getInstance();
    Map<Marker, String> markersMap = new HashMap<Marker, String>();
    private Handler handler = new Handler();

    @Override
    public void markerOptsLoaded(List<MarkerOptions> markerOptionsList) {
        mMap.clear();
        System.out.println("in markerOptsLoaded");
//        List<Marker> markersList = new ArrayList<Marker>();
        for (MarkerOptions markerOpt : markerOptionsList) {
            Marker marker = mMap.addMarker(markerOpt);

        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

//        ImageMetadataList metadataList = (ImageMetadataList) getIntent().getSerializableExtra("FullList");

        setContentView(R.layout.activity_maps);
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);

        mapFragment.getMapAsync(this);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        new MapLoadTask().execute(googleMap);
//        runOnUiThread(new Runnable() {
//            @Override
//            public void run() {
//                mMap.clear();
//                LatLngBounds.Builder builder = new LatLngBounds.Builder();
//                List<Marker> markersList = new ArrayList<Marker>();
//
//                for (ImageMetadata metadata : metadataList.getList()) {
//                    System.out.println(metadataList.getList());
//                    if (metadata != null) {
//                        System.out.println(metadata.getFilePath());
//                        LatLng location = new LatLng(metadata.getLatitude(), metadata.getLongitude());
//                        Marker marker = mMap.addMarker(new MarkerOptions().position(location));
//                        if (metadata.getTitle() != null) {
//                            marker.setTitle(metadata.getTitle());
//                        } else {
//                            marker.setTitle("Marker at location");
//                        }
//                        if (metadata.getDescription() != null) {
//                            marker.setSnippet(metadata.getDescription());
//                        }
//                        markersList.add(marker);
//                        markersMap.put(marker, metadata.getFilePath());
//
//                    }
//                }
//                for (Marker m : markersList) {
//                    builder.include(m.getPosition());
//                }
                CustomInfoWindowAdapter adapter = new CustomInfoWindowAdapter(MapsActivity.this);
                mMap.setInfoWindowAdapter(adapter);

//                LatLngBounds bounds = builder.build();
//                int width = getResources().getDisplayMetrics().widthPixels;
//                int height = getResources().getDisplayMetrics().heightPixels;
//                int padding = (int) (width * 0.10); // offset from edges of the map 10% of screen
//
//                cu = CameraUpdateFactory.newLatLngBounds(bounds, width, height, padding);

                // When map is ready, camera zooms to fit all markers
//                mMap.setOnMapLoadedCallback(new GoogleMap.OnMapLoadedCallback() {
//                    @Override
//                    public void onMapLoaded() {
//                        /**set animated zoom camera into map*/
//                        mMap.animateCamera(cu);
//
//                    }
//                });
            }
//        });


//    }

    class CustomInfoWindowAdapter implements GoogleMap.InfoWindowAdapter {

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
            String filepath = markersMap.get(marker);

            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inPreferredConfig = Bitmap.Config.ARGB_8888;
//            Bitmap bitmap = BitmapFactory.decodeFile(filepath, options);

//            infoImage.setImageBitmap(bitmap);

            return view;
        }
    }


}
