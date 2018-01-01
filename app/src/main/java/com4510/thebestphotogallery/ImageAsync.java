package com4510.thebestphotogallery;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.util.Log;
import android.view.View;

import java.io.File;
import java.lang.ref.WeakReference;

/**
 * Created by Philip on 01-Jan-18.
 */

public class ImageAsync extends AsyncTask<Void, Void, Bitmap> {

    protected File file = null;
    protected int sampleSize = 0;
    protected int dimension = 0;

    ImageAsync(final File file, final int sampleSize, final int dimension) {
        this.file = file;
        this.sampleSize = sampleSize;
        this.dimension = dimension;
    }
    ImageAsync(final File file, final int sampleSize) {
        this(file, sampleSize, 512);
    }
    ImageAsync(final File file) {
        this(file,4);
    }
    ImageAsync() {
        this(null);
    }

    @Override
    protected Bitmap doInBackground(Void... data) {
        Bitmap b = Util.loadBitmap(file);

        if (isCancelled()) {
            return null;
        }
        return Util.loadBitmap(file);
    }

}
