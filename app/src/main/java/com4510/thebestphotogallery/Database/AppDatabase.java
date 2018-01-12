package com4510.thebestphotogallery.Database;

import android.arch.persistence.room.*;
import android.content.Context;

import java.util.Date;

import com4510.thebestphotogallery.ImageMetadataList;

/**
 * Singleton database connection class
 */

@android.arch.persistence.room.Database(entities = {ImageMetadata.class}, version = 5)
@TypeConverters(DateTypeConverter.class)
public abstract class AppDatabase extends RoomDatabase {
    private static AppDatabase INSTANCE;

    public abstract ImageMetadataDao imageMetadataDao();

    public static AppDatabase getInstance(Context applicationContext) {
        if (INSTANCE == null) {
            INSTANCE = Room.databaseBuilder(applicationContext, AppDatabase.class, "photogallery-db").fallbackToDestructiveMigration().build();
        }
        return INSTANCE;
    }
}
