package com.offsync.album.data;

import android.os.AsyncTask;
import android.support.v4.os.AsyncTaskCompat;

import com.offsync.R;
import com.offsync.posts.data.Post;
import com.offsync.util.MyApplication;
import com.offsync.util.Util;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * Implementation of the Albums Service API.
 */
public class AlbumImagesServiceApiImpl implements AlbumImagesServiceApi {

    private ArrayList<AlbumImage> mAlbumImages;
    private Post mPost;

    @Override
    public void getAlbumImages(final String albumId, final GetAlbumServiceCallback callback) {
        AsyncTaskCompat.executeParallel(new AsyncTask<Void, Void, Void>() {

            String error = null;

            @Override
            protected Void doInBackground(Void... params) {
                mAlbumImages = new ArrayList<>();

                try {
                    final Request request = new Request.Builder()
                            .url(Util.IMGUR_ALBUM_URL.replace("{id}", albumId))
                            .addHeader(Util.IMGUR_CLIENT_HEADER, MyApplication.getContext()
                                    .getString(R.string.imgur_client_id))
                            .build();

                    Response response = new OkHttpClient().newCall(request).execute();

                    if (response == null || !response.isSuccessful()) {
                        error = "Server error (" + response + ")";
                        return null;
                    }

                    String responseBody = response.body().string();

                    parseAlbumImages(new JSONObject(responseBody).getJSONObject("data")
                            .getJSONArray("images"));

                } catch (Exception e) {
                    Util.Log("comments exception = " + e);
                    error = e.getMessage();
                }
                return null;
            }

            @Override
            protected void onPostExecute(Void result) {
                if (mAlbumImages != null) {
                    callback.onImagesLoaded(mAlbumImages);
                } else {
                    callback.onError(error);
                }
            }
        });
    }

    private void parseAlbumImages(JSONArray albumImagesJson) {
        for (int i = 0; i < albumImagesJson.length(); i++) {
            try {
                JSONObject albumImage = albumImagesJson.getJSONObject(i);
                mAlbumImages.add(new AlbumImage(albumImage.getString("title"), albumImage
                        .getString("description"), albumImage.getString("link"), albumImage
                        .getInt("height"), albumImage.getInt("width")));
            } catch (Exception e) {
                Util.Log("Can't parse Imgur album image: " + e);
            }
        }
    }
}