package com4510.thebestphotogallery.Activities;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.util.Log;
import android.net.Uri;
import android.support.v4.content.FileProvider;
import android.widget.Toast;

import java.util.Date;
import java.text.SimpleDateFormat;
import java.io.IOException;
import java.io.File;
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
import pl.aprilapps.easyphotopicker.EasyImage;

import static android.support.v7.widget.RecyclerView.SCROLL_STATE_DRAGGING;

public class MainActivity extends AppCompatActivity implements DatabaseResponseListener, LoadImagesResponseListener {
    private RecyclerView.Adapter recyclerViewAdapter;
    private SwipeRefreshLayout swipeContainer;
    private List<ImageMetadata> imageMetadataList = new ArrayList<>();
    static final int REQUEST_IMAGE_CAPTURE = 1;
    private Activity activity;

    String mCurrentPhotoPath;

    private boolean permissionsOk = true;

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_view, menu);
        return true;
    }

    private File createImageFile() throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";

//        File storageDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM);
        //  File storageDir = new File("/storage/emulated/0/DCIM/Camera/");
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        // Above works but does not save, first and second lines camera won't load?

        File image = File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",         /* suffix */
                storageDir      /* directory */
        );


        return image;
    }

    private void galleryAddPic() {
        File f = new File(mCurrentPhotoPath);
        System.out.println(f);
        Uri contentUri = Uri.fromFile(f);
        System.out.println(contentUri);
        Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE,contentUri);
        mediaScanIntent.setData(contentUri);
        System.out.println(mediaScanIntent);
        this.sendBroadcast(mediaScanIntent);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        initEasyImage();
        Intent intent;
        switch (item.getItemId()) {
            case R.id.btn_camera:
//                EasyImage.openCamera(getActivity(), 0);
                intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                if (intent.resolveActivity(getPackageManager()) != null) {
                    File photoFile = null;
                    try {
                        photoFile = createImageFile();
                        mCurrentPhotoPath = photoFile.getAbsolutePath();
                        galleryAddPic();
                    } catch (IOException ex) {
                        // Error occurred while creating the File

                    }
//                     Continue only if the File was successfully created
                    if (photoFile != null) {
                        Uri photoURI = FileProvider.getUriForFile(this,
                                "com.example.android.fileprovider",
                                photoFile);
                        intent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                        startActivityForResult(intent, REQUEST_IMAGE_CAPTURE);
                    }
                }
                break;

            case R.id.btn_map:
                intent = new Intent(this, MapsActivity.class);
                startActivity(intent);
                break;
        }
        return true;
    }

    private void initEasyImage() {
        EasyImage.configuration(this)
                .setImagesFolderName("EasyImage sample")
                .setCopyTakenPhotosToPublicGalleryAppFolder(true)
                .setCopyPickedImagesToPublicGalleryAppFolder(false)
                .setAllowMultiplePickInGallery(true);
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
        if (!permissionsOk) {
            Log.e(getClass().getName(), "Permission request failed.");
            return;
        }
        LoadImagesTask loadImagesTask = new LoadImagesTask(this);
        LoadImagesTask.LoadImagesTaskParam loadImagesTaskParam = new LoadImagesTask.LoadImagesTaskParam();
        loadImagesTaskParam.activity = this;
        loadImagesTask.execute(loadImagesTaskParam);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (grantResults.length < 1 || grantResults[0] != PackageManager.PERMISSION_GRANTED) {
            permissionsOk = false;
            System.out.println("permissions not granted");
            Toast.makeText(this, "View photo permission is required!", Toast.LENGTH_SHORT).show();
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

//        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab_camera);
//        fab.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                EasyImage.openCamera(getActivity(), 0);
//            }
//        });


        activity= this;

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

        Util.checkPermissions(this);
        ReadFromDatabaseTask readFromDatabaseTask = new ReadFromDatabaseTask(this);
        readFromDatabaseTask.execute(AppDatabase.getInstance(this).imageMetadataDao());

        Util.initEasyImage(this);
//        doLoadImages();
    }

    public Activity getActivity() {
        return activity;
    }

}
