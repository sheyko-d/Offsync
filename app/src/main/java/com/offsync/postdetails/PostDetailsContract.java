package com.offsync.postdetails;


import com.offsync.comments.data.Comment;
import com.offsync.posts.data.Post;

import java.util.ArrayList;

public interface PostDetailsContract {

    interface View {

        void showComments(ArrayList<Comment> comments);

        void showPost(Post post);

        void showError(String message);

        void setProgressBar(boolean visible);

        void scrollToNextComment();

        void scrollToNextBranch();
    }

    interface UserActionsListener {

        void loadComments(String postId, String subreddit);

        void refreshData();

        void scrollToNextComment();

        void scrollToNextBranch();
    }
}