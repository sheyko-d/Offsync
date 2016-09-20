package com.offsync.comments.data;

import com.offsync.posts.data.Post;

import java.util.ArrayList;

/**
 * Main entry point for accessing comments data.
 */
public interface CommentsRepository {

    interface GetCommentsCallback {

        void onPostLoaded(Post post);

        void onCommentsLoaded(ArrayList<Comment> comments);

        void onError(String message);
    }

    void getComments(String postId, String subreddit, GetCommentsCallback callback);

    void refreshData();
}
