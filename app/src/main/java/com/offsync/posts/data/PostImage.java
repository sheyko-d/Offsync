package com.offsync.posts.data;

public class PostImage {

    private final String mUrl;
    private final int mWidth;
    private final int mHeight;

    public PostImage(String url, int width, int height) {
        mUrl = url;
        mWidth = width;
        mHeight = height;
    }

    public String getUrl() {
        return mUrl;
    }

    public int getWidth() {
        return mWidth;
    }

    public int getHeight() {
        return mHeight;
    }
}
