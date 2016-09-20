package com.offsync.subreddits.data;

public class Subreddit {

    private final String mId;
    private final String mTitle;

    public Subreddit(String id, String title) {
        mId = id;
        mTitle = title;
    }

    public String getId() {
        return mId;
    }

    public String getTitle() {
        return mTitle;
    }
}
