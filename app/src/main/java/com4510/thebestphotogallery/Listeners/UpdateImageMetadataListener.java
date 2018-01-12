package com4510.thebestphotogallery.Listeners;

import com4510.thebestphotogallery.Database.ImageMetadata;

/**
 * Describes a listener to database updating image metadata
 */

public interface UpdateImageMetadataListener {
    void imageUpdated(ImageMetadata imageMetadata);
}
