package com4510.thebestphotogallery;

import android.app.Activity;
import android.os.AsyncTask;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by joshua on 02/01/18.
 */

public class LoadImagesTask extends AsyncTask<LoadImagesTask.LoadImagesTaskParam, Void, List<ImageMetadata>> {
    public static class LoadImagesTaskParam {
        public Activity activity;
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
        List<String> imagePaths = ImageLoader.loadImages(activity);
        List<ImageMetadata> imageMetadataList = new ArrayList<>();

        for (String p: imagePaths) {
            ImageMetadata imageMetadata = new ImageMetadata();
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
