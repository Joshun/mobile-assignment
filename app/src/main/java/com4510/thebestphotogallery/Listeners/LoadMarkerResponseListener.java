package com4510.thebestphotogallery.Listeners;

import com.google.android.gms.maps.model.Marker;

import com4510.thebestphotogallery.ClusterMarker;

/**
 * Created by George on 04-Jan-18.
 */

public interface LoadMarkerResponseListener {

    void markerLoaded(ClusterMarker marker);

}
