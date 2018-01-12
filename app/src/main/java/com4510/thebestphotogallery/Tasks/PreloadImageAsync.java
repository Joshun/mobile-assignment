package com4510.thebestphotogallery.Tasks;

import android.graphics.Bitmap;
import android.util.Log;

import java.io.File;
import java.lang.ref.WeakReference;

import com4510.thebestphotogallery.Activities.ImageLoadActivity;
import com4510.thebestphotogallery.Util;

/**
 * Async class for handling the image loading in MainActivity
 * Once a batch of images have been loaded, the onFinishedBitmapLoad callback is triggered
 * Created by George on 03-Jan-18.
 */

public class PreloadImageAsync extends ImageAsync {

    private WeakReference<ImageLoadActivity> activity;
    private WeakReference<Util.BitmapList> bitmaps;
    private final int offset;
    private final int position;
    private final int filesToLoad;

    /**
     * Constructor
     * @param activity the parent activity
     * @param bitmaps the bitmap list
     * @param file the file to be loaded
     * @param offset the batch offset in the bitmap list
     * @param position the position of the bitmap in the batch
     * @param filesToLoad the number of files to load in the batch
     */
    public PreloadImageAsync(ImageLoadActivity activity, Util.BitmapList bitmaps, File file, final int offset, final int position, final int filesToLoad) {
        super(file);
        this.activity = new WeakReference<ImageLoadActivity>(activity);
        this.bitmaps = new WeakReference<Util.BitmapList>(bitmaps);
        this.offset = offset;
        this.position = position;
        this.filesToLoad = filesToLoad;
    }

    @Override
    protected void onPostExecute(Bitmap bitmap) {
        //Checking whether the bitmap list exists and whether there is a reserved space for the bitmap
        if (bitmaps != null && bitmaps.get() != null && bitmaps.get().getList().size() > offset + position) {

            //Inserting the bitmap and increasing the number of bitmaps loaded
            bitmaps.get().getList().set(offset + position, bitmap);
            bitmaps.get().incLoadedSize(1);

            //Checking whether the whole batch has finished loading
            if (bitmaps.get().getLoadedSize() >= offset + filesToLoad) {
                activity.get().onFinishedBitmapLoad(bitmaps.get().getList().subList(offset, offset + filesToLoad));
            }
        }
        else {
            Log.e("Nullpointer", "bitmaps is a nullpointer!");
        }
    }

}
