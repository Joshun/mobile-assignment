package com4510.thebestphotogallery.Database;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Update;

import java.util.Date;
import java.util.List;

/**
 * Dao (Database Access Object) interface for ImageMetadata
 * Maps queries to functions using Room persistence library
 */

@Dao
public interface ImageMetadataDao {
    @Query("SELECT * FROM image_metadata")
    List<ImageMetadata> getAll();

    @Query("SELECT * FROM image_metadata WHERE dateAdded >= :start AND dateAdded < :end")
    List<ImageMetadata> getByDates(Date start, Date end);

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    void insertAll(ImageMetadata... imageMetadatas);

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    void insert(ImageMetadata imageMetadata);

    @Update
    void update(ImageMetadata imageMetadata);

    @Delete
    void delete(ImageMetadata imageMetadata);
}
