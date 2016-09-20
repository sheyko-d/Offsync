package com.offsync.posts.data;

public class PostRepositories {

    private PostRepositories() {
        // no instance
    }

    private static PostsRepository repository = null;

    public synchronized static PostsRepository getInMemoryRepoInstance
            (PostsServiceApi postsServiceApi) {
        if (null == repository) {
            repository = new InMemoryPostRepository(postsServiceApi);
        }
        return repository;
    }
}