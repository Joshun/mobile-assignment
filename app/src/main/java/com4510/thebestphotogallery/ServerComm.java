package com4510.thebestphotogallery;

import android.util.Log;

import com.android.volley.AuthFailureError;
import com.android.volley.Cache;
import com.android.volley.Network;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.BasicNetwork;
import com.android.volley.toolbox.DiskBasedCache;
import com.android.volley.toolbox.HurlStack;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;

import org.json.JSONObject;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by joshua on 21/12/17.
 */

public class ServerComm {
    private Cache cache;
    private Network network;
    private RequestQueue mRequestQueue;
    private static final String URL = "http://jmoey.com:8091/uploadpicture";

    public ServerComm(File cacheDir) {
        cache = new DiskBasedCache(cacheDir, 1024*1024);
        network = new BasicNetwork(new HurlStack());
        mRequestQueue = new RequestQueue(cache, network);
        mRequestQueue.start();
    }

    public void sendData(final ServerData serverData) {
        HashMap<String, String> serverDataHashMap = new HashMap<>();
//        serverDataHashMap.put("title", data.title);
//        serverDataHashMap.put("description", data.description);
//        serverDataHashMap.put("longitude", data.longitude);
//        serverDataHashMap.put("latitude", data.latitude);

        VolleyMultipartRequest multipartRequest = new VolleyMultipartRequest(
            Request.Method.POST, URL,
            new Response.Listener<NetworkResponse>() {
                @Override
                public void onResponse(NetworkResponse response) {

                }
        },
            new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {

                }
            }
        ) {

            @Override
            protected Map<String, DataPart> getByteData() throws AuthFailureError {
                Map<String, DataPart> params = new HashMap<>();
//                params.put("image", new DataPart("file.jpg", AppHelper.getFileDataFromDrawable()
                return super.getByteData();
            }

            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("title", serverData.title);
                params.put("description", serverData.description);
                params.put("longitude", serverData.longitude);
                params.put("latitude", serverData.latitude);
                return params;
            }
        };

//        JSONObject jsonObject = new JSONObject(serverDataHashMap);
//
//        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, URL, jsonObject, new Response.Listener<JSONObject>() {
//            @Override
//            public void onResponse(JSONObject response) {
//                Log.v(getClass().getName(), "data sent to server successfully.");
//            }
//        }, new Response.ErrorListener() {
//            @Override
//            public void onErrorResponse(VolleyError error) {
////                Log.e(getClass().getName(), error.getMessage());
//                Log.e(getClass().getName(), "failed to send to server.");
//            }
//        });
//
//        mRequestQueue.add(jsonObjectRequest);

//        StringRequest stringRequest = new StringRequest(
//                Request.Method.POST,
//                url,
//                new Response.Listener<String>() {
//                    @Override
//                    public void onResponse(String response) {
//
//                    }
//                },
//                new Response.ErrorListener() {
//                    @Override
//                    public void onErrorResponse(VolleyError error) {
//
//                    }
//                });
    }
}
