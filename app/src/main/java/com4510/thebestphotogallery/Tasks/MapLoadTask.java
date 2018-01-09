package com4510.thebestphotogallery.Tasks;

import android.os.AsyncTask;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com4510.thebestphotogallery.Database.ImageMetadata;
import com4510.thebestphotogallery.ImageMetadataList;

/**
 * Created by joshua on 09/01/18.
 */

public class MapLoadTask extends AsyncTask<GoogleMap, Void, List<Marker>> {
    @Override
    protected List<Marker> doInBackground(GoogleMap... googleMaps) {
        GoogleMap mMap = googleMaps[0];
        ArrayList<Marker> markersList = new ArrayList<>();
        Map<Marker, String> markersMap = new HashMap<Marker, String>();

        mMap.clear();

        ImageMetadataList imageMetadataList = ImageMetadataList.getInstance();
        for (ImageMetadata metadata: imageMetadataList.getList()) {
            System.out.println(imageMetadataList.getList());
            if (metadata != null) {
                System.out.println(metadata.getFilePath());
                LatLng location = new LatLng(metadata.getLatitude(), metadata.getLongitude());
                Marker marker = mMap.addMarker(new MarkerOptions().position(location));
                if (metadata.getTitle() != null) {
                    marker.setTitle(metadata.getTitle());
                } else {
                    marker.setTitle("Marker at location");
                }
                if (metadata.getDescription() != null) {
                    marker.setSnippet(metadata.getDescription());
                }
                markersList.add(marker);
                markersMap.put(marker, metadata.getFilePath());
            }
        }

        // created markers list
        // now we need to actually put it on the map
        // which might need to be done on ui thread? not sure
        return markersList;
    }
}
