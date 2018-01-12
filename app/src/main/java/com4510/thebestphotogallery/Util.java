package com4510.thebestphotogallery;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.os.Parcelable;
import android.os.Parcel;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Serializable;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.NoRouteToHostException;
import java.net.SocketTimeoutException;
import java.net.URL;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

import javax.net.ssl.SSLException;

import com4510.thebestphotogallery.Database.ImageMetadata;
import pl.aprilapps.easyphotopicker.DefaultCallback;
import pl.aprilapps.easyphotopicker.EasyImage;

/**
 * Utility class for useful static methods to be used across the project
 * Created by joshua on 22/11/17.
 */

public class Util{

    //ids for requests
    private static final int REQUEST_READ_EXTERNAL_STORAGE = 2987;
    private static final int REQUEST_WRITE_EXTERNAL_STORAGE = 7829;

    /**
     * Helper class for reserving space for bitmaps and the number of bitmaps
     * actually loaded
     */
    public static class BitmapList  {

        private List<Bitmap> bitmaps;
        private int loadedSize;

        public BitmapList() {
            this.bitmaps = new ArrayList<>();
            this.loadedSize = 0;
        }

        public final List<Bitmap> getList() {
            return bitmaps;
        }

        public final int getLoadedSize() {
            return loadedSize;
        }

        public final boolean loaded() { return bitmaps.size() == loadedSize; }

        public void incLoadedSize(final int x) {
            loadedSize += x;
        }

        public void clear() {
            bitmaps.clear();
            loadedSize = 0;
        }
    }

    /**
     * it initialises EasyImage
     */
    public static void initEasyImage(Context c) {
        EasyImage.configuration(c)
                .setImagesFolderName("EasyImage sample")
                .setCopyTakenPhotosToPublicGalleryAppFolder(true)
                .setCopyPickedImagesToPublicGalleryAppFolder(false)
                .setAllowMultiplePickInGallery(true);
    }

    /**
     * Helper method that efficiently loads bitmaps based on various parameters
     * @param file File for image to be loaded
     * @param dimension Size in pixels to resize to. Resulting bitmap's shortest side will be this size
     * @param square Whether the bitmap should be trimmed down to a square
     * @return The final bitmap image
     */
    public static Bitmap loadBitmap(File file, int dimension, boolean square) {
        // don't try to load nonexistent files
        if (!file.exists()) {
            return null;
        }

        if (dimension > 4096) {
            Log.w("Dimension warning", "dimension set too large. Reducing to 4096");
            dimension = 4096;
        }
        float fdimension = (float)dimension;

        //Setting options and deriving outWidth and outHeight
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        options.inPreferredConfig = Bitmap.Config.RGB_565;
        options.inDither = true;
        Bitmap bitmap = BitmapFactory.decodeFile(file.getAbsolutePath(), options);

        //Using outWidth and outHeight to calculate a suitable sampleSize
        if (dimension != -1) {
            float resize = 1;

            //If the image isn't going to be a square, then prioritise reducing the maximum dimension
            //If the image IS going to be a square (and therefore will be cropped), reduce based on smallest dimension
            if (!square && (options.outHeight > dimension || options.outWidth > dimension)) {
                resize = options.outWidth > options.outHeight ? (float)options.outWidth / fdimension : (float)options.outHeight / fdimension;
            }
            else if (square && (options.outHeight > dimension && options.outWidth > dimension)) {
                resize = options.outWidth < options.outHeight ? (float)options.outWidth / fdimension : (float)options.outHeight / fdimension;
            }
            options.inJustDecodeBounds = false;
            options.inSampleSize = (int)resize;
            bitmap = BitmapFactory.decodeFile(file.getAbsolutePath(), options);

            int w = options.outWidth;
            int h = options.outHeight;

            //Resize image if width or height is less than dimension
            if (bitmap != null && (options.outWidth < dimension || options.outHeight < dimension)) {
                float r = options.outWidth < options.outHeight ? fdimension / (float)w : fdimension / (float)h;
                w = (int)Math.ceil(r * w);
                h = (int)Math.ceil(r * h);
                bitmap = Bitmap.createScaledBitmap(bitmap, w, h, true);
            }

            //Cropping the image to a square
            if (square && bitmap != null) {
                int d = dimension;
                int x = Math.max(0, (w - d) / 2);
                int y = Math.max(0, (h - d) / 2);
//                Log.v("ImageData", "" + x + ", " + y + ", " + d + ", " + w + ", " + h + ", " + options.outWidth + ", " + options.outHeight);
                return Bitmap.createBitmap(bitmap, x, y, d, d);
            }
        }

        return bitmap;
    }

