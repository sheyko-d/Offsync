package com.offsync.postpreview;


import com.offsync.album.data.AlbumImage;

import java.util.ArrayList;

public interface PostPreviewContract {

    interface View {

        void showAlbumImages(ArrayList<AlbumImage> albumImages);

        void showError(String message);
    }

    interface UserActionsListener {

        void loadAlbumImages(String albumId);
    }
}