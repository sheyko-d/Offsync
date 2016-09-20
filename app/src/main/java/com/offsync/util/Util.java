package com.offsync.util;

import android.app.Activity;
import android.content.Context;
import android.content.res.Resources;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.preference.PreferenceManager;
import android.text.format.DateUtils;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.TypedValue;
import android.webkit.CookieManager;
import android.webkit.CookieSyncManager;

import com.offsync.R;
import com.offsync.album.data.AlbumImageRepositories;
import com.offsync.album.data.AlbumImagesRepository;
import com.offsync.album.data.AlbumImagesServiceApiImpl;
import com.offsync.comments.data.CommentRepositories;
import com.offsync.comments.data.CommentsRepository;
import com.offsync.comments.data.CommentsServiceApiImpl;
import com.offsync.posts.data.PostRepositories;
import com.offsync.posts.data.PostsRepository;
import com.offsync.posts.data.PostsServiceApiImpl;
import com.offsync.subreddits.data.SubredditRepositories;
import com.offsync.subreddits.data.SubredditsRepository;
import com.offsync.subreddits.data.SubredditsServiceApiImpl;

public class Util {

    public final static String COMMENTS_URL = "https://www.reddit.com/r/{subreddit}/comments/" +
            "{postId}.json?sort=top";
    public final static String IMGUR_IMAGE_URL = "https://api.imgur.com/3/image/{id}";
    public final static String IMGUR_ALBUM_URL = "https://api.imgur.com/3/album/{id}";
    public final static String SUBREDDITS_URL = "https://www.reddit.com/subreddits/mine/";
    public final static String LOG_TAG = "Offsync";
    public static final String IMGUR_CLIENT_HEADER = "Client-ID";
    public static final String PREF_TOKEN = "Token";

    /**
     * Adds a message to LogCat.
     */
    public static void Log(Object text) {
        Log.d(LOG_TAG, text + "");
    }

    public static PostsRepository providePostsRepository() {
        return PostRepositories.getInMemoryRepoInstance(new PostsServiceApiImpl());
    }

    public static CommentsRepository provideCommentsRepository() {
        return CommentRepositories.getInMemoryRepoInstance(new CommentsServiceApiImpl());
    }

    public static AlbumImagesRepository provideAlbumRepository() {
        return AlbumImageRepositories.getInMemoryRepoInstance(new AlbumImagesServiceApiImpl());
    }

    public static SubredditsRepository provideSubredditsRepository() {
        return SubredditRepositories.getInMemoryRepoInstance(new SubredditsServiceApiImpl());
    }

    public static boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager = (ConnectivityManager) MyApplication.getContext()
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    public static String removeDomainExtension(String url) {
        if (url.contains("imgur")) {
            return "imgur";
        } else if (url.contains("youtube") || url.contains("youtu.be")) {
            return "youtube";
        }
        return url.replace(".com", "")
                .replace(".org", "")
                .replace(".net", "")
                .replace(".int", "")
                .replace(".edu", "")
                .replace(".gov", "")
                .replace(".mil", "");
    }

    public static String getYouTubeIdFromUrl(String url) {
        return url.replace("https://www.youtube.com/watch?v=", "")
                .replace("http://www.youtube.com/watch?v=", "").replace("https://youtu.be/", "")
                .replace("http://youtu.be/", "");
    }

    /**
     * Converts from DP (density-independent pixels) to regular pixels.
     */
    public static int convertDpToPixel(float dp) {
        Resources resources = MyApplication.getContext().getResources();
        DisplayMetrics metrics = resources.getDisplayMetrics();
        return (int) (dp * (metrics.densityDpi / 160f));
    }

    public static int getScreenWidth() {
        return MyApplication.getContext().getResources().getDisplayMetrics().widthPixels;
    }

    public static int getScreenHeight() {
        return MyApplication.getContext().getResources().getDisplayMetrics().heightPixels;
    }

    /**
     * Measures toolbar (action bar) height.
     */
    public static int getToolbarHeight(Activity activity) {
        TypedValue tv = new TypedValue();

        if (activity.getTheme().resolveAttribute(R.attr.actionBarSize, tv, true)) {
            return TypedValue.complexToDimensionPixelSize(tv.data, activity.getResources()
                    .getDisplayMetrics());
        } else {
            return -1;
        }
    }

    /**
     * Measures height of the system status bar.
     */
    public static int getStatusBarHeight() {
        int result = 0;
        int resourceId = MyApplication.getContext().getResources().getIdentifier
                ("status_bar_height", "dimen", "android");
        if (resourceId > 0) {
            result = MyApplication.getContext().getResources().getDimensionPixelSize(resourceId);
        }
        return result;
    }

    public static String formatTime(long timestamp) {
        return DateUtils.getRelativeTimeSpanString(timestamp, System.currentTimeMillis(),
                DateUtils.FORMAT_ABBREV_ALL).toString();
    }

    public static String parseImgurId(String link) {
        if (link.endsWith("/")) {
            link = link.substring(0, link.length() - 1);
        }
        link = link.substring(link.lastIndexOf("/") + 1, link.length());
        return link;
    }

    public static String formatBody(String body) {
        body = body.replace("&lt;", "<").replace("&gt;", ">").replace("&quot;", "\"")
                .replace("&apos;", "'").replace("&amp;", "&").replace("<li><p>", "<br>• ")
                .replaceFirst("<br>• ", "• ").replace("</li>", "").replaceAll("<li.*?>", "• ");
        if (body.contains("</p>")) {
            body = body.substring(0, body.lastIndexOf("</p>"));
            body = body.substring(0, body.lastIndexOf("<p>")) + body.substring(body
                    .lastIndexOf("<p>") + 3, body.length());
        }
        body = body.replaceFirst("<div class=\"md\">", "");
        body = body.replace("<a href=\"/r/", "<a href=\"https://reddit.com/r/");
        body = body.replace("<a href=\"/u/", "<a href=\"https://reddit.com/u/");
        //Util.Log("body = " + body);
        return body;
    }

    public static String formatGifLink(String link) {
        if (link.contains("imgur")) {
            link = link.replace(".gifv", ".webm");
            link = link.replace(".gif", ".webm");
        } else if (link.contains("gfycat")) {

            link = link.replace("//gfycat", "//fat.gfycat");

            if (!link.endsWith(".webm")) {
                link = link + ".webm";
            }
        }
        return link;
    }

    public static String formatImgurUrl(String link) {
        return link + ".jpg";
    }

    public static void setToken(String token) {
        PreferenceManager.getDefaultSharedPreferences(MyApplication.getContext()).edit()
                .putString(PREF_TOKEN, token).apply();
    }

    public static String getToken() {
        return PreferenceManager.getDefaultSharedPreferences(MyApplication.getContext())
                .getString(PREF_TOKEN, null);
    }

    @SuppressWarnings("deprecation")
    public static void clearCookies() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP_MR1) {
            CookieManager.getInstance().removeAllCookies(null);
            CookieManager.getInstance().flush();
        } else {
            CookieSyncManager cookieSyncManger = CookieSyncManager.createInstance
                    (MyApplication.getContext());
            cookieSyncManger.startSync();
            CookieManager cookieManager = CookieManager.getInstance();
            cookieManager.removeAllCookie();
            cookieManager.removeSessionCookie();
            cookieSyncManger.stopSync();
            cookieSyncManger.sync();
        }
    }
}
