package com4510.thebestphotogallery.Images;

import android.graphics.Bitmap;
import android.util.Log;
import android.widget.ImageView;

import java.io.File;
import java.lang.ref.WeakReference;

/**
 * Created by George on 01-Jan-18.
 */

public class ShowImageAsync extends ImageAsync {

    protected WeakReference<ImageView> imageView;

    public ShowImageAsync(final ImageView imageView, final File file) {
        super(file, 2048, false);
        this.imageView = new WeakReference<ImageView>(imageView);
    }

    @Override
    protected void onPostExecute(Bitmap bitmap) {
        if (bitmap != null) {
            imageView.get().setImageBitmap(bitmap);
        }
        else {
            Log.e("ShowImageAsync", "Failed to load bitmap");
        }

        super.onPostExecute(bitmap);
    }

}
