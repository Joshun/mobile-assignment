package com4510.thebestphotogallery;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.util.Log;
import android.view.View;

import java.io.File;
import java.lang.ref.WeakReference;

/**
 * Created by Philip on 01-Jan-18.
 */

public class MenuImageAsync extends ImageAsync {

    protected MyAdapter.View_Holder holder = null;
    protected WeakReference<Context> context = null;
    protected int position = 0;

    MenuImageAsync(final MyAdapter.View_Holder holder, final Context context, final File file, final int position) {
        super(file);
        this.position = position;
        this.holder = holder;
        this.context = new WeakReference<Context>(context);
    }

    @Override
    protected void onPostExecute(Bitmap bitmap) {
        if (bitmap != null) {
            holder.imageView.setImageBitmap(bitmap);
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(context.get(), ShowImageActivity.class);
                    intent.putExtra("position", position);
                    context.get().startActivity(intent);
                }
            });
        }
        else {
            Log.e("MenuImageAsync", "Failed to load bitmap");
        }

        super.onPostExecute(bitmap);
    }

    public final int getPosition() {
        return position;
    }


}
