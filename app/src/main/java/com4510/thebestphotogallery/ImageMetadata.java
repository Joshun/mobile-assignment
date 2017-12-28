package com4510.thebestphotogallery;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;

/**
 * Created by joshua on 27/12/17.
 */
@Entity(tableName = "image_metadata")
public class ImageMetadata {
    @PrimaryKey
    private int uid;

    public int getUid() {
        return uid;
    }

    public void setUid(int uid) {
        this.uid = uid;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    @ColumnInfo(name="title")
    private String title;

    @ColumnInfo(name="description")
    private String description;
}
