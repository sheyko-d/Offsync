package com.offsync.posts.data;

import java.util.ArrayList;

/**
 * Main entry point for accessing posts data.
 */
public interface PostsRepository {

    interface GetPostsCallback {

        void onPostsLoaded(ArrayList<Post> posts);

        void onError(String message);
    }

    interface SavePostsCallback {

        void onPostSaved(int pos);

        void onPostFailed(int pos);

        void onError(String message);
    }

    ArrayList<Post> getCurrentCachedPosts();

    void getPosts(String subreddit, boolean more, GetPostsCallback callback);

    void savePosts(ArrayList<Post> posts, SavePostsCallback callback);

    void refreshData();
}
