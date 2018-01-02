/*
 * Copyright (c) 2017. This code has been developed by Fabio Ciravegna, The University of Sheffield. All rights reserved. No part of this code can be used without the explicit written permission by the author
 */

package com4510.thebestphotogallery;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import java.util.ArrayList;
import java.util.List;

public class MyAdapter extends RecyclerView.Adapter<MyAdapter.View_Holder> {
    private Context context;
    private static List<ImageMetadata> items;
    private List<MenuImageAsync> currentTasks;

    public MyAdapter(Context cont, List<ImageMetadata> items) {
        super();
        MyAdapter.items = items;
        this.currentTasks = new ArrayList<>();
        context = cont;
    }

    public MyAdapter(List<ImageMetadata> items) {
        this(null, items);
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

        //Use the provided View Holder on the onCreateViewHolder method to populate the
        // current row on the RecyclerView

        if (holder!=null && items.get(position)!=null) {
            holder.imageView.setImageBitmap(null);
            MenuImageAsync imageAsync = new MenuImageAsync(holder, context, items.get(position).file, position);
            currentTasks.add(imageAsync);
            imageAsync.execute();
        }
        //animate(holder);
    }


    // convenience method for getting data at click position
    ImageMetadata getItem(int id) {
        return items.get(id);
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    public class View_Holder extends RecyclerView.ViewHolder  {
        ImageView imageView;

        View_Holder(View itemView) {
            super(itemView);
            imageView = (ImageView) itemView.findViewById(R.id.image_item);

        }
    }

    private void clearFinished() {
        if (currentTasks != null && currentTasks.size() > 0) {
            ArrayList<MenuImageAsync> toRemove = new ArrayList<>();
            for (MenuImageAsync i : currentTasks) {
                if (i.getStatus() == AsyncTask.Status.FINISHED) {
                    toRemove.add(i);
                }
            }
            currentTasks.removeAll(toRemove);
        }
    }

    public void cancelLoading(int firstVisibleItem, int lastVisibleItem, boolean scrollingUp) {
        clearFinished();
        if (currentTasks != null && currentTasks.size() > 0) {
            for (MenuImageAsync i : currentTasks) {
                if (!scrollingUp && i.getPosition() < firstVisibleItem
                        || scrollingUp && i.getPosition() > lastVisibleItem) {
                    i.cancel(true);
                }
            }
        }
    }

    public void cancelAll() {
        clearFinished();
        for (MenuImageAsync i : currentTasks) {
            i.cancel(true);
        }
    }

    public static List<ImageMetadata> getItems() {
        return items;
    }

    public static void setItems(List<ImageMetadata> items) {
        MyAdapter.items = items;
    }
}