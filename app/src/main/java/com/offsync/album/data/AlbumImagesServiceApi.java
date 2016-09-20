package com.offsync.album.data;

import java.util.ArrayList;

/**
 * Defines an interface to the service API that is used by this application. All data request should
 * be piped through this interface.
 */
public interface AlbumImagesServiceApi {

    interface GetAlbumServiceCallback {

        void onImagesLoaded(ArrayList<AlbumImage> albumImages);

        void onError(String message);
    }

    void getAlbumImages(String albumId, GetAlbumServiceCallback callback);
}
