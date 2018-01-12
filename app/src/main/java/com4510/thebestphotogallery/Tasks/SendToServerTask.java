package com4510.thebestphotogallery.Tasks;

import android.os.AsyncTask;
import android.util.Log;
import android.webkit.MimeTypeMap;

import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import com4510.thebestphotogallery.Database.ImageMetadata;
import com4510.thebestphotogallery.Listeners.ServerResponseListener;
import com4510.thebestphotogallery.OkHttpMultipartRequest;
import okhttp3.OkHttpClient;

/**
 * AsyncTask for uploading an image and some of its metadata to a server
 */

public class SendToServerTask extends AsyncTask<ImageMetadata, Void, Void> {
    private ServerResponseListener serverResponseListener;
    private boolean uploadSucceeded = false;

    private static final String SERVER_URI = "http://wesenseit-vm1.shef.ac.uk:8091/uploadImages";
//    private static final String SERVER_URI = "http://jmoey.com:8091/uploadpicture";

    public SendToServerTask(ServerResponseListener responseListener) {
        serverResponseListener = responseListener;
    }

    public SendToServerTask() {
        this(null);
    }


    public String extractFilenameFromPath(String f) {
        return f.substring(f.lastIndexOf("/")+1);
    }

    public String extractFileExtFromFilename(String f) {
        return f.substring(f.lastIndexOf(".")+1);
    }

    public String getMimetypeFromFileExt(String f) {
        return MimeTypeMap.getSingleton().getMimeTypeFromExtension(extractFileExtFromFilename(f));
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

        try {
            String imgFilename = extractFilenameFromPath(imageMetadata.getFilePath());
            String mimeType = getMimetypeFromFileExt(extractFileExtFromFilename(imgFilename));
            multipartRequest.addFile("image",
                    mimeType,
                    imgFilename,
                    imageMetadata.getFilePath());


            Log.v(getClass().getName(), "Attemtping to send " + imgFilename + " of type " + mimeType);

            multipartRequest.execute(SERVER_URI);
            uploadSucceeded = true;
        }
        catch (StringIndexOutOfBoundsException e) {
            e.printStackTrace();
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
