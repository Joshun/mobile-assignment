package com4510.thebestphotogallery;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;

import java.util.List;

/**
 * Created by joshua on 27/12/17.
 */

@Dao
public interface ImageMetadataDao {
    @Query("SELECT * FROM image_metadata")
    List<ImageMetadata> getAll();

    @Insert
    void insertAll(ImageMetadata... imageMetadatas);

    @Insert
    void insert(ImageMetadata imageMetadata);

    @Delete
    void delete(ImageMetadata imageMetadata);
}