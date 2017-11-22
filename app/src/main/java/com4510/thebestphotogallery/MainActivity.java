package com4510.thebestphotogallery;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;

import pl.aprilapps.easyphotopicker.EasyImage;


public class MainActivity extends AppCompatActivity {
    private RecyclerView recyclerView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        recyclerView = findViewById(R.id.grid_recycler_view);

        Util.checkPermissions(getApplicationContext(), this);
        Util.initEasyImage(this);
    }




}
