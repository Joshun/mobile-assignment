package com4510.thebestphotogallery.Images;

import android.graphics.Bitmap;
import android.util.Log;

import java.io.File;
import java.lang.ref.WeakReference;
import java.util.List;

import com4510.thebestphotogallery.Activities.ImageLoadActivity;

/**
 * Created by George on 03-Jan-18.
 */

public class PreloadImageAsync extends ImageAsync {

    private WeakReference<ImageLoadActivity> activity;
    private WeakReference<List<Bitmap>> bitmaps;
    int filesToLoad;

    public PreloadImageAsync(ImageLoadActivity activity, List<Bitmap> bitmaps, File file, final int filesToLoad) {
        super(file);
        this.activity = new WeakReference<ImageLoadActivity>(activity);
        this.bitmaps = new WeakReference<List<Bitmap>>(bitmaps);
        this.filesToLoad = filesToLoad;
    }

    @Override
    protected void onPostExecute(Bitmap bitmap) {
        bitmaps.get().add(bitmap);
        Log.v("Bitmaps", "" + bitmaps.get().size());
        if (bitmaps.get().size() >= filesToLoad) {
            activity.get().finishedLoading();
        }
    }

}
