package com4510.thebestphotogallery;

import android.os.AsyncTask;
import android.util.Log;

import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by joshua on 19/12/17.
 */

public class SendToServerTask extends AsyncTask<ServerData, Void, Void> {
    @Override
    protected Void doInBackground(ServerData... serverData) {
        HttpURLConnection urlConnection = null;
        try {
            Log.v("SendToServerTask", "connecting to server...");
            URL url = new URL("http://jmoey.com:8091/uploadpicture");
            urlConnection = (HttpURLConnection) url.openConnection();
            Log.v("SendToServerTask", "connected.");

            Log.v("SendToServerTask", "sending...");
            urlConnection.setRequestMethod("POST");
            urlConnection.setDoOutput(true);
            urlConnection.setChunkedStreamingMode(0);
            OutputStream outputPost = new BufferedOutputStream(urlConnection.getOutputStream());
            writeStream(null, outputPost);
            Log.v("SendToServerTask", "sent.");

        }
        catch (java.net.MalformedURLException e) {
            Log.e("SendToServerTask", "sending to server failed (malformed URL)...");
            Log.e("SendToServerTask", e.getMessage());
        }
        catch (java.io.IOException e) {
            Log.e("SendToServerTask", "sending to server failed (IO error)...");
            Log.e("SendToServerTask", e.getMessage());
        }
        finally {
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
        }

        return null;
    }

    private void writeStream(ServerData serverData, OutputStream out) throws IOException {
        String output = "test";
        out.write(output.getBytes());
        out.flush();
    }
}
