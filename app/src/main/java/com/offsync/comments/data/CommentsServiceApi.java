package com.offsync.comments.data;

import com.offsync.posts.data.Post;

import java.util.ArrayList;

/**
 * Defines an interface to the service API that is used by this application. All data request should
 * be piped through this interface.
 */
public interface CommentsServiceApi {

    interface GetCommentsServiceCallback {

        void onPostLoaded(Post post);

        void onCommentsLoaded(ArrayList<Comment> comments);

        void onError(String message);
    }

    void getComments(String postId, String subreddit, GetCommentsServiceCallback callback);
}
