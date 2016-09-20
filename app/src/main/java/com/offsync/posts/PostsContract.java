package com.offsync.posts;


import com.offsync.posts.data.Post;

import java.util.ArrayList;

public interface PostsContract {

    interface View {

        void showPosts(ArrayList<Post> posts);

        void showError(String message);

        void setProgressBar(boolean visible);

        void setMoreProgressBar(boolean visible);

        void openPostFullscreen(android.view.View view, Post post);

        void openPostDetails(android.view.View v, Post post);

        void markPostAsSavedAt(int pos);

        void markPostAsFailedAt(int pos);

        void setUpvoted(int pos, boolean upvoted);

        void setDownvoted(int pos, boolean downvoted);
    }

    interface UserActionsListener {

        void loadPosts(String subreddit);

        void loadMorePosts(String subreddit);

        void refreshData();

        void openPostFullscreen(android.view.View view, Post post);

        void openPostDetails(android.view.View v, Post post);

        void savePosts(ArrayList<Post> posts);

        void upvote(int pos, Post post);

        void downvote(int pos, Post post);
    }
}