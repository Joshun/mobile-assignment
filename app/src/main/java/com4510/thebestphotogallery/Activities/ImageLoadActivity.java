package com4510.thebestphotogallery.Activities;

import android.Manifest;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import com4510.thebestphotogallery.Database.ImageMetadata;
import com4510.thebestphotogallery.Listeners.LoadImagesResponseListener;
import com4510.thebestphotogallery.Tasks.PreloadImageAsync;
import com4510.thebestphotogallery.Tasks.LoadImagesTask;
import com4510.thebestphotogallery.Util;

/**
 * Parent activity for handling activites with batch image loading
 * Created by George on 03-Jan-18.
 */

public abstract class ImageLoadActivity extends AppCompatActivity implements LoadImagesResponseListener {
    protected final int UPDATE_DATA = 2;

    private boolean loading = false;
    protected static final int BLOCK_SIZE = 32;
    protected List<ImageMetadata> imageMetadataList = new ArrayList<>();
    protected Util.BitmapList bitmaps = new Util.BitmapList();
    private int softCap = 0;

    /**
     * Method for dispatching an image load batch
     * @param numberToLoad the number of images to load in the batch
     */
    public void dispatchBitmapLoad(final int numberToLoad) {
        if (numberToLoad > 0) {
            loading = true;

            //Reserves space in the bitmap list and dispatches async tasks to load the images
            final int offset = bitmaps.getList().size();
            for (int i = 0; i < numberToLoad; ++i) {
                //Checking whether there is actually a file to load to prevent null pointer exceptions
                if (offset + i >= imageMetadataList.size()) {
                    break;
                }
                bitmaps.getList().add(null);
                PreloadImageAsync imageAsync = new PreloadImageAsync(this, bitmaps, imageMetadataList.get(offset + i).file, offset, i, numberToLoad);
                imageAsync.execute();
            }
        }
    }

    /**
     * Callback for when a batch has finished loading
     * @param bitmaps the loaded bitmaps
     */
    public void onFinishedBitmapLoad(List<Bitmap> bitmaps) {
        loading = false;
    }

    public final boolean getLoading() {
        return loading;
    }

    /**
     * Whether the number of loaded bitmaps has reached a soft limit (for battery-preserving purposes)
     * @return whether the soft limit has been hit or not
     */
    public final boolean atSoftCap() {
        return bitmaps.getLoadedSize() >= softCap;
    }

    /**
     * Increases the soft limit
     */
    public void incSoftCap() {
        softCap += BLOCK_SIZE * 4;
    }

    /**
     * Whether there are more bitmaps to be loaded still
     * @return whether there are more bitmaps to be loaded still
     */
    public final boolean moreToLoad() {
        return bitmaps.getList().size() < imageMetadataList.size();
    }

    /**
     * Callback for when all of the metadata has been loaded
     * @param imageMetadataList the list of loaded metadata objects
     */
    @Override
    public void imagesLoaded(List<ImageMetadata> imageMetadataList) {
        this.imageMetadataList.clear();
        this.imageMetadataList.addAll(imageMetadataList);

        Util.initEasyImage(this);
        Log.v("Init image", "LOADED");
        Log.v("Image Count", "" + imageMetadataList.size());

        incSoftCap();
        dispatchBitmapLoad(Math.min(BLOCK_SIZE, imageMetadataList.size()));
    }

    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (grantResults.length < 1 || grantResults[0] != PackageManager.PERMISSION_GRANTED) {
            Toast.makeText(this, "App requires media read and write permissions to function correctly, please restart it and accept", Toast.LENGTH_LONG).show();
        }
        else {
            doLoadImages();
        }

    }

    /**
     * Starts an initial load of images
     */
    public void doLoadImages(){
        doLoadImages(null,null);
    }

    /**
     * Starts an initial load of images
     * @param filterStartDate start date to get images from
     * @param filterEndDate end date to get images from
     */
    public void doLoadImages(Calendar filterStartDate, Calendar filterEndDate) {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            Log.v(getClass().getName(), "doLoadImages: read permission not granted");
            return;
        }

        //Clears the bitmaps and starts a new image load
        this.bitmaps.clear();
        LoadImagesTask loadImagesTask = new LoadImagesTask(this);
        LoadImagesTask.LoadImagesTaskParam loadImagesTaskParam = new LoadImagesTask.LoadImagesTaskParam();
        loadImagesTaskParam.activity = this;
        loadImagesTaskParam.filterStartDate = filterStartDate;
        loadImagesTaskParam.filterEndDate = filterEndDate;
        loadImagesTask.execute(loadImagesTaskParam);
    }

    /**
     * Refreshes the list
     */
    public void refresh() {
        refresh(null, null);
    }

    /**
     * Refreshes the list
     * @param filterStartDate start date to get images from
     * @param filterEndDate end date to get images from
     */
    public void refresh(Calendar filterStartDate, Calendar filterEndDate) {
        loading = false;
        softCap = 0;
        imageMetadataList.clear();
        bitmaps.clear();
        doLoadImages(filterStartDate, filterEndDate);
    }


}
