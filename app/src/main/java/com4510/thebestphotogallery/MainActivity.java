package com4510.thebestphotogallery;

import android.content.pm.PackageManager;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.widget.Toast;

import java.io.File;

import pl.aprilapps.easyphotopicker.EasyImage;


public class MainActivity extends AppCompatActivity {
    private RecyclerView recyclerView;

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (grantResults.length < 1 || grantResults[0] != PackageManager.PERMISSION_GRANTED) {
                System.out.println("permissions not granted");
        }
        else {
            String externalStorageDirP = Environment.getExternalStorageDirectory().getAbsolutePath();
            File externalStorageDirF = new File(externalStorageDirP);
            System.out.println(externalStorageDirP);
            File[] files = externalStorageDirF.listFiles();
            for (File f: files) {
                System.out.println(f.getAbsolutePath());
            }
        }

    }



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        recyclerView = findViewById(R.id.grid_recycler_view);

        Util.checkPermissions(getApplicationContext(), this);
        Util.initEasyImage(this);


    }




}
