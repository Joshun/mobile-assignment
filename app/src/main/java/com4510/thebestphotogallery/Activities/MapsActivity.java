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

import java.util.ArrayList;
import java.util.Set;
import java.util.List;
import java.util.Map;
import java.util.HashMap;

import com4510.thebestphotogallery.Database.ImageMetadata;
import com4510.thebestphotogallery.ImageMetadataList;
import com4510.thebestphotogallery.R;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private ImageMetadata im;
    protected ArrayList<ImageMetadata> imageList = new ArrayList<ImageMetadata>();
    private CameraUpdate cu;
    protected ImageMetadataList metadataList = ImageMetadataList.getInstance();
    Map<Marker, String> markersMap = new HashMap<Marker, String>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        ImageMetadataList metadataList = (ImageMetadataList) getIntent().getSerializableExtra("FullList");

//        Bundle b = getIntent().getExtras();
//
//        Set<String> keys = b.keySet();
//
//        for (String imkey : keys) {
//            im = (ImageMetadata) getIntent().getSerializableExtra(imkey);
//            imageList.add(im);
//        }



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
//        for (ImageMetadata metadata : metadataList.getList()) {
        for (ImageMetadata metadata : metadataList.getList()) {
            System.out.println(metadataList.getList());
            if (metadata != null) {
                System.out.println(metadata.getFilePath());
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
                markersMap.put(marker1, metadata.getFilePath());

            }
        }
        for (Marker m : markersList) {
            builder.include(m.getPosition());
        }
        CustomInfoWindowAdapter adapter = new CustomInfoWindowAdapter(MapsActivity.this);
        mMap.setInfoWindowAdapter(adapter);

        LatLngBounds bounds = builder.build();
        int width = getResources().getDisplayMetrics().widthPixels;
        int height = getResources().getDisplayMetrics().heightPixels;
        int padding = (int) (width * 0.10); // offset from edges of the map 10% of screen

        cu = CameraUpdateFactory.newLatLngBounds(bounds, width, height, padding);

        // When map is ready, camera zooms to fit all markers
        mMap.setOnMapLoadedCallback(new GoogleMap.OnMapLoadedCallback() {
            @Override
            public void onMapLoaded() {
                /**set animated zoom camera into map*/
                mMap.animateCamera(cu);

            }
        });

    }
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
            TextView tvTitle = (TextView) view.findViewById(R.id.info_title);
            TextView tvSubTitle = (TextView) view.findViewById(R.id.info_subtitle);

            tvTitle.setText(marker.getTitle());
            tvSubTitle.setText(marker.getSnippet());
            String filepath = markersMap.get(marker);

            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inPreferredConfig = Bitmap.Config.ARGB_8888;
            Bitmap bitmap = BitmapFactory.decodeFile(filepath, options);

            infoImage.setImageBitmap(bitmap);

            System.out.println(tvTitle.getText());


            return view;
        }
    }


}
//class CustomInfoWindowAdapter implements GoogleMap.InfoWindowAdapter {
//
//    private Activity context;
//
//    public CustomInfoWindowAdapter(Activity context) {
//        this.context = context;
//    }
//
//    @Override
//    public View getInfoWindow(Marker marker) {
//        return null;
//    }
//
//    @Override
//    public View getInfoContents(Marker marker) {
//        View view = context.getLayoutInflater().inflate(R.layout.custominfowindow, null);
//
//        ImageView infoImage = (ImageView) view.findViewById(R.id.info_image);
//        TextView tvTitle = (TextView) view.findViewById(R.id.info_title);
//        TextView tvSubTitle = (TextView) view.findViewById(R.id.info_subtitle);
//
//        tvTitle.setText(marker.getTitle());
//        tvSubTitle.setText(marker.getSnippet());
//        markersMap.get(marker);
//
//        System.out.println(tvTitle.getText());
//
//
//        return view;
//    }
//}
