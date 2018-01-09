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

public class MapLoadTask extends AsyncTask<GoogleMap, Void, List<MarkerOptions>> {
    @Override
    protected List<MarkerOptions> doInBackground(GoogleMap... googleMaps) {
        GoogleMap mMap = googleMaps[0];
        ArrayList<MarkerOptions> markerOptsList = new ArrayList<>();

        ImageMetadataList imageMetadataList = ImageMetadataList.getInstance();

        for (ImageMetadata metadata: imageMetadataList.getList()) {
            System.out.println(imageMetadataList.getList());
            if (metadata != null) {
                System.out.println(metadata.getFilePath());
                LatLng location = new LatLng(metadata.getLatitude(), metadata.getLongitude());
                MarkerOptions marker = new MarkerOptions().position(location);

                markerOptsList.add(marker);
            }
        }

        // created markers list
        // now we need to actually put it on the map
        // which might need to be done on ui thread? not sure
        return markerOptsList;
    }
}
