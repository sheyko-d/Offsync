package com.offsync.postdetails;

import com.offsync.comments.data.Comment;
import com.offsync.comments.data.CommentsRepository;
import com.offsync.posts.data.Post;

import java.util.ArrayList;

/**
 * Listens to user actions from the UI ({@link PostDetailsActivity}), retrieves the data and updates
 * the UI as required.
 */
public class PostDetailsPresenter implements PostDetailsContract.UserActionsListener {

    private final CommentsRepository mCommentsRepository;
    private final PostDetailsContract.View mPostDetailsView;

    public PostDetailsPresenter(CommentsRepository commentsRepository,
                                PostDetailsContract.View postDetailsView) {
        mCommentsRepository = commentsRepository;
        mPostDetailsView = postDetailsView;
    }

    @Override
    public void loadComments(String postId, String subreddit) {
        mPostDetailsView.setProgressBar(true);
        mCommentsRepository.getComments(postId, subreddit, new CommentsRepository
                .GetCommentsCallback() {
            @Override
            public void onPostLoaded(Post post) {
                mPostDetailsView.showPost(post);
            }

            @Override
            public void onCommentsLoaded(ArrayList<Comment> comments) {
                mPostDetailsView.setProgressBar(false);
                mPostDetailsView.showComments(comments);
            }

            @Override
            public void onError(String message) {
                mPostDetailsView.setProgressBar(false);
                mPostDetailsView.showError(message);
            }
        });
    }

    @Override
    public void refreshData() {
        mCommentsRepository.refreshData();
    }

    @Override
    public void scrollToNextComment() {
        mPostDetailsView.scrollToNextComment();
    }

    @Override
    public void scrollToNextBranch() {
        mPostDetailsView.scrollToNextBranch();
    }
}
