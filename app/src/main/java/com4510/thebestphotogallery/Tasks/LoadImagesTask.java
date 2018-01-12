package com4510.thebestphotogallery.Tasks;

import android.app.Activity;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.provider.MediaStore;
import android.support.media.ExifInterface;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import com4510.thebestphotogallery.Database.AppDatabase;
import com4510.thebestphotogallery.Database.ImageMetadata;
import com4510.thebestphotogallery.Images.LoadImagesResponseListener;

/**
 * AsyncTask for querying Android MediaStore to obtain image paths and extracting metadata
 */

public class LoadImagesTask extends AsyncTask<LoadImagesTask.LoadImagesTaskParam, Void, List<ImageMetadata>> {
    public static class LoadImagesTaskParam {
        public Activity activity;
        public Calendar filterStartDate = null;
        public Calendar filterEndDate = null;
    }

    private LoadImagesResponseListener loadImagesResponseListenerListener;

    public LoadImagesTask() {
        loadImagesResponseListenerListener = null;
    }

    public LoadImagesTask(LoadImagesResponseListener l) {
        loadImagesResponseListenerListener = l;
    }

    @Override
    protected void onPostExecute(List<ImageMetadata> imageMetadata) {
        super.onPostExecute(imageMetadata);
        if (loadImagesResponseListenerListener != null) {
            loadImagesResponseListenerListener.imagesLoaded(imageMetadata);
        }
    }

    @Override
    protected List<ImageMetadata> doInBackground(LoadImagesTaskParam... loadImagesTaskParams) {
        Activity activity = loadImagesTaskParams[0].activity;
        Calendar filterStartDate = loadImagesTaskParams[0].filterStartDate;
        Calendar filterEndDate = loadImagesTaskParams[0].filterEndDate;
        List<String> imagePaths = getGalleryImages(activity);
        List<ImageMetadata> imageMetadataList = new ArrayList<>();

        for (String p: imagePaths) {
            ImageMetadata imageMetadata = new ImageMetadata();
            loadMetadata(imageMetadata, p);
            imageMetadata.setFilePath(p);
            imageMetadataList.add(imageMetadata);
        }

        // insert all discovered images into database
        // existing ones will be ignored (see onConflict = OnConflictStrategy.IGNORE in ImageMetadata.java)
        AppDatabase.getInstance(activity).imageMetadataDao().insertAll((imageMetadataList.toArray(new ImageMetadata[imageMetadataList.size()])));

        List<ImageMetadata> allImageMetadata;

        // if filters are applied, retrieve only the images between the two dates
        if (filterStartDate != null && filterEndDate != null) {
            allImageMetadata = AppDatabase.getInstance(activity).imageMetadataDao().getByDates(filterStartDate.getTime(), filterEndDate.getTime());
        }
        // otherwise get all images
        else {
            allImageMetadata = AppDatabase.getInstance(activity).imageMetadataDao().getAll();
        }

        // create file handlers for each of the images
        for (ImageMetadata imageMetadata: allImageMetadata) {
            imageMetadata.file = new File(imageMetadata.getFilePath());
        }

        return allImageMetadata;
    }

    private static ArrayList<String> getGalleryImages(Activity activity) {
        // query Android MediaStore to find all gallery images

        Cursor cursor = activity.getContentResolver().query(
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                new String[]{},
                "",
                new String[]{},
                "");

        ArrayList<String> imgPaths = new ArrayList<>();

        cursor.moveToFirst();

        while (!cursor.isAfterLast()) {
            imgPaths.add(cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.DATA)));
            cursor.moveToNext();
        }
        cursor.close();
        return imgPaths;
    }

    private static void loadMetadata(ImageMetadata imageMetadata, String filePath) {
        InputStream in = null;
        try {
            in = new FileInputStream(filePath);
            ExifInterface exifInterface = new ExifInterface(filePath);

            // extract and set lat and long
            double[] latLong = exifInterface.getLatLong();
            if (latLong != null && latLong.length == 2) {
                imageMetadata.setLatitude(latLong[0]);
                imageMetadata.setLongitude(latLong[1]);
            }

            double altitude = exifInterface.getAltitude(0);
            imageMetadata.setAltitude(altitude);

            // extract dimensions
            int width = exifInterface.getAttributeInt(ExifInterface.TAG_IMAGE_WIDTH, 0);
            int height = exifInterface.getAttributeInt(ExifInterface.TAG_IMAGE_LENGTH, 0);
            int fSize = (int)new File(filePath).length();

            // dimensions not part of exif, we need to get them manually
            if (width == 0 || height == 0) {
                BitmapFactory.Options options = new BitmapFactory.Options();
                options.inJustDecodeBounds = true;
                Bitmap b = BitmapFactory.decodeFile(new File(filePath).getAbsolutePath(), options);
                width = options.outWidth;
                height = options.outHeight;
            }

            imageMetadata.setWidth(width);
            imageMetadata.setHeight(height);
            imageMetadata.setFileSize(fSize);

            // get date modified from filesystem
            // (could get this from exif, but it wouldn't always be available
            //  which would result in 1970s dates)
            Date lastModified = new Date(new File(filePath).lastModified());

            imageMetadata.setDateAdded(lastModified);

        }
        catch (IOException e) {
            e.printStackTrace(System.err);
        }
        finally {
            if (in != null) {
                try {
                    in.close();
                }
                catch (IOException ignored) {

                }
            }
        }
    }
}
