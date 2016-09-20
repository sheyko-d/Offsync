package com.offsync.postpreview;

import com.offsync.album.data.AlbumImage;
import com.offsync.album.data.AlbumImagesRepository;

import java.util.ArrayList;

/**
 * Listens to user actions from the UI ({@link PostPreviewFragment}), retrieves the data and updates
 * the UI as required.
 */
public class PostPreviewPresenter implements PostPreviewContract.UserActionsListener {

    private final AlbumImagesRepository mAlbumImagesRepository;
    private final PostPreviewContract.View mPostPreviewView;

    public PostPreviewPresenter(AlbumImagesRepository albumImagesRepository,
                                PostPreviewContract.View postPreviewView) {
        mAlbumImagesRepository = albumImagesRepository;
        mPostPreviewView = postPreviewView;
    }

    @Override
    public void loadAlbumImages(String albumId) {
        mAlbumImagesRepository.getAlbumImages(albumId, new AlbumImagesRepository
                .GetAlbumCallback() {
            @Override
            public void onImagesLoaded(ArrayList<AlbumImage> albumImages) {
                mPostPreviewView.showAlbumImages(albumImages);
            }

            @Override
            public void onError(String message) {
                mPostPreviewView.showError(message);
            }
        });
    }
}
