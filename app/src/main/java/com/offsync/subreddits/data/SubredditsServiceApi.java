package com.offsync.subreddits.data;

import java.util.ArrayList;

/**
 * Defines an interface to the service API that is used by this application. All data request should
 * be piped through this interface.
 */
public interface SubredditsServiceApi {

    interface GetSubredditsServiceCallback {

        void onSubredditsLoaded(ArrayList<Subreddit> subreddits);

        void onError(String message);
    }

    void getSubreddits(GetSubredditsServiceCallback callback);
}
