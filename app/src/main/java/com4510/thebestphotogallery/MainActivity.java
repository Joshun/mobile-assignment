package com4510.thebestphotogallery;

import android.app.ActionBar;
import android.app.Activity;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.DragEvent;
import android.view.View;
import android.widget.Toast;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import pl.aprilapps.easyphotopicker.EasyImage;

import static android.support.v7.widget.RecyclerView.SCROLL_STATE_DRAGGING;


public class MainActivity extends AppCompatActivity implements DatabaseResponseListener, LoadImagesResponseListener {
    private RecyclerView recyclerView;
    private RecyclerView.Adapter recyclerViewAdapter;
    private SwipeRefreshLayout swipeContainer;
    private List<ImageMetadata> imageMetadataList = new ArrayList<>();

    @Override
    public void imagesLoaded(List<ImageMetadata> imageMetadataList) {
        this.imageMetadataList.clear();
        this.imageMetadataList.addAll(imageMetadataList);

        recyclerViewAdapter.notifyDataSetChanged();

        Util.initEasyImage(this);
        Log.v("Init image", "LOADED");
        Log.v("Image Count", "" + imageMetadataList.size());
    }


    public void doLoadImages() {
        LoadImagesTask loadImagesTask = new LoadImagesTask(this);
        LoadImagesTask.LoadImagesTaskParam loadImagesTaskParam = new LoadImagesTask.LoadImagesTaskParam();
        loadImagesTaskParam.activity = this;
        loadImagesTask.execute(loadImagesTaskParam);
    }

    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (grantResults.length < 1 || grantResults[0] != PackageManager.PERMISSION_GRANTED) {
                System.out.println("permissions not granted");
        }
        else {
            doLoadImages();
        }

    }


    @Override
    public void onDatabaseRead(List<ImageMetadata> imageMetadataList) {
        Log.v(getClass().getName(), "loaded image metadata database.");
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.main_toolbar);
        setSupportActionBar(toolbar);

        swipeContainer = findViewById(R.id.swipe_refresh_layout);
        swipeContainer.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                System.out.println("refreshing...");
                doLoadImages();
                swipeContainer.setRefreshing(false);
            }
        });


        recyclerView = findViewById(R.id.grid_recycler_view);
        int numberOfColumns = 4;
        final GridLayoutManager layoutManager = new GridLayoutManager(this, numberOfColumns);
        recyclerView.setLayoutManager(layoutManager);
        recyclerViewAdapter = new MyAdapter(imageMetadataList);
        recyclerView.setAdapter(recyclerViewAdapter);
        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            private boolean moving = false;
            private boolean fastScroll = false;

            //Logic here is designed to cancel out of view async tasks if scrolling slowly,
            //but if scrolling fast all async tasks are cancelled and restarted once scrolling has stopped

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                final int firstItemPosition = layoutManager.findFirstCompletelyVisibleItemPosition();
                final int lastItemPosition = layoutManager.findLastCompletelyVisibleItemPosition();
                final boolean scrolledUp = dy < 0;

                if (!fastScroll) {
                    fastScroll = Math.abs(dy) > 200;
                    if (fastScroll) {
                        ((MyAdapter) recyclerViewAdapter).cancelAll();
                    }
                }

                if (!fastScroll && moving) {
                    ((MyAdapter) recyclerViewAdapter).cancelLoading(firstItemPosition, lastItemPosition, scrolledUp);
                }
                else if (fastScroll && !moving) {
                    fastScroll = false;
                    doLoadImages();
                }
                super.onScrolled(recyclerView, dx, dy);
            }

            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int state) {
                moving = state == SCROLL_STATE_DRAGGING;
                super.onScrollStateChanged(recyclerView, state);
            }
        });

        Util.checkPermissions(getApplicationContext(), this);
        ReadFromDatabaseTask readFromDatabaseTask = new ReadFromDatabaseTask(this);
        readFromDatabaseTask.execute(AppDatabase.getInstance(this).imageMetadataDao());

        Util.initEasyImage(this);
        doLoadImages();
    }

}
