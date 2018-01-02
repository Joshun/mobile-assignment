package com4510.thebestphotogallery;

import android.os.AsyncTask;

/**
 * Created by joshua on 01/01/18.
 */

public class StoreInDatabaseTask extends AsyncTask<StoreInDatabaseTask.StoreInDbParam, Void, Void> {
    public class StoreInDbParam {
        public ImageMetadata imageMetadata;
        public ImageMetadataDao imageMetadataDao;
    }

    @Override
    protected Void doInBackground(StoreInDbParam... storeInDbParams) {
        return null;
    }
}
