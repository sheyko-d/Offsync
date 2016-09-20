package com.offsync.postpreview.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.offsync.album.AlbumFragment;
import com.offsync.album.data.AlbumImage;

import java.util.ArrayList;

public class AlbumPagerAdapter extends FragmentPagerAdapter {

    private final ArrayList<AlbumImage> mAlbumImages;

    public AlbumPagerAdapter(FragmentManager fragmentManager, ArrayList<AlbumImage> albumImages) {
        super(fragmentManager);
        mAlbumImages = albumImages;
    }

    // Returns total number of pages
    @Override
    public int getCount() {
        return mAlbumImages.size();
    }

    // Returns the fragment to display for that page
    @Override
    public Fragment getItem(int position) {
        AlbumImage image = mAlbumImages.get(position);
        return AlbumFragment.newInstance(image.getTitle(), image.getDescription(), image.getLink(),
                image.getHeight(), image.getWidth());
    }
}
