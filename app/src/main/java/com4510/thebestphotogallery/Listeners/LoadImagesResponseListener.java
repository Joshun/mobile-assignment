package com4510.thebestphotogallery.Listeners;

import java.util.List;

import com4510.thebestphotogallery.Database.ImageMetadata;

/**
 * Created by joshua on 02/01/18.
 */

public interface LoadImagesResponseListener {
    void imagesLoaded(List<ImageMetadata> imageMetadataList);
}
