package com4510.thebestphotogallery.Tasks;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.support.media.ExifInterface;
import android.util.Log;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import com4510.thebestphotogallery.Database.AppDatabase;
import com4510.thebestphotogallery.Database.ImageMetadata;
import com4510.thebestphotogallery.Images.ImageLoader;
import com4510.thebestphotogallery.Images.LoadImagesResponseListener;

/**
 * Created by joshua on 02/01/18.
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

    protected static void loadExif(ImageMetadata imageMetadata, String filePath) {
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
            System.out.println("fSize " + fSize);

            // dimensions not part of exif, we need to get them manually
            if (width == 0 || height == 0) {
                BitmapFactory.Options options = new BitmapFactory.Options();
                options.inJustDecodeBounds = true;
                System.out.println(new File(filePath).getAbsolutePath());
                Bitmap b = BitmapFactory.decodeFile(new File(filePath).getAbsolutePath(), options);
                width = options.outWidth;
                height = options.outHeight;
            }

            Log.v("dimens", "width " + width + " height " + height);

            imageMetadata.setWidth(width);
            imageMetadata.setHeight(height);
            imageMetadata.setFileSize(fSize);

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

    @Override
    protected List<ImageMetadata> doInBackground(LoadImagesTaskParam... loadImagesTaskParams) {
        Activity activity = loadImagesTaskParams[0].activity;
        Calendar filterStartDate = loadImagesTaskParams[0].filterStartDate;
        Calendar filterEndDate = loadImagesTaskParams[0].filterEndDate;
        List<String> imagePaths = ImageLoader.loadImages(activity);
        List<ImageMetadata> imageMetadataList = new ArrayList<>();

        for (String p: imagePaths) {
            ImageMetadata imageMetadata = new ImageMetadata();
            loadExif(imageMetadata, p);
            imageMetadata.setFilePath(p);
            imageMetadataList.add(imageMetadata);
        }

        System.out.println(imagePaths);
//        System.out.println(imageMetadataList.toArray());
//        AppDatabase.getInstance(activity).imageMetadataDao().insertAll((ImageMetadata[])imageMetadataList.toArray());
        AppDatabase.getInstance(activity).imageMetadataDao().insertAll((imageMetadataList.toArray(new ImageMetadata[imageMetadataList.size()])));

//        for (ImageMetadata imageMetadata: imageMetadataList) {
//            AppDatabase.getInstance(activity).imageMetadataDao().insert(imageMetadata);
//        }


        List<ImageMetadata> allImageMetadata = AppDatabase.getInstance(activity).imageMetadataDao().getAll();
        System.out.println(allImageMetadata);
        System.out.println(allImageMetadata.get(0));

        for (ImageMetadata imageMetadata: allImageMetadata) {
            imageMetadata.file = new File(imageMetadata.getFilePath());
        }

        return allImageMetadata;
    }
}
