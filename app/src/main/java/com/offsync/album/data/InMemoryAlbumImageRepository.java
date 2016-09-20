package com.offsync.album.data;

import android.support.annotation.VisibleForTesting;
import android.text.TextUtils;

import java.util.ArrayList;

/**
 * Concrete implementation to load album images from the a data source.
 */
public class InMemoryAlbumImageRepository implements AlbumImagesRepository {

    private final AlbumImagesServiceApi mAlbumImagesServiceApi;

    /**
     * This method has reduced visibility for testing and is only visible to tests in the same
     * package.
     */
    @VisibleForTesting
    ArrayList<AlbumImage> mCachedAlbumImages;
    @VisibleForTesting
    String mCachedAlbumId;

    public InMemoryAlbumImageRepository(AlbumImagesServiceApi albumImagesServiceApi) {
        mAlbumImagesServiceApi = albumImagesServiceApi;
    }

    @Override
    public void getAlbumImages(final String albumId, final GetAlbumCallback callback) {
        // Load from API only if needed.
        if (TextUtils.isEmpty(mCachedAlbumId) || !mCachedAlbumId.equals(albumId)
                || mCachedAlbumImages == null) {

            mAlbumImagesServiceApi.getAlbumImages(albumId, new AlbumImagesServiceApi
                    .GetAlbumServiceCallback() {

                @Override
                public void onImagesLoaded(ArrayList<AlbumImage> albumImages) {
                    mCachedAlbumImages = albumImages;

                    callback.onImagesLoaded(albumImages);
                }

                @Override
                public void onError(String message) {
                    callback.onError(message);
                }
            });
        } else {
            callback.onImagesLoaded(mCachedAlbumImages);
        }
    }

    @Override
    public void refreshData() {
        mCachedAlbumImages = null;
    }
}
