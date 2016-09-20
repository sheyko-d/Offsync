package com.offsync.comments.data;

public class Comment {

    public final static int TYPE_REPLY = 0;
    public final static int TYPE_MORE = 1;

    private int mDepth;
    private String mId;
    private String mBody;
    private int mScore;
    private String mAuthor;
    private Boolean mStickied;
    private String mDistinguished;
    private int mMoreCount;
    private int mType;
    private long mTimestamp;
    private boolean mScoreHidden;
    private int mGildedCount;

    public Comment(int depth, String id, String body, int score, String author,
                   Boolean stickied, String distinguished, long timestamp, boolean scoreHidden,
                   int gildedCount) {
        mType = TYPE_REPLY;
        mDepth = depth;
        mId = id;
        mBody = body;
        mScore = score;
        mAuthor = author;
        mStickied = stickied;
        mDistinguished = distinguished;
        mTimestamp = timestamp;
        mScoreHidden = scoreHidden;
        mGildedCount = gildedCount;
    }

    public Comment(int depth, int moreCount) {
        mType = TYPE_MORE;
        mDepth = depth;
        mMoreCount = moreCount;
    }

    public Integer getDepth() {
        return mDepth;
    }

    public String getId() {
        return mId;
    }

    public String getBody() {
        return mBody;
    }

    public Integer getScore() {
        return mScore;
    }

    public String getAuthor() {
        return mAuthor;
    }

    public Boolean isStickied() {
        return mStickied;
    }

    public String getDistinguished() {
        return mDistinguished;
    }

    public int getMoreCount() {
        return mMoreCount;
    }

    public int getType() {
        return mType;
    }

    public long getTimestamp() {
        return mTimestamp;
    }

    public boolean isScoreHidden() {
        return mScoreHidden;
    }

    public Integer getGildedCount() {
        return mGildedCount;
    }
}
