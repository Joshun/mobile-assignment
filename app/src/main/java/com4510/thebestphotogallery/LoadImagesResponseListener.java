package com4510.thebestphotogallery;

import java.util.List;

/**
 * Created by joshua on 02/01/18.
 */

public interface LoadImagesResponseListener {
    void imagesLoaded(List<ImageMetadata> imageMetadataList);
}
