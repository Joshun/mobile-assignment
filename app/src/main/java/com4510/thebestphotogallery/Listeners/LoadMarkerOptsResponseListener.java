package com4510.thebestphotogallery.Listeners;

import com.google.android.gms.maps.model.MarkerOptions;
import java.util.List;

/**
 * Created by George on 04-Jan-18.
 */

public interface LoadMarkerOptsResponseListener {

    void markerOptsLoaded(List<MarkerOptions> markerOptionsList);

}
