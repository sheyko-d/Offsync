package com.offsync.subreddits.data;

import java.util.ArrayList;

/**
 * Main entry point for accessing subreddits data.
 */
public interface SubredditsRepository {

    interface GetSubredditsCallback {

        void onSubredditsLoaded(ArrayList<Subreddit> subreddits);

        void onError(String message);
    }

    void getSubreddits(GetSubredditsCallback callback);

    void refreshData();
}
