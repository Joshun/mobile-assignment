package com4510.thebestphotogallery.Database;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.Ignore;
import android.arch.persistence.room.Index;
import android.arch.persistence.room.PrimaryKey;

import java.io.File;
import java.io.Serializable;
import java.util.Date;

/**
 * Object to represent image metadata (name, description, exif, filepath)
 * Mapped to sqlite database using Android Room library
 */

// filePath is unique - if an image is already in the database, we leave it as it is
@Entity(tableName = "image_metadata", indices = {@Index(value="filePath", unique = true)})
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

    public void setAltitude(double altitude) {
        this.altitude = altitude;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }

    public void setDateAdded(Date dateAdded) {
        this.dateAdded = dateAdded;
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

    public double getAltitude() {
        return altitude;
    }


    public Date getDateAdded() {
        return dateAdded;
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

    @ColumnInfo(name="altitude")
    private double altitude;

    @ColumnInfo(name="filePath")
    private String filePath;

    @ColumnInfo(name="dateAdded")
    private Date dateAdded;

    @Ignore
    public File file;
}
