package com.offsync.subreddits;


import com.offsync.subreddits.data.Subreddit;

import java.util.ArrayList;

public interface SubredditsContract {

    interface View {

        void showSubreddits(ArrayList<Subreddit> subreddits);

        void showError(String message);
    }

    interface UserActionsListener {

        void loadSubreddits();

        void refreshData();
    }
}