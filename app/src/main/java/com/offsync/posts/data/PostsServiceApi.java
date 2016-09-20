package com.offsync.posts.data;

import java.util.ArrayList;

/**
 * Defines an interface to the service API that is used by this application. All data request should
 * be piped through this interface.
 */
public interface PostsServiceApi {

    interface GetPostsServiceCallback {

        void onPostsLoaded(String after, ArrayList<Post> posts);

        void onError(String message);
    }

    interface SavePostsServiceCallback {

        void onPostSaved(int pos);

        void onPostFailed(int pos);

        void onError(String message);
    }

    void getPosts(String subreddit, boolean more, String cachedAfter,
                  GetPostsServiceCallback callback);

    void savePosts(ArrayList<Post> posts, SavePostsServiceCallback callback);
}
