package com4510.thebestphotogallery.Images;

import android.graphics.Bitmap;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import java.io.File;
import java.lang.ref.WeakReference;

import com4510.thebestphotogallery.R;

/**
 * Created by George on 01-Jan-18.
 */

public class ShowImageAsync extends ImageAsync {

    protected WeakReference<ImageView> imageView;
    protected WeakReference<View> loadingView;

    public ShowImageAsync(final ImageView imageView, final View loadingView, final File file) {
        super(file, 2048, false, true);
        this.imageView = new WeakReference<ImageView>(imageView);
        this.loadingView = new WeakReference<View>(loadingView);
    }

    @Override
    protected void onPostExecute(Bitmap bitmap) {
        if (bitmap != null && loadingView != null && imageView != null) {
            loadingView.get().setVisibility(View.GONE);
            imageView.get().setImageBitmap(bitmap);
        }
        else {
            Log.e("ShowImageAsync", "Failed to load bitmap");
        }

        super.onPostExecute(bitmap);
    }

}
