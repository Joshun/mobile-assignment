package com4510.thebestphotogallery.Tasks;

import android.graphics.Bitmap;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import java.io.File;
import java.lang.ref.WeakReference;
import java.util.List;

/**
 * Async class for handling the image loading in ShowImageActivity
 * Created by George on 01-Jan-18.
 */

public class ShowImageAsync extends ImageAsync {

    protected WeakReference<ImageView> imageView;
    protected WeakReference<View> loadingView;
    protected WeakReference<View> detailsView;

    /**
     * Constructor
     * @param imageView the image view that will house the loaded bitmap
     * @param loadingView the loading view to be hidden once loaded
     * @param detailsView the view to be shown once loaded
     * @param file the file to be loaded
     */
    public ShowImageAsync(final ImageView imageView, final View loadingView, final View detailsView, final File file) {
        super(file, 2048, false, true);
        this.imageView = new WeakReference<ImageView>(imageView);
        this.loadingView = new WeakReference<View>(loadingView);
        this.detailsView = new WeakReference<View>(detailsView);
    }

    @Override
    protected void onPostExecute(Bitmap bitmap) {
        if (bitmap != null && nullCheck()) {
            //Hiding the loading animation
            loadingView.get().setVisibility(View.GONE);
            imageView.get().setImageBitmap(bitmap);

            //Fading animation
            detailsView.get().setVisibility(View.VISIBLE);
            detailsView.get().setAlpha(0.0f);
            final int animDuration = detailsView.get().getResources().getInteger(android.R.integer.config_shortAnimTime);
            detailsView.get().animate().alpha(1.0f).setDuration(animDuration).setListener(null);
        }
        else {
            Log.e("ShowImageAsync", "Failed to load bitmap");
        }

        super.onPostExecute(bitmap);
    }

    private final boolean nullCheck() {
        return loadingView != null && imageView != null && detailsView != null;
    }

}
