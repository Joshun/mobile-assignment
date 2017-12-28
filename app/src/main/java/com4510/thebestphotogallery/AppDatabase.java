package com4510.thebestphotogallery;

import android.arch.persistence.room.*;
import android.content.Context;

/**
 * Created by joshua on 27/12/17.
 */

@android.arch.persistence.room.Database(entities = {ImageMetadata.class}, version = 1)
public abstract class AppDatabase extends RoomDatabase {
    private static AppDatabase INSTANCE;

    public abstract ImageMetadataDao imageMetadataDao();

    public static AppDatabase getInstance(Context applicationContext) {
        if (INSTANCE == null) {
            INSTANCE = Room.databaseBuilder(applicationContext, AppDatabase.class, "photogallery-db").build();
        }
        return INSTANCE;
    }
}