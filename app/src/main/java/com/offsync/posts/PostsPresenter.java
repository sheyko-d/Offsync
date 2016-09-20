package com.offsync.posts;

import android.view.View;

import com.offsync.posts.data.Post;
import com.offsync.posts.data.PostsRepository;

import java.util.ArrayList;

/**
 * Listens to user actions from the UI ({@link PostsFragment}), retrieves the data and updates
 * the UI as required.
 */
public class PostsPresenter implements PostsContract.UserActionsListener {

    private final PostsRepository mPostsRepository;
    private final PostsContract.View mPostsView;

    public PostsPresenter(PostsRepository postsRepository,
                          PostsContract.View postsView) {
        mPostsRepository = postsRepository;
        mPostsView = postsView;
    }

    @Override
    public void loadPosts(String subreddit) {
        mPostsView.setProgressBar(true);
        mPostsRepository.getPosts(subreddit, false, new PostsRepository.GetPostsCallback() {
            @Override
            public void onPostsLoaded(ArrayList<Post> posts) {
                mPostsView.setProgressBar(false);
                mPostsView.showPosts(posts);
            }

            @Override
            public void onError(String message) {
                mPostsView.setProgressBar(false);
                mPostsView.showError(message);
            }
        });
    }

    @Override
    public void loadMorePosts(String subreddit) {
        mPostsView.setMoreProgressBar(true);
        mPostsRepository.getPosts(subreddit, true, new PostsRepository.GetPostsCallback() {
            @Override
            public void onPostsLoaded(ArrayList<Post> posts) {
                mPostsView.setMoreProgressBar(false);
                mPostsView.showPosts(posts);
            }

            @Override
            public void onError(String message) {
                mPostsView.setMoreProgressBar(false);
                mPostsView.showError(message);
            }
        });
    }

    @Override
    public void refreshData() {
        mPostsRepository.refreshData();
    }

    @Override
    public void openPostFullscreen(View view, Post post) {
        mPostsView.openPostFullscreen(view, post);
    }

    @Override
    public void openPostDetails(View v, Post post) {
        mPostsView.openPostDetails(v, post);
    }

    @Override
    public void savePosts(ArrayList<Post> posts) {
        mPostsRepository.savePosts(posts, new PostsRepository.SavePostsCallback() {
            @Override
            public void onPostSaved(int pos) {
                mPostsView.markPostAsSavedAt(pos);
            }

            @Override
            public void onPostFailed(int pos) {
                mPostsView.markPostAsFailedAt(pos);
            }

            @Override
            public void onError(String message) {
                mPostsView.showError(message);
            }
        });
    }

    @Override
    public void upvote(int pos, Post post) {
        mPostsView.setUpvoted(pos, !post.isUpvoted());
        mPostsView.setDownvoted(pos, false);
    }

    @Override
    public void downvote(int pos, Post post) {
        mPostsView.setDownvoted(pos, !post.isDownvoted());
        mPostsView.setUpvoted(pos, false);
    }
}
