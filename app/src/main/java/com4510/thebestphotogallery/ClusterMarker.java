package com4510.thebestphotogallery;

import com.google.android.gms.maps.model.LatLng;
import com.google.maps.android.clustering.ClusterItem;

/**
 * Specialised marker used for the Google maps ClusterManager
 * Created by George on 11-Jan-18.
 */

public class ClusterMarker implements ClusterItem {

    private final String filepath;
    private final LatLng position;
    private final String title;
    private final String snippet;

    public ClusterMarker(final String filepath, final double lat, final double lng, final String title, final String snippet) {
        this.filepath = filepath;
        this.position = new LatLng(lat, lng);
        this.title = title;
        this.snippet = snippet;
    }

    public ClusterMarker(final String filepath, final double lat, final double lng) {
        this(filepath, lat, lng, "", "");
    }

    @Override
    public final LatLng getPosition() {
        return position;
    }

    @Override
    public final String getTitle() {
        return title;
    }

    @Override
    public final String getSnippet() {
        return snippet;
    }

    public final String getFilepath() {
        return filepath;
    }

}
