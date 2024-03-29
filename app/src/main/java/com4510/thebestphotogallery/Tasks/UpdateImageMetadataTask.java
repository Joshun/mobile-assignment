package com4510.thebestphotogallery.Tasks;

import android.app.Activity;
import android.os.AsyncTask;

import com4510.thebestphotogallery.Database.AppDatabase;
import com4510.thebestphotogallery.Database.ImageMetadata;
import com4510.thebestphotogallery.Database.ImageMetadataDao;
import com4510.thebestphotogallery.Listeners.UpdateImageMetadataListener;

/**
 * AsyncTask for updating image metadata, notably when it is edited by the user
 */

public class UpdateImageMetadataTask extends AsyncTask<UpdateImageMetadataTask.UpdateMetadataParam, Void, Void> {
    private UpdateImageMetadataListener updateImageMetadataListener;
    private ImageMetadata updatedMetadata = null;

    public UpdateImageMetadataTask() {
        updateImageMetadataListener = null;
    }

    public UpdateImageMetadataTask(UpdateImageMetadataListener l) {
        updateImageMetadataListener = l;
    }

    public static class UpdateMetadataParam {
        public Activity activity;
        public ImageMetadata imageMetadata;
    }

    @Override
    protected Void doInBackground(UpdateImageMetadataTask.UpdateMetadataParam... updateImageMetadataTasks) {
        ImageMetadataDao imageMetadataDao = AppDatabase.getInstance(updateImageMetadataTasks[0].activity).imageMetadataDao();
        imageMetadataDao.update(updateImageMetadataTasks[0].imageMetadata);
        updatedMetadata = updateImageMetadataTasks[0].imageMetadata;
        return null;
    }

    @Override
    protected void onPostExecute(Void aVoid) {
        super.onPostExecute(aVoid);
        if (updateImageMetadataListener != null) {
            updateImageMetadataListener.imageUpdated(updatedMetadata);
        }
    }
}
