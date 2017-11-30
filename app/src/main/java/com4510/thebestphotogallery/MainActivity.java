package com4510.thebestphotogallery;

import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.widget.Toast;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import pl.aprilapps.easyphotopicker.EasyImage;


public class MainActivity extends AppCompatActivity {
    private RecyclerView recyclerView;
    private RecyclerView.Adapter recyclerViewAdapter;
    private SwipeRefreshLayout swipeContainer;
    private List<ImageElement> pictureList = new ArrayList<>();

    public void doLoadImages() {
        pictureList.clear();
        ArrayList<String> imagePaths = ImageLoader.loadImages(this);
//           for (Integer imgId: imageIds) {
//               pictureList.add(new ImageElement(imgId));
//               System.out.println(imgId);
//           }
        for (String p: imagePaths) {
            System.out.println(p);
            pictureList.add(new ImageElement(new File(p)));
        }
        recyclerViewAdapter.notifyDataSetChanged();
//        recyclerView.setAdapter(recyclerViewAdapter);
        Util.initEasyImage(this);

        System.out.println(pictureList);
    }

    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (grantResults.length < 1 || grantResults[0] != PackageManager.PERMISSION_GRANTED) {
                System.out.println("permissions not granted");
        }
        else {
//           ArrayList<String> imagePaths = ImageLoader.loadImages(this);
////           for (Integer imgId: imageIds) {
////               pictureList.add(new ImageElement(imgId));
////               System.out.println(imgId);
////           }
//            for (String p: imagePaths) {
//                System.out.println(p);
//                pictureList.add(new ImageElement(new File(p)));
//            }
//            Util.initEasyImage(this);
//           recyclerViewAdapter.notifyDataSetChanged();
//           System.out.println(pictureList);
            doLoadImages();


        }

    }



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        swipeContainer = findViewById(R.id.swipe_refresh_layout);
        swipeContainer.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                System.out.println("refreshing...");
//                pictureList = new ArrayList<>();
                doLoadImages();
                swipeContainer.setRefreshing(false);
            }
        });


        recyclerView = findViewById(R.id.grid_recycler_view);
        int numberOfColumns = 4;
        recyclerView.setLayoutManager(new GridLayoutManager(this, numberOfColumns));
        recyclerViewAdapter = new MyAdapter(pictureList);
        recyclerView.setAdapter(recyclerViewAdapter);

        Util.checkPermissions(getApplicationContext(), this);


    }




}
