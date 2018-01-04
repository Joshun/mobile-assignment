/*
 * Copyright (c) 2017. This code has been developed by Fabio Ciravegna, The University of Sheffield. All rights reserved. No part of this code can be used without the explicit written permission by the author
 */

package com4510.thebestphotogallery.Activities;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;

import com4510.thebestphotogallery.Database.ImageMetadata;
import com4510.thebestphotogallery.MyAdapter;
import com4510.thebestphotogallery.R;
import com4510.thebestphotogallery.ServerComm;
import com4510.thebestphotogallery.ServerData;
import com4510.thebestphotogallery.Images.ShowImageAsync;

public class ShowImageActivity extends AppCompatActivity {

    private Bitmap currentImage = null;
    private String currentImageFile = "file.png";

    private ServerComm serverComm;

    Integer imageIndex = null;

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.image_view, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Intent intent;
        switch (item.getItemId()) {
            case R.id.view_map_menuentry:
                Log.v(getClass().getName(), "map option selected");
                return true;
            case R.id.view_image_details_menuentry:
                Log.v(getClass().getName(), "view detail option selected");
                intent = new Intent(this, ViewDetailsActivity.class);
                intent.putExtra("position", imageIndex);
                startActivity(intent);
                return true;
            case R.id.edit_image_details_menuentry:
                Log.v(getClass().getName(), "edit detail option selected");
                intent = new Intent(this, EditDetailsActivity.class);
                intent.putExtra("position", imageIndex);
                startActivity(intent);
                return true;
            case android.R.id.home:
                onBackPressed();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_message2);

        Toolbar toolbar = findViewById(R.id.showmessage_toolbar);
        serverComm = new ServerComm(getCacheDir());

        View loadingView = findViewById(R.id.image_loading);

        Log.v(getClass().getName(), "image index is null");

        if (savedInstanceState != null) {
            Log.v(getClass().getName(), "loaded instance state");
            imageIndex = savedInstanceState.getInt("position");
        }
        else {

            Bundle b = getIntent().getExtras();
            //            int position = -1;
            imageIndex = -1;
            if (b != null) {
                // this is the image position in the itemList
                imageIndex = b.getInt("position");
            }
        }

        ImageView imageView = (ImageView) findViewById(R.id.image);
        ImageMetadata element = MyAdapter.getItem(imageIndex);
        Log.v("Name", "" + element.file.getName());

        currentImageFile = element.file.getAbsolutePath();
        ShowImageAsync imageAsync = new ShowImageAsync(imageView, loadingView, element.file);
        imageAsync.execute();

        toolbar.setTitle(element.file.getName());
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        imageView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                Log.v(getClass().getName(), "touch event");
                if (currentImage != null) {
                    Log.v(getClass().getName(), "attempting to send to server...");
                    ServerData serverData = new ServerData();
                    serverData.imageData = v.getDrawingCache();
                    serverData.title = "title";
                    serverData.longitude = "0.0";
                    serverData.latitude = "0.0";
                    serverData.description = "description";
                    serverData.imageFilename = currentImageFile;
                    serverData.date = "1/1/1";
                    serverComm.sendData(serverData);
                }
                return false;
            }
        });
    }

}
