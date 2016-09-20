package com.offsync.subreddits.data;

public class SubredditRepositories {

    private SubredditRepositories() {
        // no instance
    }

    private static SubredditsRepository repository = null;

    public synchronized static SubredditsRepository getInMemoryRepoInstance
            (SubredditsServiceApi subredditsServiceApi) {
        if (null == repository) {
            repository = new InMemorySubredditRepository(subredditsServiceApi);
        }
        return repository;
    }
}