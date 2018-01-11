package com4510.thebestphotogallery.Tasks;

import android.app.Activity;
import android.os.AsyncTask;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com4510.thebestphotogallery.ClusterMarker;
import com4510.thebestphotogallery.Database.ImageMetadata;
import com4510.thebestphotogallery.Listeners.ElevationResponseListener;
import com4510.thebestphotogallery.Listeners.LoadMarkerResponseListener;
import com4510.thebestphotogallery.R;
import com4510.thebestphotogallery.Util;

/**
 * Created by George on 11-Jan-18.
 */

public class ElevationTask extends AsyncTask<Void, Void, String> {

    private final double latitude;
    private final double longitude;
    private final String APIKey;
    private final ElevationResponseListener listener;

    public ElevationTask(final double latitude, final double longitude, final String APIKey, ElevationResponseListener listener) {
        this.latitude = latitude;
        this.longitude = longitude;
        this.APIKey = APIKey;
        this.listener = listener;
    }

    @Override
    protected void onPostExecute(String elevation) {
        super.onPostExecute(elevation);
        if (listener != null) {
            listener.elevationResponse(Double.parseDouble(elevation));
        }
    }

    @Override
    protected String doInBackground(Void... data) {
        String elevation = "0.0";
        StringBuffer jsonData = new StringBuffer();
        String url = "https://maps.googleapis.com/maps/api/elevation/json?locations=" + latitude + "," + longitude + "&key=" + APIKey;
        int responseCode = Util.getHttpToServer(url, jsonData);
        if (responseCode == 0) {
            try {
                JSONObject json = new JSONObject(jsonData.toString());
                JSONArray result = json.getJSONArray("results");
                JSONObject current = result.getJSONObject(0);
                elevation = current.getString("elevation");
            }
            catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return elevation;
    }

}
