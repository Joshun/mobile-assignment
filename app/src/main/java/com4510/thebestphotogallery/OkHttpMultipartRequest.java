package com4510.thebestphotogallery;

import android.content.Context;
import android.util.Log;

import java.io.File;
import java.io.IOException;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;


/**
 * Created by joshua on 10/01/18.
 */

public class OkHttpMultipartRequest {
    public MultipartBody.Builder multipartBody;
    public OkHttpClient okHttpClient;

    public OkHttpMultipartRequest() {
        multipartBody = new MultipartBody.Builder();
        this.multipartBody.setType(MultipartBody.FORM);
        this.okHttpClient = new OkHttpClient();
    }

    public void addValue(String name, String value) {
        multipartBody.addFormDataPart(name, value);
    }

    public void addFile(String name, String mimetype, String fileName, String filePath) {
        System.out.println("uploading " + filePath + "...");
        multipartBody.addFormDataPart(name,
            fileName,
            RequestBody.create(MediaType.parse(mimetype), new File(filePath))
            );
    }

    public String execute(String url) throws IOException {
        RequestBody requestBody = null;
        Request request = null;
        Response response = null;
        String strResponse = null;

        requestBody = multipartBody.build();
        request = new Request.Builder().url(url).post(requestBody).build();
        Log.v("*REQUEST*", request.toString());
        response = okHttpClient.newCall(request).execute();
        Log.v("*RESPONSE*", request.toString());

        if (!response.isSuccessful()) {
            throw new IOException();
        }
        else {
            strResponse = response.body().string();
            // Check for 200 OK HTTP code
            int code = response.networkResponse().code();
            if (code != 200) {
                throw new IOException();
            }
        }

        return strResponse;

    }
}