    /**
     * Helper method that efficiently loads bitmaps based on various parameters
     * @param file File for image to be loaded
     * @param dimension Size in pixels to resize to. Resulting bitmap's shortest side will be this size
     * @return The final bitmap image
     */
    public static Bitmap loadBitmap(File file, int dimension) {
        return loadBitmap(file, dimension, true);
    }

    /**
     * Helper method that efficiently loads bitmaps based on various parameters
     * @param file File for image to be loaded
     * @return The final bitmap image
     */
    public static Bitmap loadBitmap(File file) {
        return loadBitmap(file, 128);
    }

    /**
     * check permissions are necessary starting from Android 6
     * if you do not set the permissions, the activity will simply not work and you will be probably baffled for some hours
     * until you find a note on StackOverflow
     */
    public static void requestPermissionsIfNecessary(final Activity activity) {
        int currentAPIVersion = Build.VERSION.SDK_INT;
        if (currentAPIVersion >= android.os.Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(activity, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions((Activity) activity, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, REQUEST_WRITE_EXTERNAL_STORAGE);
            }
        }
    }

    /**
     * Helper method that rounds a double to a specified number of decimal places
     * @param x The double to be rounded
     * @param dp The number of decimal places to round to
     * @return The rounded double
     */
    public static double roundDP(double x, int dp) {
        StringBuilder sb = new StringBuilder();
        sb.append("#.");
        for (int i=0; i<dp; i++) {
            sb.append("#");
        }
        DecimalFormat df = new DecimalFormat(sb.toString());
        return Double.valueOf(df.format(x));
    }

    /**
     * Helper method that rounds a double to two decimal places
     * @param x The double to be rounded
     * @return The rounded double
     */
    public static double round2DP(double x) {
        return roundDP(x, 2);
    }

    /**
     * Helper method that takes a url to a server and produces a response
     * @param urlLink Url of server to query
     * @param response Out parameter of the server response
     * @return Response code
     */
    public static int getHttpToServer(String urlLink, StringBuffer response) {
        try {
            URL obj = new URL(urlLink);
            HttpURLConnection conn = (HttpURLConnection) obj.openConnection();
            conn.setRequestMethod("GET");

            BufferedReader in = new BufferedReader(
                    new InputStreamReader(conn.getInputStream()));
            String inputLine;

            while ((inputLine = in.readLine()) != null) {
                response.append(inputLine);
            }
            in.close();
        } catch (MalformedURLException ex) {
            Log.e("GetHttp", Log.getStackTraceString(ex));
            return 2;
        } catch (NoRouteToHostException ex) {
            Log.e("GetHttp", Log.getStackTraceString(ex));
            return 3;
        } catch (SocketTimeoutException ex){
            Log.e("GetHttp", Log.getStackTraceString(ex));
            return 4;
        } catch (SSLException ex){
            Log.e("GetHttp", Log.getStackTraceString(ex));
            return 5;
        } catch (IOException ex) {
            Log.e("GetHttp", Log.getStackTraceString(ex));
            return 6;
        } catch (Exception e){
            Log.e("GetHttp", Log.getStackTraceString(e));
            return 7;
        }
        return 0;
    }

}
