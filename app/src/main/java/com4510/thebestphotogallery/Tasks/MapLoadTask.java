package com4510.thebestphotogallery.Tasks;

import android.os.AsyncTask;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.maps.android.MarkerManager;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

import com4510.thebestphotogallery.ClusterMarker;
import com4510.thebestphotogallery.Database.ImageMetadata;
import com4510.thebestphotogallery.ImageMetadataList;
import com4510.thebestphotogallery.Listeners.LoadMarkerResponseListener;
/**
 * Created by joshua on 09/01/18.
 */

public class MapLoadTask extends AsyncTask<Void, Void, ClusterMarker> {

    private LoadMarkerResponseListener loadMarkerResponseListenerListener;
    private ImageMetadata metadata;

    public MapLoadTask(final ImageMetadata data, LoadMarkerResponseListener l) {
        this.loadMarkerResponseListenerListener = l;
        this.metadata = data;
    }

    public MapLoadTask(final ImageMetadata data) {
        this(data, null);
    }

    @Override
    protected void onPostExecute(ClusterMarker marker) {
        super.onPostExecute(marker);
        if (loadMarkerResponseListenerListener != null) {
            loadMarkerResponseListenerListener.markerLoaded(metadata, marker);
        }
    }

    @Override
    protected ClusterMarker doInBackground(Void... data) {
        final String title = metadata.getTitle() != null ? metadata.getTitle() : metadata.file.getName();
        final String desc = metadata.getDescription();
        final ClusterMarker marker = new ClusterMarker(metadata.getFilePath(), metadata.getLatitude(), metadata.getLongitude(), title, desc);
        return marker;
    }
}
