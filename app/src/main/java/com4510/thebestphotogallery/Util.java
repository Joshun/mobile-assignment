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

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import pl.aprilapps.easyphotopicker.DefaultCallback;
import pl.aprilapps.easyphotopicker.EasyImage;

/**
 * Created by joshua on 22/11/17.
 */

public class Util {

    private static final int REQUEST_READ_EXTERNAL_STORAGE = 2987;
    private static final int REQUEST_WRITE_EXTERNAL_STORAGE = 7829;

    public static class BitmapList {

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

    public static Bitmap loadBitmap(File file, int dimension, boolean square) {
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

    public static Bitmap loadBitmap(File file, int dimension) {
        return loadBitmap(file, dimension, true);
    }

    public static Bitmap loadBitmap(File file) {
        return loadBitmap(file, 128);
    }

    /**
     * check permissions are necessary starting from Android 6
     * if you do not set the permissions, the activity will simply not work and you will be probably baffled for some hours
     * until you find a note on StackOverflow
     */
    public static void checkPermissions(final Context applicationContext, final Activity activity) {
        int currentAPIVersion = Build.VERSION.SDK_INT;
        if (currentAPIVersion >= android.os.Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(applicationContext, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                if (ActivityCompat.shouldShowRequestPermissionRationale(activity, Manifest.permission.READ_EXTERNAL_STORAGE)) {
                    android.support.v7.app.AlertDialog.Builder alertBuilder = new android.support.v7.app.AlertDialog.Builder(applicationContext);
                    alertBuilder.setCancelable(true);
                    alertBuilder.setTitle("Permission necessary");
                    alertBuilder.setMessage("External storage permission is necessary");
                    alertBuilder.setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                        @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
                        public void onClick(DialogInterface dialog, int which) {
                            ActivityCompat.requestPermissions((Activity) applicationContext, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, REQUEST_READ_EXTERNAL_STORAGE);
                        }
                    });
                    android.support.v7.app.AlertDialog alert = alertBuilder.create();
                    alert.show();

                } else {
                    ActivityCompat.requestPermissions(activity, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, REQUEST_READ_EXTERNAL_STORAGE);
                }

            }
            if (ContextCompat.checkSelfPermission(applicationContext, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                if (ActivityCompat.shouldShowRequestPermissionRationale(activity, Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                    android.support.v7.app.AlertDialog.Builder alertBuilder = new android.support.v7.app.AlertDialog.Builder(applicationContext);
                    alertBuilder.setCancelable(true);
                    alertBuilder.setTitle("Permission necessary");
                    alertBuilder.setMessage("Writing external storage permission is necessary");
                    alertBuilder.setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                        @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
                        public void onClick(DialogInterface dialog, int which) {
                            ActivityCompat.requestPermissions((Activity) applicationContext, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, REQUEST_WRITE_EXTERNAL_STORAGE);
                        }
                    });
                    android.support.v7.app.AlertDialog alert = alertBuilder.create();
                    alert.show();

                } else {
                    ActivityCompat.requestPermissions(activity, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, REQUEST_WRITE_EXTERNAL_STORAGE);
                }

            }


        }
    }

}
