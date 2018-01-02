package com4510.thebestphotogallery.Database;

import java.util.List;

/**
 * Created by joshua on 28/12/17.
 */

public interface DatabaseResponseListener {
    void onDatabaseRead(List<ImageMetadata> imageMetadataList);
}
