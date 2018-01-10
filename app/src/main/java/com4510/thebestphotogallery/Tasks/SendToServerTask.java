package com4510.thebestphotogallery.Tasks;

import android.os.AsyncTask;
import android.util.Log;

import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import com4510.thebestphotogallery.Database.ImageMetadata;
import com4510.thebestphotogallery.Listeners.ServerResponseListener;
import com4510.thebestphotogallery.OkHttpMultipartRequest;
import okhttp3.OkHttpClient;

/**
 * Created by joshua on 10/01/18.
 */

public class SendToServerTask extends AsyncTask<ImageMetadata, Void, Void> {
    private ServerResponseListener serverResponseListener;
    private boolean uploadSucceeded = false;

    private enum SERVER_RESPONSE_TYPE { SUCCESS, FAILURE }

    public SendToServerTask(ServerResponseListener responseListener) {
        serverResponseListener = responseListener;
    }

    public SendToServerTask() {
        this(null);
    }

    public void handleCallback(SERVER_RESPONSE_TYPE responseType) {
        if (serverResponseListener == null) {
            return;
        }

        switch (responseType) {
            case SUCCESS:
                serverResponseListener.onServerSuccess();
                break;
            case FAILURE:
                serverResponseListener.onServerFailure();
                break;
            default:
                Log.e(getClass().getName(), "handleCallback: invalid responseType value " + responseType.toString());
        }

    }

    public String extractFilenameFromPath(String f) {
        return f.substring(f.lastIndexOf("/")+1);
    }

    public void sendServerData(ImageMetadata imageMetadata) {
        OkHttpMultipartRequest multipartRequest = new OkHttpMultipartRequest();

        String title = (imageMetadata.getTitle() != null) ? imageMetadata.getTitle() : "";
        String description = (imageMetadata.getDescription()) != null ? imageMetadata.getDescription() : "";
        String latitude = String.valueOf(imageMetadata.getLatitude());
        String longitude = String.valueOf(imageMetadata.getLongitude());

        multipartRequest.addValue("title", title);
        multipartRequest.addValue("description", description);
        multipartRequest.addValue("latitude", latitude);
        multipartRequest.addValue("longitude", longitude);

        Date date = new Date();
        DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
        multipartRequest.addValue("date", dateFormat.format(date));

        multipartRequest.addFile("image",
                "image/jpeg",
                extractFilenameFromPath(imageMetadata.getFilePath()),
                imageMetadata.getFilePath());


        try {
            multipartRequest.execute("http://jmoey.com:8091/uploadpicture");
            uploadSucceeded = true;
        }
        catch (IOException e) {
            e.printStackTrace(System.err);
        }

    }

    @Override
    protected void onPostExecute(Void aVoid) {
        super.onPostExecute(aVoid);
        if (serverResponseListener != null) {
            if (uploadSucceeded) {
                serverResponseListener.onServerSuccess();
            } else {
                serverResponseListener.onServerFailure();
            }
        }
    }

    @Override
    protected Void doInBackground(ImageMetadata... imageMetadata) {
        sendServerData(imageMetadata[0]);
        return null;
    }
}
