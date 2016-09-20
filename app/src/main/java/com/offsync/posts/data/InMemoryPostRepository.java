package com.offsync.posts.data;

import android.support.annotation.VisibleForTesting;

import java.util.ArrayList;

/**
 * Concrete implementation to load posts from the a data source.
 */
public class InMemoryPostRepository implements PostsRepository {

    private final PostsServiceApi mPostsServiceApi;

    /**
     * This method has reduced visibility for testing and is only visible to tests in the same
     * package.
     */
    @VisibleForTesting
    public static ArrayList<Post> cachedPosts;
    @VisibleForTesting
    String mCachedAfter;

    public InMemoryPostRepository(PostsServiceApi postsServiceApi) {
        mPostsServiceApi = postsServiceApi;
    }

    @Override
    public void getPosts(final String subreddit, final boolean more,
                         final GetPostsCallback callback) {
        // Load from API only if needed.
        if (more || cachedPosts == null) {
            mPostsServiceApi.getPosts(subreddit, more, mCachedAfter,
                    new PostsServiceApi.GetPostsServiceCallback() {
                        @Override
                        public void onPostsLoaded(String after, ArrayList<Post> posts) {
                            if (more) {
                                cachedPosts.addAll(posts);
                            } else {
                                cachedPosts = posts;
                            }
                            mCachedAfter = after;
                            callback.onPostsLoaded(cachedPosts);
                        }

                        @Override
                        public void onError(String message) {
                            callback.onError(message);
                        }
                    });
        } else {
            callback.onPostsLoaded(cachedPosts);
        }
    }

    @Override
    public void savePosts(ArrayList<Post> posts, final SavePostsCallback callback) {
        mPostsServiceApi.savePosts(posts, new PostsServiceApi.SavePostsServiceCallback() {
            @Override
            public void onPostSaved(int pos) {
                callback.onPostSaved(pos);
            }

            @Override
            public void onPostFailed(int pos) {
                callback.onPostFailed(pos);
            }

            @Override
            public void onError(String message) {
                callback.onError(message);
            }
        });
    }

    @Override
    public ArrayList<Post> getCurrentCachedPosts() {
        return cachedPosts;
    }

    @Override
    public void refreshData() {
        cachedPosts = null;
    }
}
