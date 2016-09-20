package com.offsync.subreddits.data;

import android.support.annotation.VisibleForTesting;

import java.util.ArrayList;

/**
 * Concrete implementation to load subreddits from the a data source.
 */
public class InMemorySubredditRepository implements SubredditsRepository {

    private final SubredditsServiceApi mSubredditsServiceApi;

    /**
     * This method has reduced visibility for testing and is only visible to tests in the same
     * package.
     */
    @VisibleForTesting
    ArrayList<Subreddit> mCachedSubreddits;

    public InMemorySubredditRepository(SubredditsServiceApi subredditsServiceApi) {
        mSubredditsServiceApi = subredditsServiceApi;
    }

    @Override
    public void getSubreddits(final GetSubredditsCallback callback) {
        // Load from API only if needed.
        if (mCachedSubreddits == null) {
            mSubredditsServiceApi.getSubreddits(new SubredditsServiceApi
                    .GetSubredditsServiceCallback() {
                @Override
                public void onSubredditsLoaded(ArrayList<Subreddit> subreddits) {
                    mCachedSubreddits = subreddits;
                    callback.onSubredditsLoaded(mCachedSubreddits);
                }

                @Override
                public void onError(String message) {
                    callback.onError(message);
                }
            });
        } else {
            callback.onSubredditsLoaded(mCachedSubreddits);
        }
    }

    @Override
    public void refreshData() {
        mCachedSubreddits = null;
    }
}
