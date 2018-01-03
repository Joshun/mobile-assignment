package com4510.thebestphotogallery.Images;

import android.graphics.Bitmap;
import android.util.Log;

import java.io.File;
import java.lang.ref.WeakReference;
import java.util.List;

import com4510.thebestphotogallery.Activities.ImageLoadActivity;
import com4510.thebestphotogallery.Util;

/**
 * Created by George on 03-Jan-18.
 */

public class PreloadImageAsync extends ImageAsync {

    private WeakReference<ImageLoadActivity> activity;
    private WeakReference<Util.BitmapList> bitmaps;
    private final int position;
    private final int filesToLoad;

    public PreloadImageAsync(ImageLoadActivity activity, Util.BitmapList bitmaps, File file, final int position, final int filesToLoad) {
        super(file);
        this.activity = new WeakReference<ImageLoadActivity>(activity);
        this.bitmaps = new WeakReference<Util.BitmapList>(bitmaps);
        this.position = position;
        this.filesToLoad = filesToLoad;
    }

    @Override
    protected void onPostExecute(Bitmap bitmap) {
        bitmaps.get().getList().set(position, bitmap);
        bitmaps.get().incLoadedSize(1);
        Log.v("Bitmaps", "Reserved size: " + bitmaps.get().getList().size() + ", Loaded size: " + bitmaps.get().getLoadedSize());
        if (bitmaps.get().getLoadedSize() >= filesToLoad) {
            activity.get().finishedLoading();
        }
    }

}
