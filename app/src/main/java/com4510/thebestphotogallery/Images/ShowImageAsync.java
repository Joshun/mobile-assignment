package com4510.thebestphotogallery.Images;

import android.graphics.Bitmap;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import java.io.File;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

import com4510.thebestphotogallery.R;

/**
 * Created by George on 01-Jan-18.
 */

public class ShowImageAsync extends ImageAsync {

    protected WeakReference<ImageView> imageView;
    protected WeakReference<View> loadingView;
    protected WeakReference<View> detailsView;
    protected WeakReference<List<Bitmap>> bitmapMipMaps;

    public ShowImageAsync(final ImageView imageView, final View loadingView, final View detailsView, final List<Bitmap> bitmapMipMaps, final File file) {
        super(file, 2048, false, true);
        this.imageView = new WeakReference<ImageView>(imageView);
        this.loadingView = new WeakReference<View>(loadingView);
        this.detailsView = new WeakReference<View>(detailsView);
        this.bitmapMipMaps = new WeakReference<List<Bitmap>>(bitmapMipMaps);
    }

    @Override
    protected void onPostExecute(Bitmap bitmap) {
        if (bitmap != null && nullCheck()) {
            loadingView.get().setVisibility(View.GONE);
            imageView.get().setImageBitmap(bitmap);

            //Generate mipmaps
//            final int DIVIDER = 2;
//            Bitmap mip1 = Bitmap.createScaledBitmap(bitmap, bitmap.getWidth() / DIVIDER, bitmap.getHeight() / DIVIDER, true);
//            Bitmap mip2 = Bitmap.createScaledBitmap(mip1, mip1.getWidth() / DIVIDER, mip1.getHeight() / DIVIDER, true);
//            Bitmap mip3 = Bitmap.createScaledBitmap(mip2, mip2.getWidth() / DIVIDER, mip2.getHeight() / DIVIDER, true);
//            Bitmap mip4 = Bitmap.createScaledBitmap(mip3, mip3.getWidth() / DIVIDER, mip3.getHeight() / DIVIDER, true);
//            if (bitmapMipMaps.get() != null) {
//                bitmapMipMaps.get().add(bitmap);
//                bitmapMipMaps.get().add(mip1);
//                bitmapMipMaps.get().add(mip2);
//                bitmapMipMaps.get().add(mip3);
//                bitmapMipMaps.get().add(mip4);
//            }

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
