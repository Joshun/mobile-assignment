package com4510.thebestphotogallery.Activities;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

import com4510.thebestphotogallery.Database.AppDatabase;
import com4510.thebestphotogallery.Database.DatabaseResponseListener;
import com4510.thebestphotogallery.Database.ImageMetadata;
import com4510.thebestphotogallery.Images.LoadImagesResponseListener;
import com4510.thebestphotogallery.Tasks.LoadImagesTask;
import com4510.thebestphotogallery.MyAdapter;
import com4510.thebestphotogallery.R;
import com4510.thebestphotogallery.Tasks.ReadFromDatabaseTask;
import com4510.thebestphotogallery.Util;

import static android.support.v7.widget.RecyclerView.SCROLL_STATE_DRAGGING;

public class MainActivity extends AppCompatActivity implements DatabaseResponseListener, LoadImagesResponseListener {
    private RecyclerView.Adapter recyclerViewAdapter;
    private SwipeRefreshLayout swipeContainer;
    private List<ImageMetadata> imageMetadataList = new ArrayList<>();
    static final int REQUEST_IMAGE_CAPTURE = 1;

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_view, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Intent intent;
        switch (item.getItemId()) {
            case R.id.btn_camera:
                intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                if (intent.resolveActivity(getPackageManager()) != null) {
                    startActivityForResult(intent, REQUEST_IMAGE_CAPTURE);
                }
                return true;
        }
        return true;
    }

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


        RecyclerView recyclerView = findViewById(R.id.grid_recycler_view);
        int numberOfColumns = 4;
        final GridLayoutManager layoutManager = new GridLayoutManager(this, numberOfColumns);
        recyclerView.setLayoutManager(layoutManager);
        recyclerViewAdapter = new MyAdapter(imageMetadataList);
        recyclerView.setAdapter(recyclerViewAdapter);
        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            private boolean moving = false;

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                final int firstItemPosition = layoutManager.findFirstCompletelyVisibleItemPosition();
                final int lastItemPosition = layoutManager.findLastCompletelyVisibleItemPosition();
                final boolean scrolledUp = dy < 0;

                if (moving) {
                    ((MyAdapter) recyclerViewAdapter).cancelLoading(firstItemPosition, lastItemPosition, scrolledUp);
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
