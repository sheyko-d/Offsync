package com.offsync.comments.data;

import android.support.annotation.VisibleForTesting;
import android.text.TextUtils;

import com.offsync.posts.data.InMemoryPostRepository;
import com.offsync.posts.data.Post;
import com.offsync.util.Util;

import java.util.ArrayList;

/**
 * Concrete implementation to load comments from the a data source.
 */
public class InMemoryCommentRepository implements CommentsRepository {

    private final CommentsServiceApi mCommentsServiceApi;

    /**
     * This method has reduced visibility for testing and is only visible to tests in the same
     * package.
     */
    @VisibleForTesting
    Post mCachedPost;
    @VisibleForTesting
    ArrayList<Comment> mCachedComments;
    @VisibleForTesting
    String mCachedPostId;

    public InMemoryCommentRepository(CommentsServiceApi commentsServiceApi) {
        mCommentsServiceApi = commentsServiceApi;
    }

    @Override
    public void getComments(final String postId, String subreddit,
                            final GetCommentsCallback callback) {
        // Load from API only if needed.
        if (TextUtils.isEmpty(mCachedPostId) || !mCachedPostId.equals(postId)
                || mCachedComments == null) {

            if (InMemoryPostRepository.cachedPosts != null) {
                for (Post post : InMemoryPostRepository.cachedPosts) {
                    if (post.getId().equals(postId)) {
                        mCachedPost = post;
                        mCachedPostId = postId;
                        callback.onPostLoaded(post);
                        break;
                    }
                }
            }

            mCommentsServiceApi.getComments(postId, subreddit, new CommentsServiceApi
                    .GetCommentsServiceCallback() {
                @Override
                public void onPostLoaded(Post post) {
                    mCachedPost = post;
                }

                @Override
                public void onCommentsLoaded(ArrayList<Comment> comments) {
                    mCachedComments = comments;
                    mCachedPostId = postId;
                    callback.onCommentsLoaded(comments);
                }

                @Override
                public void onError(String message) {
                    callback.onError(message);
                }
            });
        } else {
            Util.Log("load cached comments = " + mCachedPostId);
            callback.onPostLoaded(mCachedPost);
            callback.onCommentsLoaded(mCachedComments);
        }
    }

    @Override
    public void refreshData() {
        mCachedComments = null;
    }
}
