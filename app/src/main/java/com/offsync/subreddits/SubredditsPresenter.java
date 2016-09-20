package com.offsync.subreddits;

import com.offsync.subreddits.data.Subreddit;
import com.offsync.subreddits.data.SubredditsRepository;

import java.util.ArrayList;

/**
 * Listens to user actions from the UI ({@link com.offsync.posts.PostsActivity}), retrieves the data
 * and updates the UI as required.
 */
public class SubredditsPresenter implements SubredditsContract.UserActionsListener {

    private final SubredditsRepository mSubredditsRepository;
    private final SubredditsContract.View mPostsView;

    public SubredditsPresenter(SubredditsRepository subredditsRepository,
                               SubredditsContract.View postsView) {
        mSubredditsRepository = subredditsRepository;
        mPostsView = postsView;
    }

    @Override
    public void loadSubreddits() {
        mSubredditsRepository.getSubreddits(new SubredditsRepository.GetSubredditsCallback() {
            @Override
            public void onSubredditsLoaded(ArrayList<Subreddit> subreddits) {
                mPostsView.showSubreddits(subreddits);
            }

            @Override
            public void onError(String message) {
                mPostsView.showError(message);
            }
        });
    }

    @Override
    public void refreshData() {
        mSubredditsRepository.refreshData();
    }
}
