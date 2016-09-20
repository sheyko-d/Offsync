package com.offsync.posts.data;

import java.util.ArrayList;

public class Post {

    public final static int SAVED_STATUS_DEFAULT = 0;
    public final static int SAVED_STATUS_PENDING = 1;
    public final static int SAVED_STATUS_DOWNLOADED = 2;
    public final static int SAVED_STATUS_FAILED = 3;

    public final static int TYPE_TEXT = 0;
    public final static int TYPE_PHOTO = 1;
    public final static int TYPE_GALLERY = 2;
    public final static int TYPE_GIF = 3;
    public final static int TYPE_YOUTUBE = 4;
    public final static int TYPE_WEBSITE = 5;

    private final String mId;
    private final String mTitle;
    private int mScore;
    private final String mAuthor;
    private final String mSubreddit;
    private final int mCommentsCount;
    private final String mDomain;
    private final Long mTimestamp;
    private final ArrayList<PostImage> mImages;
    private final boolean mNsfw;
    private final boolean mSelf;
    private final int mType;
    private final String mFlair;
    private final String mLink;
    private final String mText;
    private final boolean mStickied;
    private int mSavedStatus;
    private boolean mNsfl;
    private int mGildedCount;
    private boolean mUpvoted;
    private boolean mDownvoted;

    public Post(String id, String title, int score, String author, String subreddit,
                int commentsCount, String domain, Long timestamp, ArrayList<PostImage> images,
                boolean nsfw, boolean self, int type, String flair, String link, String text,
                boolean stickied, int savedStatus, boolean nsfl, int gildedCount, boolean upvoted,
                boolean downvoted) {
        mId = id;
        mTitle = title;
        mScore = score;
        mAuthor = author;
        mSubreddit = subreddit;
        mCommentsCount = commentsCount;
        mDomain = domain;
        mTimestamp = timestamp;
        mImages = images;
        mNsfw = nsfw;
        mSelf = self;
        mType = type;
        mFlair = flair;
        mLink = link;
        mText = text;
        mStickied = stickied;
        mSavedStatus = savedStatus;
        mNsfl = nsfl;
        mGildedCount = gildedCount;
        mUpvoted = upvoted;
        mDownvoted = downvoted;
    }

    public String getId() {
        return mId;
    }

    public String getTitle() {
        return mTitle;
    }

    public int getScore() {
        return mScore;
    }

    public void setScore(int score){
        mScore = score;
    }

    public String getAuthor() {
        return mAuthor;
    }

    public String getSubreddit() {
        return mSubreddit;
    }

    public int getCommentsCount() {
        return mCommentsCount;
    }

    public String getDomain() {
        return mDomain;
    }

    public Long getTimestamp() {
        return mTimestamp;
    }

    public ArrayList<PostImage> getImages() {
        return mImages;
    }

    public boolean isNsfw() {
        return mNsfw;
    }

    public boolean isSelf() {
        return mSelf;
    }

    public int getType() {
        return mType;
    }

    public String getFlair() {
        return mFlair;
    }

    public String getLink() {
        return mLink;
    }

    public String getText() {
        return mText;
    }

    public boolean isStickied() {
        return mStickied;
    }

    public int getDownloadedStatus() {
        return mSavedStatus;
    }

    public void setSavedStatus(int savedStatus) {
        mSavedStatus = savedStatus;
    }

    public boolean isNsfl() {
        return mNsfl;
    }

    public int getGildedCount() {
        return mGildedCount;
    }

    public boolean isUpvoted() {
        return mUpvoted;
    }

    public void setUpvoted(boolean upvoted) {
        mUpvoted = upvoted;
    }

    public boolean isDownvoted() {
        return mDownvoted;
    }

    public void setDownvoted(boolean downvoted) {
        mDownvoted = downvoted;
    }
}
