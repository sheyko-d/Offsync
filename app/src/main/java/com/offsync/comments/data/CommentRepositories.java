package com.offsync.comments.data;

public class CommentRepositories {

    private CommentRepositories() {
        // no instance
    }

    private static CommentsRepository repository = null;

    public synchronized static CommentsRepository getInMemoryRepoInstance
            (CommentsServiceApi commentsServiceApi) {
        if (null == repository) {
            repository = new InMemoryCommentRepository(commentsServiceApi);
        }
        return repository;
    }
}