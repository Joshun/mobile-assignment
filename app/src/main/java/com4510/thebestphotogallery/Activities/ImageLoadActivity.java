package com4510.thebestphotogallery.Activities;

import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

import com4510.thebestphotogallery.Database.DatabaseResponseListener;
import com4510.thebestphotogallery.Database.ImageMetadata;
import com4510.thebestphotogallery.Images.LoadImagesResponseListener;
import com4510.thebestphotogallery.Images.PreloadImageAsync;
import com4510.thebestphotogallery.Tasks.LoadImagesTask;
import com4510.thebestphotogallery.Util;

/**
 * Created by George on 03-Jan-18.
 */

public abstract class ImageLoadActivity extends AppCompatActivity implements LoadImagesResponseListener, DatabaseResponseListener {

    private boolean loading = false;
    private static final int BLOCK_SIZE = 40;
    protected List<ImageMetadata> imageMetadataList = new ArrayList<>();
    protected Util.BitmapList bitmaps = new Util.BitmapList();

    public void dispatchBitmapLoad(final int numberToLoad) {
        loading = true;
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

    public void onFinishedBitmapLoad(List<Bitmap> bitmaps) {
        loading = false;
    }

    public final boolean getLoading() {
        return loading;
    }

    @Override
    public void imagesLoaded(List<ImageMetadata> imageMetadataList) {
        this.imageMetadataList.clear();
        this.imageMetadataList.addAll(imageMetadataList);

        Util.initEasyImage(this);
        Log.v("Init image", "LOADED");
        Log.v("Image Count", "" + imageMetadataList.size());

        dispatchBitmapLoad(BLOCK_SIZE);
    }

    @Override
    public void onDatabaseRead(List<ImageMetadata> imageMetadataList) {
        Log.v(getClass().getName(), "loaded image metadata database.");
    }

    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (grantResults.length < 1 || grantResults[0] != PackageManager.PERMISSION_GRANTED) {
            System.out.println("permissions not granted");
        }
        else {
            doLoadImages();
        }

    }

    public void doLoadImages() {
        this.bitmaps.clear();
        LoadImagesTask loadImagesTask = new LoadImagesTask(this);
        LoadImagesTask.LoadImagesTaskParam loadImagesTaskParam = new LoadImagesTask.LoadImagesTaskParam();
        loadImagesTaskParam.activity = this;
        loadImagesTask.execute(loadImagesTaskParam);
    }

}
