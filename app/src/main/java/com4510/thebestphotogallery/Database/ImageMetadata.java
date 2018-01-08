package com4510.thebestphotogallery.Database;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.Ignore;
import android.arch.persistence.room.Index;
import android.arch.persistence.room.PrimaryKey;

import java.io.File;
import java.io.Serializable;

/**
 * Created by joshua on 27/12/17.
 */
@Entity(tableName = "image_metadata", indices = {@Index(value="filePath", unique = true)})
//@Entity(tableName = "image_metadata")
public class ImageMetadata implements Serializable {
    @PrimaryKey(autoGenerate = true)
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

    public void setWidth(int width) { this.width = width; }

    public void setHeight(int height) { this.height = height; }

    public void setFileSize(int fileSize) { this.fileSize = fileSize; }

    public void setLongitude(double longitude) { this.longitude = longitude; }

    public void setLatitude(double latitude) { this.latitude = latitude; }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }

    public final String getTitle() {
        return title;
    }

    public final String getDescription() {
        return description;
    }

    public final int getWidth() { return width; }

    public final int getHeight() { return height; }

    public final int getFileSize() { return fileSize; }

    public final double getLongitude() { return longitude; }

    public final double getLatitude() { return latitude; }

    public String getFilePath() {
        return filePath;
    }

    @ColumnInfo(name="title")
    private String title;

    @ColumnInfo(name="description")
    private String description;

    @ColumnInfo(name="width")
    private int width;

    @ColumnInfo(name="height")
    private int height;

    @ColumnInfo(name="fileSize")
    private int fileSize;

    @ColumnInfo(name="longitude")
    private double longitude;

    @ColumnInfo(name="latitude")
    private double latitude;

    @ColumnInfo(name="filePath")
    private String filePath;

    @Ignore
    public File file;
}
