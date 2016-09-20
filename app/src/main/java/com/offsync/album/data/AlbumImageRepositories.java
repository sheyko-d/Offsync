package com.offsync.album.data;

public class AlbumImageRepositories {

    private AlbumImageRepositories() {
        // no instance
    }

    private static AlbumImagesRepository repository = null;

    public synchronized static AlbumImagesRepository getInMemoryRepoInstance
            (AlbumImagesServiceApi albumImagesServiceApi) {
        if (null == repository) {
            repository = new InMemoryAlbumImageRepository(albumImagesServiceApi);
        }
        return repository;
    }
}