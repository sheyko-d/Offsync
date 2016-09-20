package com.offsync.album.data;

public class AlbumImage {

    private String mTitle;
    private String mDescription;
    private String mLink;
    private int mHeight;
    private int mWidth;

    public AlbumImage(String title, String description, String link, int height, int width) {
        mTitle = title;
        mDescription = description;
        mLink = link;
        mHeight = height;
        mWidth = width;
    }

    public String getTitle() {
        return mTitle;
    }

    public String getDescription() {
        return mDescription;
    }

    public String getLink() {
        return mLink;
    }

    public int getHeight() {
        return mHeight;
    }

    public int getWidth() {
        return mWidth;
    }
}
