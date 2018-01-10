package com4510.thebestphotogallery;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import com4510.thebestphotogallery.Database.ImageMetadata;

/**
 * Created by joshua on 08/01/18.
 */

public class ImageMetadataList implements Serializable{

    private ArrayList<ImageMetadata> imageMetadataArrayList;

    public ImageMetadataList() {
        this.imageMetadataArrayList = new ArrayList<>();
    }

    public void add(ImageMetadata im) {
        imageMetadataArrayList.add(im);
    }

    public void addAll(Collection<? extends ImageMetadata> c) {
        imageMetadataArrayList.addAll(c);
    }

    public void clear() {
        imageMetadataArrayList.clear();
    }

    public ImageMetadata get(int index) {
        return imageMetadataArrayList.get(index);
    }

    public void set(int index, ImageMetadata imageMetadata) {
        imageMetadataArrayList.set(index, imageMetadata);
    }

    public ArrayList<ImageMetadata> getList() {
        return imageMetadataArrayList;
    }

}
