package com4510.thebestphotogallery;

import android.app.Activity;
import android.content.ContentUris;
import android.database.Cursor;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;

import java.io.File;
import java.util.ArrayList;

/**
 * Created by joshua on 23/11/17.
 */

public class ImageLoader {
    public static ArrayList<String> loadImages(Activity activity) {
//        String externalStorageDirP = Environment.getExternalStorageDirectory().getAbsolutePath();
//        File externalStorageDirF = new File(externalStorageDirP);
//        System.out.println(externalStorageDirP);
//        File[] files = externalStorageDirF.listFiles();
//        for (File f: files) {
//            System.out.println(f.getAbsolutePath());
//        }

        ArrayList<Integer> imgIds = new ArrayList<>();
//        Cursor cursor = activity.getContentResolver().query(
//                MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
//                new String[]{},
//                null,
//                null);
        Cursor cursor = activity.getContentResolver().query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, new String[]{}, "", new String[]{}, "");
        ArrayList<String> imgPaths = new ArrayList<>();
        cursor.moveToFirst();

        byte temp = 0;

        while (!cursor.isAfterLast()) {
            imgIds.add(cursor.getInt(cursor.getColumnIndex(MediaStore.Images.Media._ID)));
//            Uri imageUri = ContentUris.withAppendedId(
//                    MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
//                    cursor.getInt(cursor.getColumnIndex(MediaStore.Images.Media._ID))
//            );
//            imgPaths.add(imageUri);
            imgPaths.add(cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.DATA)));
//            cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.EXTERNAL_CONTENT_URI))
//                System.out.println(cursor.getInt(cursor.getColumnIndex(MediaStore.Images.Media._ID)));
            cursor.moveToNext();
            ++temp;
            if (temp > 12)
            {
                break;
            }
        }
        cursor.close();
        System.out.println(imgIds);
//        return imgIds;
        return imgPaths;
    }
}
