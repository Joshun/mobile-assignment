package com4510.thebestphotogallery.Listeners;

/**
 * Describes a listener to a server's response to uploading an image
 */

public interface ServerResponseListener {
    void onServerSuccess();
    void onServerFailure();
}
