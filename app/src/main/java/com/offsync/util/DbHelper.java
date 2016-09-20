package com.offsync.util;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DbHelper extends SQLiteOpenHelper {

    // If you change the database schema, you must increment the database version.
    public static final int DATABASE_VERSION = 19;
    public static final String DATABASE_NAME = "Offsync.db";

    // Posts table
    public static final String POSTS_TABLE = "Posts";
    public static final String POSTS_ID_COLUMN = "Id";
    public static final String POSTS_TITLE_COLUMN = "Title";
    public static final String POSTS_SCORE_COLUMN = "Score";
    public static final String POSTS_AUTHOR_COLUMN = "Author";
    public static final String POSTS_SUBREDDIT_COLUMN = "Subreddit";
    public static final String POSTS_COMMENTS_COUNT_COLUMN = "CommentsCount";
    public static final String POSTS_DOMAIN_COLUMN = "Domain";
    public static final String POSTS_TIMESTAMP_COLUMN = "Timestamp";
    public static final String POSTS_NSFW_COLUMN = "Nsfw";
    public static final String POSTS_NSFL_COLUMN = "Nsfl";
    public static final String POSTS_SELF_COLUMN = "Self";
    public static final String POSTS_TYPE_COLUMN = "Type";
    public static final String POSTS_FLAIR_COLUMN = "Flair";
    public static final String POSTS_LINK_COLUMN = "Link";
    public static final String POSTS_TEXT_COLUMN = "Text";
    public static final String POSTS_STICKIED_COLUMN = "Stickied";
    public static final String POSTS_GILDED_COUNT_COLUMN_COLUMN = "GildedColumn";
    public static final String POSTS_FILE_STORED_PATH_COLUMN = "FileStoredPath";
    public static final String POSTS_PREVIEW_STORED_PATH_COLUMN = "PreviewStoredPath";
    public static final String POSTS_PREVIEW_HEIGHT_COLUMN = "PreviewHeight";
    public static final String POSTS_PREVIEW_WIDTH_COLUMN = "PreviewWidth";
    public static final String POSTS_UPVOTED_COLUMN = "Upvoted";
    public static final String POSTS_DOWNVOTED_COLUMN = "Downvoted";

    public static final Integer POSTS_ID_INDEX = 0;
    public static final Integer POSTS_TITLE_INDEX = 1;
    public static final Integer POSTS_SCORE_INDEX = 2;
    public static final Integer POSTS_AUTHOR_INDEX = 3;
    public static final Integer POSTS_SUBREDDIT_INDEX = 4;
    public static final Integer POSTS_COMMENTS_COUNT_INDEX = 5;
    public static final Integer POSTS_DOMAIN_INDEX = 6;
    public static final Integer POSTS_TIMESTAMP_INDEX = 7;
    public static final Integer POSTS_NSFW_INDEX = 8;
    public static final Integer POSTS_NSFL_INDEX = 9;
    public static final Integer POSTS_SELF_INDEX = 10;
    public static final Integer POSTS_TYPE_INDEX = 11;
    public static final Integer POSTS_FLAIR_INDEX = 12;
    public static final Integer POSTS_LINK_INDEX = 13;
    public static final Integer POSTS_TEXT_INDEX = 14;
    public static final Integer POSTS_STICKIED_INDEX = 15;
    public static final Integer POSTS_GILDED_COUNT_INDEX = 16;
    public static final Integer POSTS_FILE_STORED_PATH_INDEX = 17;
    public static final Integer POSTS_PREVIEW_STORED_PATH_INDEX = 18;
    public static final Integer POSTS_PREVIEW_HEIGHT_INDEX = 19;
    public static final Integer POSTS_PREVIEW_WIDTH_INDEX = 20;
    public static final Integer POSTS_UPVOTED_INDEX = 21;
    public static final Integer POSTS_DOWNVOTED_INDEX = 22;

    // Comments table
    public static final String COMMENTS_TABLE = "Comments";
    public static final String COMMENTS_ID_COLUMN = "Id";
    public static final String COMMENTS_POST_ID_COLUMN = "PostId";
    public static final String COMMENTS_DEPTH_COLUMN = "Depth";
    public static final String COMMENTS_BODY_COLUMN = "Body";
    public static final String COMMENTS_SCORE_COLUMN = "Score";
    public static final String COMMENTS_AUTHOR_COLUMN = "Author";
    public static final String COMMENTS_STICKIED_COLUMN = "Stickied";
    public static final String COMMENTS_DISTINGUISHED_COLUMN = "Distinguished";
    public static final String COMMENTS_MORE_COUNT_COLUMN = "MoreCount";
    public static final String COMMENTS_TYPE_COLUMN = "Type";
    public static final String COMMENTS_TIMESTAMP_COLUMN = "Timestamp";
    public static final String COMMENTS_SCORE_HIDDEN_COLUMN = "ScoreHidden";
    public static final String COMMENTS_GILDED_COUNT_COLUMN = "GildedCount";

    public static final Integer COMMENTS_ID_INDEX = 0;
    public static final Integer COMMENTS_POST_ID_INDEX = 1;
    public static final Integer COMMENTS_DEPTH_INDEX = 2;
    public static final Integer COMMENTS_BODY_INDEX = 3;
    public static final Integer COMMENTS_SCORE_INDEX = 4;
    public static final Integer COMMENTS_AUTHOR_INDEX = 5;
    public static final Integer COMMENTS_STICKIED_INDEX = 6;
    public static final Integer COMMENTS_DISTINGUISHED_INDEX = 7;
    public static final Integer COMMENTS_MORE_COUNT_INDEX = 8;
    public static final Integer COMMENTS_TYPE_INDEX = 9;
    public static final Integer COMMENTS_TIMESTAMP_INDEX = 10;
    public static final Integer COMMENTS_SCORE_HIDDEN_INDEX = 11;
    public static final Integer COMMENTS_GILDED_COUNT_INDEX = 12;

    private static final String SQL_CREATE_POSTS_TABLE = "CREATE TABLE "
            + POSTS_TABLE + " ("
            + POSTS_ID_COLUMN + " TEXT PRIMARY KEY, "
            + POSTS_TITLE_COLUMN + " TEXT, "
            + POSTS_SCORE_COLUMN + " INTEGER, "
            + POSTS_AUTHOR_COLUMN + " TEXT, "
            + POSTS_SUBREDDIT_COLUMN + " TEXT, "
            + POSTS_COMMENTS_COUNT_COLUMN + " INTEGER, "
            + POSTS_DOMAIN_COLUMN + " TEXT, "
            + POSTS_TIMESTAMP_COLUMN + " INTEGER, "
            + POSTS_NSFW_COLUMN + " INTEGER, "
            + POSTS_NSFL_COLUMN + " INTEGER, "
            + POSTS_SELF_COLUMN + " INTEGER, "
            + POSTS_TYPE_COLUMN + " INTEGER, "
            + POSTS_FLAIR_COLUMN + " TEXT, "
            + POSTS_LINK_COLUMN + " TEXT, "
            + POSTS_TEXT_COLUMN + " TEXT, "
            + POSTS_STICKIED_COLUMN + " INTEGER, "
            + POSTS_GILDED_COUNT_COLUMN_COLUMN + " INTEGER, "
            + POSTS_FILE_STORED_PATH_COLUMN + " TEXT, "
            + POSTS_PREVIEW_STORED_PATH_COLUMN + " TEXT, "
            + POSTS_PREVIEW_HEIGHT_COLUMN + " INTEGER, "
            + POSTS_PREVIEW_WIDTH_COLUMN + " INTEGER, "
            + POSTS_UPVOTED_COLUMN + " INTEGER, "
            + POSTS_DOWNVOTED_COLUMN + " INTEGER)";

    private static final String SQL_CREATE_COMMENTS_TABLE = "CREATE TABLE "
            + COMMENTS_TABLE + " ("
            + COMMENTS_ID_COLUMN + " TEXT PRIMARY KEY, "
            + COMMENTS_POST_ID_COLUMN + " TEXT, "
            + COMMENTS_DEPTH_COLUMN + " INTEGER, "
            + COMMENTS_BODY_COLUMN + " TEXT, "
            + COMMENTS_SCORE_COLUMN + " INTEGER, "
            + COMMENTS_AUTHOR_COLUMN + " TEXT, "
            + COMMENTS_STICKIED_COLUMN + " INTEGER, "
            + COMMENTS_DISTINGUISHED_COLUMN + " TEXT, "
            + COMMENTS_MORE_COUNT_COLUMN + " INTEGER, "
            + COMMENTS_TYPE_COLUMN + " INTEGER, "
            + COMMENTS_TIMESTAMP_COLUMN + " INTEGER, "
            + COMMENTS_SCORE_HIDDEN_COLUMN + " INTEGER, "
            + COMMENTS_GILDED_COUNT_COLUMN + " INTEGER)";

    private static final String SQL_DELETE_POSTS_TABLE = "DROP TABLE IF EXISTS " + POSTS_TABLE;
    private static final String SQL_DELETE_COMMENTS_TABLE = "DROP TABLE IF EXISTS "
            + COMMENTS_TABLE;

    public DbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    public void onCreate(SQLiteDatabase db) {
        db.execSQL(SQL_CREATE_POSTS_TABLE);
        db.execSQL(SQL_CREATE_COMMENTS_TABLE);
    }

    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // This database is only a cache for online data, so its upgrade policy is
        // to simply to discard the data and start over
        db.execSQL(SQL_DELETE_POSTS_TABLE);
        db.execSQL(SQL_DELETE_COMMENTS_TABLE);
        onCreate(db);
    }

    public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        onUpgrade(db, oldVersion, newVersion);
    }
}