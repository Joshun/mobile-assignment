package com4510.thebestphotogallery.Images;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.util.Log;
import android.view.View;

import java.io.File;
import java.lang.ref.WeakReference;

import com4510.thebestphotogallery.Util;

/**
 * Created by George on 01-Jan-18.
 */

public class ImageAsync extends AsyncTask<Void, Void, Bitmap> {

    protected File file = null;
    protected int dimension = 0;
    protected boolean square = false;

    public ImageAsync(final File file, final int dimension, final boolean square) {
        this.file = file;
        this.dimension = dimension;
        this.square = square;
    }
    public ImageAsync(final File file, final int dimension) {
        this(file, dimension, true);
    }
    public ImageAsync(final File file) {
        this(file, 128);
    }
    public ImageAsync() {
        this(null);
    }

    @Override
    protected Bitmap doInBackground(Void... data) {
        Bitmap b = Util.loadBitmap(file, dimension, square);

        if (isCancelled()) {
            return null;
        }
        return b;
    }

}
