package com4510.thebestphotogallery;

import android.app.Activity;
import android.database.Cursor;
import android.os.Environment;
import android.provider.MediaStore;

import java.io.File;
import java.util.ArrayList;

/**
 * Created by joshua on 23/11/17.
 */

public class ImageLoader {
    public static ArrayList<Integer> loadImages(Activity activity) {
//        String externalStorageDirP = Environment.getExternalStorageDirectory().getAbsolutePath();
//        File externalStorageDirF = new File(externalStorageDirP);
//        System.out.println(externalStorageDirP);
//        File[] files = externalStorageDirF.listFiles();
//        for (File f: files) {
//            System.out.println(f.getAbsolutePath());
//        }

        ArrayList<Integer> imgIds = new ArrayList<>();
        Cursor cursor = activity.getContentResolver().query(
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                new String[]{},
                null,
                null);
        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            imgIds.add(cursor.getInt(cursor.getColumnIndex(MediaStore.Images.Media._ID)));
//                System.out.println(cursor.getInt(cursor.getColumnIndex(MediaStore.Images.Media._ID)));
            cursor.moveToNext();
        }
        cursor.close();
        System.out.println(imgIds);
        return imgIds;
    }
}
