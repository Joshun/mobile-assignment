package com4510.thebestphotogallery.Activities;

import android.Manifest;
import android.app.Activity;
import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.content.ContextCompat;
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

import java.io.FileNotFoundException;
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
import com4510.thebestphotogallery.OnBottomReachedListener;
import com4510.thebestphotogallery.Tasks.LoadImagesTask;
import com4510.thebestphotogallery.MyAdapter;
import com4510.thebestphotogallery.R;
import com4510.thebestphotogallery.Tasks.ReadFromDatabaseTask;
import com4510.thebestphotogallery.Util;
import pl.aprilapps.easyphotopicker.EasyImage;

import static android.support.v7.widget.RecyclerView.SCROLL_STATE_DRAGGING;

public class MainActivity extends ImageLoadActivity {
    private boolean firstLoad;
    private boolean queueRefresh;
    private View mainView;
    private View loadingView;
    private MyAdapter recyclerViewAdapter;
    private SwipeRefreshLayout swipeContainer;
    static final int REQUEST_IMAGE_CAPTURE = 1;
    private Activity activity;

    private boolean permissionsOk = true;

    private String mCurrentPhotoPath = null;

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            if (mCurrentPhotoPath != null) {
//                galleryAddPic(mCurrentPhotoPath);
                notifyMediaStoreScanner(new File(mCurrentPhotoPath));
                Log.v(getClass().getName(), "IMAGE SAVE SUCCESS");
            }
        }

    }

    @Override
    public void onFinishedBitmapLoad(List<Bitmap> bitmaps) {
        super.onFinishedBitmapLoad(bitmaps);
        if (queueRefresh && this.bitmaps.loaded()) {
            refresh();
        }
        else if (firstLoad) {
            firstLoad = false;
            mainView.setAlpha(0.0f);
            mainView.setVisibility(View.VISIBLE);

            Toolbar toolbar = findViewById(R.id.main_toolbar);
            setSupportActionBar(toolbar);

            RecyclerView recyclerView = findViewById(R.id.grid_recycler_view);
            int numberOfColumns = 4;
            final GridLayoutManager layoutManager = new GridLayoutManager(this, numberOfColumns);
            recyclerView.setLayoutManager(layoutManager);
            recyclerViewAdapter = new MyAdapter(imageMetadataList, bitmaps);
            recyclerViewAdapter.setOnBottomReachedListener(new OnBottomReachedListener() {
                @Override
                public void onBottomReached(int position) {
                    if (!queueRefresh) {
                        if (atSoftCap()) {
                            incSoftCap();
                            dispatchBitmapLoad(8);
                        }
                        else {
                            incSoftCap();
                        }
                        Log.v("RecyclerView", "Hit the bottom!");

                    }
                }
            });
            recyclerView.setAdapter(recyclerViewAdapter);

            swipeContainer = findViewById(R.id.swipe_refresh_layout);
            swipeContainer.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
                @Override
                public void onRefresh() {
                    System.out.println("refreshing...");
                    if (!getLoading()) {
                        refresh();
                    }
                    else {
                        queueRefresh = true;
                    }
                }
            });

            final int animDuration = getResources().getInteger(android.R.integer.config_shortAnimTime);
            mainView.animate().alpha(1.0f).setDuration(animDuration).setListener(null);
            loadingView.animate().alpha(0.0f).setDuration(animDuration).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    super.onAnimationEnd(animation);
                    loadingView.setVisibility(View.GONE);
                }
            });
        }
        else {
            swipeContainer.setRefreshing(false);
            if (recyclerViewAdapter.isEmpty()) {
                recyclerViewAdapter.setItems(imageMetadataList);
            }
            recyclerViewAdapter.addBitmaps(bitmaps);
        }

        if (!atSoftCap() && moreToLoad() && !queueRefresh) {
            dispatchBitmapLoad(8);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_view, menu);
        return true;
    }

    private File createImageFile() throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";

        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
//        File storageDir = new File(MediaStore.Images.Media.EXTERNAL_CONTENT_URI.toString());

//        File storageDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM);

//        File image = new File(storageDir.getAbsolutePath() + "/" + imageFileName + ".jpg");


        File image = File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",         /* suffix */
                storageDir      /* directory */
        );


        return image;
    }

    public final void notifyMediaStoreScanner(final File file) {
        // this tries to save to sdcard
        if (file == null) {
            return;
        }
        try {
            MediaStore.Images.Media.insertImage(this.getContentResolver(),
                    file.getAbsolutePath(), file.getName(), null);
            this.sendBroadcast(new Intent(
                    Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, Uri.fromFile(file)));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    private void galleryAddPic(String photoPath) {
        // this method doesn't actually save the photo to sdcard but notifies gallery
        File f = new File(mCurrentPhotoPath);
        System.out.println(f);
        Uri contentUri = Uri.fromFile(f);
        System.out.println(contentUri);
        Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
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
                intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                if (intent.resolveActivity(getPackageManager()) != null) {
                    File photoFile = null;
                    try {
                        photoFile = createImageFile();
                        mCurrentPhotoPath = photoFile.getAbsolutePath();
                    } catch (IOException ex) {
                        // Error occurred while creating the File
                        System.err.println("err creating file");
                        ex.printStackTrace(System.err);

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
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        firstLoad = true;
        queueRefresh = false;
        setContentView(R.layout.activity_main);

        mainView = findViewById(R.id.main_view);
        loadingView = findViewById(R.id.loading_view);
        mainView.setVisibility(View.GONE);

        ReadFromDatabaseTask readFromDatabaseTask = new ReadFromDatabaseTask(this);
        readFromDatabaseTask.execute(AppDatabase.getInstance(this).imageMetadataDao());


        // try to load images (will fail silently if permission not already granted)
        doLoadImages();
        // request permissions if they haven't already been granted
        Util.requestPermissionsIfNecessary(this);

    }

    public Activity getActivity() {
        return activity;
    }

    public void refresh() {
        queueRefresh = false;
        recyclerViewAdapter.clear();
        super.refresh();
    }

}
