package com4510.thebestphotogallery.Database;

import android.arch.persistence.room.TypeConverter;

import java.util.Date;

/**
 * Used to convert Date objects into a format the database can store
 */

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