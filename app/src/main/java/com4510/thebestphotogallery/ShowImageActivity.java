/*
 * Copyright (c) 2017. This code has been developed by Fabio Ciravegna, The University of Sheffield. All rights reserved. No part of this code can be used without the explicit written permission by the author
 */

package com4510.thebestphotogallery;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;

public class ShowImageActivity extends AppCompatActivity {

    private Bitmap currentImage = null;
    private String currentImageFile = "file.png";

    private ServerComm serverComm;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_message2);

        serverComm = new ServerComm(getCacheDir());

        Bundle b = getIntent().getExtras();
        int position=-1;
        if(b != null) {
            // this is the image position in the itemList
            position = b.getInt("position");
            if (position!=-1){
                ImageView imageView = (ImageView) findViewById(R.id.image);
                ImageElement element= MyAdapter.getItems().get(position);
                if (element.image!=-1) {
                    imageView.setImageResource(element.image);
                } else if (element.file!=null) {
                    Bitmap myBitmap = BitmapFactory.decodeFile(element.file.getAbsolutePath());
                    imageView.setImageBitmap(myBitmap);
                    currentImage = myBitmap;
                    currentImageFile = element.file.getName();
                }

                imageView.setOnTouchListener(new View.OnTouchListener() {
                    @Override
                    public boolean onTouch(View v, MotionEvent event) {
                        Log.v(getClass().getName(), "touch event");
                        if (currentImage != null) {
                            Log.v(getClass().getName(), "attempting to send to server...");
                            ServerData serverData = new ServerData();
                            serverData.imageData = currentImage;
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
    }

}
