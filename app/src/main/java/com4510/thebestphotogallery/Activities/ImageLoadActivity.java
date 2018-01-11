package com4510.thebestphotogallery.Activities;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import com4510.thebestphotogallery.Database.DatabaseResponseListener;
import com4510.thebestphotogallery.Database.ImageMetadata;
import com4510.thebestphotogallery.ImageMetadataList;
import com4510.thebestphotogallery.Images.LoadImagesResponseListener;
import com4510.thebestphotogallery.Images.PreloadImageAsync;
import com4510.thebestphotogallery.Tasks.LoadImagesTask;
import com4510.thebestphotogallery.Util;

/**
 * Created by George on 03-Jan-18.
 */

public abstract class ImageLoadActivity extends AppCompatActivity implements LoadImagesResponseListener, DatabaseResponseListener {
    protected final int UPDATE_DATA = 2;

    private boolean loading = false;
    protected static final int BLOCK_SIZE = 32;
    protected List<ImageMetadata> imageMetadataList = new ArrayList<>();
    protected Util.BitmapList bitmaps = new Util.BitmapList();
    private int softCap = 0;

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

    public final boolean atSoftCap() {
        return bitmaps.getLoadedSize() >= softCap;
    }

    public void incSoftCap() {
        softCap += BLOCK_SIZE * 4;
    }

    public final boolean moreToLoad() {
        return bitmaps.getList().size() < imageMetadataList.size();
    }

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

    @Override
    public void onDatabaseRead(List<ImageMetadata> imageMetadataList) {
        Log.v(getClass().getName(), "loaded image metadata database.");
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


    public void doLoadImages(){
        doLoadImages(null,null);
    }

    public void doLoadImages(Calendar filterStartDate, Calendar filterEndDate) {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            Log.v(getClass().getName(), "doLoadImages: read permission not granted");
            return;
        }

        this.bitmaps.clear();
        LoadImagesTask loadImagesTask = new LoadImagesTask(this);
        LoadImagesTask.LoadImagesTaskParam loadImagesTaskParam = new LoadImagesTask.LoadImagesTaskParam();
        loadImagesTaskParam.activity = this;
        loadImagesTaskParam.filterStartDate = filterStartDate;
        loadImagesTaskParam.filterEndDate = filterEndDate;
        loadImagesTask.execute(loadImagesTaskParam);
    }

    public void refresh() {
        loading = false;
        softCap = 0;
        imageMetadataList.clear();
        bitmaps.clear();
        doLoadImages();
    }


}
