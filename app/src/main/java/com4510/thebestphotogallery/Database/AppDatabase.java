package com4510.thebestphotogallery.Database;

import android.arch.persistence.room.*;
import android.content.Context;

import java.util.Date;

import com4510.thebestphotogallery.ImageMetadataList;

/**
 * Created by joshua on 27/12/17.
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

class DateTypeConverter {
    @TypeConverter
    public static Long fromDate(Date date) {
        if (date==null) {
            return(null);
        }

        return(date.getTime());
    }

    @TypeConverter
    public static Date toDate(Long millisSinceEpoch) {
        if (millisSinceEpoch==null) {
            return(null);
        }

        return(new Date(millisSinceEpoch));
    }
}