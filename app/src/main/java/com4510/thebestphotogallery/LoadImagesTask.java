package com4510.thebestphotogallery;

import android.app.Activity;
import android.os.AsyncTask;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by joshua on 02/01/18.
 */

public class LoadImagesTask extends AsyncTask<LoadImagesTask.LoadImagesTaskParam, Void, List<ImageMetadata>> {
    public class LoadImagesTaskParam {
        public Activity activity;
        public ImageMetadataDao imageMetadataDao;
    }

    @Override
    protected void onPostExecute(List<ImageMetadata> imageMetadata) {
        super.onPostExecute(imageMetadata);
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
        AppDatabase.getInstance(activity).imageMetadataDao().insertAll((ImageMetadata[])imageMetadataList.toArray());

        List<ImageMetadata> allImageMetadata = AppDatabase.getInstance(activity).imageMetadataDao().getAll();

        return allImageMetadata;
    }
}
