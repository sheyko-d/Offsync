package com.offsync.subreddits.data;

import android.os.AsyncTask;
import android.support.v4.os.AsyncTaskCompat;

import com.offsync.util.Util;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * Implementation of the Subreddits Service API.
 */
public class SubredditsServiceApiImpl implements SubredditsServiceApi {

    @Override
    public void getSubreddits(final GetSubredditsServiceCallback callback) {
        AsyncTaskCompat.executeParallel(new AsyncTask<Void, Void, ArrayList<Subreddit>>() {

            String error = null;

            @Override
            protected ArrayList<Subreddit> doInBackground(Void... params) {
                ArrayList<Subreddit> subreddits = null;
                try {
                    final Request request = new Request.Builder()
                            .addHeader("Content-type", "application/json")
                            .addHeader("Accept", "application/json")
                            .addHeader("Authorization", "bearer " + Util.getToken())
                            .url(Util.SUBREDDITS_URL)
                            .get()
                            .build();

                    Response response = new OkHttpClient().newCall(request).execute();

                    if (response == null || !response.isSuccessful()) {
                        error = "Server error (" + response + ")";
                        return null;
                    }

                    String responseBody = response.body().string();

                    Util.Log("Subreddits response: " + responseBody);

                    JSONObject responseJson = new JSONObject(responseBody);
                    subreddits = parseSubreddits(responseJson.getJSONObject("data")
                            .getJSONArray("children"));

                } catch (Exception e) {
                    error = e.getMessage();
                }
                return subreddits;
            }

            @Override
            protected void onPostExecute(ArrayList<Subreddit> subreddits) {
                if (subreddits != null) {
                    callback.onSubredditsLoaded(subreddits);
                } else {
                    callback.onError(error);
                }
            }
        });
    }

    public static ArrayList<Subreddit> parseSubreddits(JSONArray subredditsJson) {
        ArrayList<Subreddit> subreddits = new ArrayList<>();

        for (int i = 0; i < subredditsJson.length(); i++) {
            try {
                JSONObject subreddit = subredditsJson.getJSONObject(i).getJSONObject("data");
                String subredditId = subreddit.getString("id");
                String title = subreddit.getString("title");

                subreddits.add(new Subreddit(subredditId, title));
            } catch (Exception e) {
                Util.Log("Can't parse subreddit: " + e);
            }
        }
        return subreddits;
    }
}
