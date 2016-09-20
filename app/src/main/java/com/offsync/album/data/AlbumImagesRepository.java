package com.offsync.album.data;

import java.util.ArrayList;

/**
 * Main entry point for accessing albums data.
 */
public interface AlbumImagesRepository {

    interface GetAlbumCallback {

        void onImagesLoaded(ArrayList<AlbumImage> albumImages);

        void onError(String message);
    }

    void getAlbumImages(String albumId, GetAlbumCallback callback);

    void refreshData();
}
