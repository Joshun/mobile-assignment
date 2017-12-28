package com4510.thebestphotogallery;

import android.os.AsyncTask;

import java.util.List;

/**
 * Created by joshua on 28/12/17.
 */

public class ReadFromDatabaseTask extends AsyncTask<ImageMetadataDao, Void, List<ImageMetadata>> {

    private DatabaseResponseListener databaseResponseListenerListener;

    public ReadFromDatabaseTask() {
        databaseResponseListenerListener = null;
    }

    public ReadFromDatabaseTask(DatabaseResponseListener l) {
        databaseResponseListenerListener = l;
    }

    @Override
    protected List<ImageMetadata> doInBackground(ImageMetadataDao... daos) {
        ImageMetadataDao imageMetadataDao = daos[0];
        return imageMetadataDao.getAll();
    }

    @Override
    protected void onPostExecute(List<ImageMetadata> imageMetadata) {
        if (databaseResponseListenerListener != null) {
            databaseResponseListenerListener.onDatabaseRead(imageMetadata);
        }
    }
}
