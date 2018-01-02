/*
 * Copyright (c) 2017. This code has been developed by Fabio Ciravegna, The University of Sheffield. All rights reserved. No part of this code can be used without the explicit written permission by the author
 */

package com4510.thebestphotogallery;

import android.app.ActionBar;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
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
        switch (item.getItemId()) {
            case R.id.image_details_menuentry:
                Log.v(getClass().getName(), "detail option selected");
                Intent intent = new Intent(this, ImageDetailsActivity.class);
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
        setSupportActionBar(toolbar);

        serverComm = new ServerComm(getCacheDir());

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);



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

//        if (imageIndex!=-1){
            ImageView imageView = (ImageView) findViewById(R.id.image);
//            ImageElement element= MyAdapter.getItems().get(imageIndex);
            ImageMetadata element = MyAdapter.getItems().get(imageIndex);
            Log.v("Name", "" + element.file.getName());

            currentImageFile = element.file.getAbsolutePath();
//            if (element.image!=-1) {
//                imageView.setImageResource(element.image);
//            } else if (element.file!=null) {
                ShowImageAsync imageAsync = new ShowImageAsync(imageView, element.file);
                imageAsync.execute();
//                Bitmap image = Util.loadBitmap(element.file, 2, 2048);
//                if (image != null) {
//                    imageView.setImageBitmap(image);
//                    currentImage = image;
//                    currentImageFile = element.file.getName();
//                }
//                else {
//                    Log.e("Error", "Failed to load bitmap");
//                }

//            }

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
//        }
    }

}
