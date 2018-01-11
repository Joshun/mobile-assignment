package com4510.thebestphotogallery.Images;

import android.app.Activity;
import android.content.ContentUris;
import android.database.Cursor;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;

import java.io.File;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

/**
 * Created by joshua on 23/11/17.
 */

public class ImageLoader {


    public static ArrayList<String> loadImages(Activity activity) {

         Cursor cursor = activity.getContentResolver().query(
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                new String[]{},
                "",
                new String[]{},
                "");

        ArrayList<String> imgPaths = new ArrayList<>();

        cursor.moveToFirst();

        while (!cursor.isAfterLast()) {
            imgPaths.add(cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.DATA)));
            cursor.moveToNext();
        }
        cursor.close();
        return imgPaths;
    }
}
