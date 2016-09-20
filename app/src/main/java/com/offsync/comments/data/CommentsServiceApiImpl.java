package com.offsync.comments.data;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.support.v4.os.AsyncTaskCompat;
import android.util.Log;

import com.offsync.posts.data.Post;
import com.offsync.posts.data.PostsServiceApiImpl;
import com.offsync.util.DbHelper;
import com.offsync.util.MyApplication;
import com.offsync.util.Util;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * Implementation of the Comments Service API.
 */
public class CommentsServiceApiImpl implements CommentsServiceApi {

    private ArrayList<Comment> mComments;
    private Post mPost;

    @Override
    public void getComments(final String postId, final String subreddit,
                            final GetCommentsServiceCallback callback) {
        mComments = new ArrayList<>();
        if (Util.isNetworkAvailable()) {
            AsyncTaskCompat.executeParallel(new AsyncTask<Void, Void, Void>() {

                String error = null;

                @Override
                protected Void doInBackground(Void... params) {

                    try {
                        final Request request = new Request.Builder()
                                .addHeader("Content-type", "application/json")
                                .addHeader("Accept", "application/json")
                                .url(Util.COMMENTS_URL
                                        .replace("{subreddit}", subreddit)
                                        .replace("{postId}", postId))
                                .get()
                                .build();

                        Response response = new OkHttpClient().newCall(request).execute();

                        if (response == null || !response.isSuccessful()) {
                            error = "Server error (" + response + ")";
                            return null;
                        }

                        String responseBody = response.body().string();

                        //Util.Log("Comments response: " + responseBody);

                        JSONArray responseJson = new JSONArray(responseBody);
                        process(responseJson.getJSONObject(1).getJSONObject("data")
                                .getJSONArray("children"), 0);

                        mPost = PostsServiceApiImpl.parsePosts(responseJson.getJSONObject(0)
                                .getJSONObject("data").getJSONArray("children")).get(0);

                    } catch (Exception e) {
                        Util.Log("comments exception = " + e);
                        error = e.getMessage();
                    }
                    return null;
                }

                @Override
                protected void onPostExecute(Void result) {
                    if (mComments != null) {
                        callback.onCommentsLoaded(mComments);
                        callback.onPostLoaded(mPost);
                    } else {
                        callback.onError(error);
                    }
                }

                // This is where the comment is actually loaded
                // For each comment, its replies are recursively loaded
                private void process(JSONArray c, int depth)
                        throws Exception {
                    for (int i = 0; i < c.length(); i++) {
                        if (c.getJSONObject(i).optString("kind") == null)
                            continue;
                        Comment comment;
                        JSONObject data = c.getJSONObject(i).getJSONObject("data");
                        if (c.getJSONObject(i).optString("kind").equals("t1")) {
                            String commentId = data.getString("id");

                            String body = Util.formatBody(data.getString("body_html"));

                            Integer score = data.getInt("score");
                            String author = data.getString("author");
                            Boolean stickied = data.getBoolean("stickied");
                            String distinguished = data.getString("distinguished");
                            long timestamp = data.getLong("created") * 1000;
                            boolean scoreHidden = data.getBoolean("score_hidden");
                            int gildedCount = data.getInt("gilded");

                            comment = new Comment(depth, commentId, body, score, author,
                                    stickied, distinguished, timestamp, scoreHidden, gildedCount);
                        } else {
                            int moreCount = data.getInt("count");

                            comment = new Comment(depth, moreCount);
                        }
                        mComments.add(comment);
                        addReplies(data, depth + 1);
                    }
                }

                // Add replies to the comments
                private void addReplies(JSONObject parent, int depth) {
                    try {
                        if (parent.get("replies").equals("")) {
                            // This means the comment has no replies
                            return;
                        }
                        JSONArray r = parent.getJSONObject("replies")
                                .getJSONObject("data")
                                .getJSONArray("children");
                        process(r, depth);
                    } catch (Exception e) {
                        Log.d("ERROR", "addReplies : " + e);
                    }
                }
            });
        } else {
            SQLiteDatabase database = new DbHelper(MyApplication.getContext())
                    .getReadableDatabase();
            Cursor cursor = database.query(DbHelper.COMMENTS_TABLE, null, DbHelper
                    .COMMENTS_POST_ID_COLUMN + "=?", new String[]{postId}, null, null, null);
            while (cursor.moveToNext()) {
                int type = cursor.getInt(DbHelper.COMMENTS_TYPE_INDEX);
                if (type == Comment.TYPE_REPLY) {
                    mComments.add(new Comment(cursor.getInt(DbHelper.COMMENTS_DEPTH_INDEX),
                            cursor.getString(DbHelper.COMMENTS_ID_INDEX),
                            cursor.getString(DbHelper.COMMENTS_BODY_INDEX),
                            cursor.getInt(DbHelper.COMMENTS_SCORE_INDEX),
                            cursor.getString(DbHelper.COMMENTS_AUTHOR_INDEX),
                            cursor.getInt(DbHelper.COMMENTS_STICKIED_INDEX) == 1,
                            cursor.getString(DbHelper.COMMENTS_DISTINGUISHED_INDEX),
                            cursor.getLong(DbHelper.COMMENTS_TIMESTAMP_INDEX),
                            cursor.getInt(DbHelper.COMMENTS_SCORE_HIDDEN_INDEX) == 1,
                            cursor.getInt(DbHelper.COMMENTS_GILDED_COUNT_INDEX)));
                }
            }
            cursor.close();
            database.close();
            callback.onCommentsLoaded(mComments);
        }
    }
}
