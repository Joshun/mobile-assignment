/*
 * Copyright (c) 2017. This code has been developed by Fabio Ciravegna, The University of Sheffield. All rights reserved. No part of this code can be used without the explicit written permission by the author
 */

package com4510.thebestphotogallery;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import com4510.thebestphotogallery.Activities.ShowImageActivity;
import com4510.thebestphotogallery.Database.ImageMetadata;
import com4510.thebestphotogallery.Images.MenuImageAsync;

public class MyAdapter extends RecyclerView.Adapter<MyAdapter.View_Holder> {
    private Context context;
    private CopyOnWriteArrayList<ImageMetadata> items;
    private CopyOnWriteArrayList<Bitmap> bitmaps;
    private OnBottomReachedListener onBottomReachedListener;

    public MyAdapter(Context cont, List<ImageMetadata> items, List<Bitmap> bitmaps) {
        super();
        this.items = new CopyOnWriteArrayList<>(items);
        this.bitmaps = new CopyOnWriteArrayList<>(bitmaps);
        context = cont;
    }

    public MyAdapter(List<ImageMetadata> items, List<Bitmap> bitmaps) {
        this(null, items, bitmaps);
    }

    @Override
    public View_Holder onCreateViewHolder(ViewGroup parent, int viewType) {
        //Inflate the layout, initialize the View Holder
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_image,
                parent, false);
        View_Holder holder = new View_Holder(v);
        context= parent.getContext();
        return holder;
    }

    @Override
    public void onBindViewHolder(final View_Holder holder, final int position) {
        if (position < bitmaps.size()) {

            if (onBottomReachedListener != null &&  position == bitmaps.size() - 1) {
                onBottomReachedListener.onBottomReached(position);
            }

            if (bitmaps.get(position) != null) {
                holder.imageView.setImageBitmap(bitmaps.get(position));
                holder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(context, ShowImageActivity.class);
                        intent.putExtra("position", position);
                        intent.putExtra("metadata", getItem(position));
                        context.startActivity(intent);
                    }
                });
            }
        }
    }


    // convenience method for getting data at click position
    public ImageMetadata getItem(int position) {
        return items.get(position);
    }

    @Override
    public int getItemCount() {
        return bitmaps.size();
    }

    public class View_Holder extends RecyclerView.ViewHolder  {
        public ImageView imageView;

        View_Holder(View itemView) {
            super(itemView);
            imageView = (ImageView) itemView.findViewById(R.id.image_item);

        }
    }

    public void clear() {
        this.bitmaps.clear();
        this.items.clear();
        notifyDataSetChanged();
    }

    public final boolean isEmpty() {
        return this.bitmaps.isEmpty() && this.items.isEmpty();
    }

    public void setItems(List<ImageMetadata> items) {
        this.items = new CopyOnWriteArrayList<>(items);
        notifyDataSetChanged();
    }

    public void addBitmaps(List<Bitmap> bitmaps) {
        this.bitmaps.addAll(bitmaps);
        notifyDataSetChanged();
    }

    public void setOnBottomReachedListener(OnBottomReachedListener onBottomReachedListener) {
        this.onBottomReachedListener = onBottomReachedListener;
    }

}