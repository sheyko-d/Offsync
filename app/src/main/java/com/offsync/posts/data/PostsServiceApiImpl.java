package com.offsync.posts.data;

import android.annotation.SuppressLint;
import android.app.NotificationManager;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.v4.os.AsyncTaskCompat;
import android.support.v7.app.NotificationCompat;
import android.text.TextUtils;
import android.webkit.ValueCallback;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SimpleTarget;
import com.offsync.comments.data.Comment;
import com.offsync.comments.data.CommentsRepository;
import com.offsync.util.DbHelper;
import com.offsync.util.MyApplication;
import com.offsync.util.Util;
import com.thin.downloadmanager.DefaultRetryPolicy;
import com.thin.downloadmanager.DownloadRequest;
import com.thin.downloadmanager.DownloadStatusListenerV1;
import com.thin.downloadmanager.ThinDownloadManager;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Locale;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * Implementation of the Posts Service API.
 */
public class PostsServiceApiImpl implements PostsServiceApi {

    private static final int NOTIFICATION_ID = 1000;

    private ArrayList<Post> mPosts;
    private SQLiteDatabase mDatabase;
    private SavePostsServiceCallback mCallback;
    private Integer mCurrentPos;
    private WebView mWebView;
    private NotificationManager mNotificationManager;
    private NotificationCompat.Builder mNotificationBuilder;
    private String mAfter;
    private String mFileStoredPath;
    private Post mPost;

    @Override
    public void getPosts(final String subreddit, boolean more, final String cachedAfter,
                         final GetPostsServiceCallback callback) {
        if (Util.isNetworkAvailable()) {
            AsyncTaskCompat.executeParallel(new AsyncTask<Void, Void, ArrayList<Post>>() {

                String error = null;

                @Override
                protected ArrayList<Post> doInBackground(Void... params) {
                    ArrayList<Post> posts = null;
                    try {
                        Uri.Builder builder = new Uri.Builder();
                        builder.scheme("https")
                                .authority("oauth.reddit.com");
                        if (!TextUtils.isEmpty(subreddit)) {
                            builder.appendPath("r")
                                    .appendPath(subreddit);
                        }
                        builder.appendPath(".json")
                                .appendQueryParameter("raw_json", "1");
                        //.appendQueryParameter("sort", "top")
                        //.appendQueryParameter("t", "all");
                        if (!TextUtils.isEmpty(cachedAfter)) {
                            builder.appendQueryParameter("after", cachedAfter);
                        }
                        final Request request = new Request.Builder()
                                .addHeader("Content-type", "application/json")
                                .addHeader("Accept", "application/json")
                                .addHeader("Authorization", "bearer " + Util.getToken())
                                .url(builder.build().toString())
                                .get()
                                .build();

                        Response response = new OkHttpClient().newCall(request).execute();

                        if (response == null || !response.isSuccessful()) {
                            error = "Server error (" + response + ")";
                            return null;
                        }

                        String responseBody = response.body().string();

                        Util.Log("Posts response: " + responseBody);

                        JSONObject responseJson = new JSONObject(responseBody);
                        posts = parsePosts(responseJson.getJSONObject("data")
                                .getJSONArray("children"));
                        mAfter = responseJson.getJSONObject("data").getString("after");

                    } catch (Exception e) {
                        error = e.getMessage();
                    }
                    return posts;
                }

                @Override
                protected void onPostExecute(ArrayList<Post> posts) {
                    if (posts != null) {
                        callback.onPostsLoaded(mAfter, posts);
                    } else {
                        callback.onError(error);
                    }
                }
            });
        } else {
            ArrayList<Post> posts = new ArrayList<>();
            SQLiteDatabase database = new DbHelper(MyApplication.getContext())
                    .getReadableDatabase();
            final Cursor cursor = database.query(DbHelper.POSTS_TABLE, null, null, null, null, null,
                    null);
            while (cursor.moveToNext()) {
                ArrayList<PostImage> images = new ArrayList<>();
                if (!cursor.isNull(DbHelper.POSTS_PREVIEW_STORED_PATH_INDEX)) {
                    images.add(new PostImage(cursor.getString(DbHelper
                            .POSTS_PREVIEW_STORED_PATH_INDEX), cursor.getInt(DbHelper
                            .POSTS_PREVIEW_HEIGHT_INDEX), cursor.getInt(DbHelper
                            .POSTS_PREVIEW_WIDTH_INDEX)));
                }
                posts.add(new Post(cursor.getString(DbHelper.POSTS_ID_INDEX),
                        cursor.getString(DbHelper.POSTS_TITLE_INDEX),
                        cursor.getInt(DbHelper.POSTS_SCORE_INDEX),
                        cursor.getString(DbHelper.POSTS_AUTHOR_INDEX),
                        cursor.getString(DbHelper.POSTS_SUBREDDIT_INDEX),
                        cursor.getInt(DbHelper.POSTS_COMMENTS_COUNT_INDEX),
                        cursor.getString(DbHelper.POSTS_DOMAIN_INDEX),
                        cursor.getLong(DbHelper.POSTS_TIMESTAMP_INDEX),
                        images,
                        cursor.getInt(DbHelper.POSTS_NSFW_INDEX) == 1,
                        cursor.getInt(DbHelper.POSTS_SELF_INDEX) == 1,
                        cursor.getInt(DbHelper.POSTS_TYPE_INDEX),
                        cursor.getString(DbHelper.POSTS_FLAIR_INDEX),
                        cursor.getInt(DbHelper.POSTS_TYPE_INDEX) != Post.TYPE_GIF ? cursor
                                .getString(DbHelper.POSTS_LINK_INDEX) : cursor.getString
                                (DbHelper.POSTS_FILE_STORED_PATH_INDEX),
                        cursor.getString(DbHelper.POSTS_TEXT_INDEX),
                        cursor.getInt(DbHelper.POSTS_STICKIED_INDEX) == 1,
                        Post.SAVED_STATUS_DOWNLOADED,
                        cursor.getInt(DbHelper.POSTS_NSFL_INDEX) == 1,
                        cursor.getInt(DbHelper.POSTS_GILDED_COUNT_INDEX),
                        cursor.getInt(DbHelper.POSTS_UPVOTED_INDEX) == 1,
                        cursor.getInt(DbHelper.POSTS_DOWNVOTED_INDEX) == 1));
                Util.Log(cursor.getString(DbHelper.POSTS_TITLE_INDEX) + " = " + new ArrayList<PostImage>() {{
                    add(new PostImage(cursor.getString(DbHelper
                            .POSTS_PREVIEW_STORED_PATH_INDEX), cursor.getInt(DbHelper
                            .POSTS_PREVIEW_HEIGHT_INDEX), cursor.getInt(DbHelper
                            .POSTS_PREVIEW_WIDTH_INDEX)));
                }}.size());
            }
            cursor.close();
            database.close();
            callback.onPostsLoaded(null, posts);
        }
    }

    public static ArrayList<Post> parsePosts(JSONArray postsJson) {
        ArrayList<Post> posts = new ArrayList<>();
        SQLiteDatabase database = new DbHelper(MyApplication.getContext())
                .getReadableDatabase();

        for (int i = 0; i < postsJson.length(); i++) {
            try {
                JSONObject post = postsJson.getJSONObject(i).getJSONObject("data");
                String postId = post.getString("id");
                String title = post.getString("title");
                Integer score = post.getInt("score");
                String author = post.getString("author");
                String subreddit = post.getString("subreddit");
                Integer commentsCount = post.getInt("num_comments");
                String domain = post.getString("domain");
                Long timestamp = post.getLong("created_utc") * 1000;

                Boolean isSelf = post.getBoolean("is_self");
                String link = post.getString("url");

                Integer type;
                if (isSelf) {
                    type = Post.TYPE_TEXT;
                } else {
                    if (link.contains("youtube.com/watch") || link.contains("youtu.be")) {
                        type = Post.TYPE_YOUTUBE;
                    } else if (link.contains(".gif") || link.contains("gfycat.com")) {
                        type = Post.TYPE_GIF;
                    } else if (link.contains("imgur.com/gallery")
                            || link.contains("imgur.com/a/")) {
                        type = Post.TYPE_GALLERY;
                    } else if (link.contains("i.reddituploads") || link.contains("imgur")
                            || link.contains(".jpg") || link.contains(".png")) {
                        type = Post.TYPE_PHOTO;
                    } else {
                        type = Post.TYPE_WEBSITE;
                    }
                }

                String flair = null;
                if (!post.isNull("link_flair_text")) {
                    flair = post.getString("link_flair_text");
                }
                Boolean nsfw = post.getBoolean("over_18");
                Boolean nsfl = title.toLowerCase(Locale.getDefault()).contains("nsfl")
                        || (flair + "").toLowerCase(Locale.getDefault()).contains("nsfl");

                String text = Util.formatBody(post.getString("selftext_html"));
                Boolean stickied = post.getBoolean("stickied");
                Integer gildedCount = post.getInt("gilded");

                // TODO: Retrieve an actual upvoted state
                boolean upvoted = false;
                // TODO: Retrieve an actual downvoted state
                boolean downvoted = false;

                Cursor cursorDownloaded = database.query(DbHelper.POSTS_TABLE, null,
                        DbHelper.POSTS_ID_COLUMN + "=?", new String[]{postId},
                        null, null, null);
                int savedStatus = cursorDownloaded.moveToFirst()
                        ? Post.SAVED_STATUS_DOWNLOADED : Post.SAVED_STATUS_DEFAULT;

                ArrayList<PostImage> images = new ArrayList<>();
                if (savedStatus == Post.SAVED_STATUS_DOWNLOADED) {
                    if (!cursorDownloaded.isNull(DbHelper
                            .POSTS_PREVIEW_STORED_PATH_INDEX)) {
                        images.add(new PostImage(cursorDownloaded.getString(DbHelper
                                .POSTS_PREVIEW_STORED_PATH_INDEX), cursorDownloaded.getInt(DbHelper
                                .POSTS_PREVIEW_HEIGHT_INDEX), cursorDownloaded.getInt(DbHelper
                                .POSTS_PREVIEW_WIDTH_INDEX)));
                    }

                    if (type == Post.TYPE_GIF) {
                        link = cursorDownloaded.getString(DbHelper.POSTS_FILE_STORED_PATH_INDEX);
                    }
                } else {
                    if (!post.isNull("preview")) {
                        JSONArray imagesJson = post.getJSONObject("preview")
                                .getJSONArray("images");
                        for (int imagePos = 0; imagePos < imagesJson.length(); imagePos++) {
                            JSONObject image = imagesJson.getJSONObject(imagePos)
                                    .getJSONObject("source");
                            images.add(new PostImage(image.getString("url"),
                                    image.getInt("width"), image.getInt("height")));
                        }
                    } else if (!post.isNull("media")) {
                        JSONObject image = post.getJSONObject("media").getJSONObject("oembed");
                        images.add(new PostImage(image.getString("thumbnail_url"),
                                image.getInt("width"), image.getInt("height")));
                    } else if (link.contains("imgur")) {
                        // TODO: Do something with height and width
                        images.add(new PostImage(Util.formatImgurUrl(link), Util.getScreenWidth(),
                                Util.convertDpToPixel(200)));
                    } else if (!TextUtils.isEmpty(post.getString("thumbnail")) && !post.getString
                            ("thumbnail").equals("self")) {
                        Util.Log("thumbnail = " + post.getString("thumbnail"));
                        // TODO: Do something with height and width
                        images.add(new PostImage(post.getString("thumbnail"), Util.getScreenWidth(),
                                Util.convertDpToPixel(200)));
                    }
                }

                cursorDownloaded.close();

                posts.add(new Post(postId, title, score, author, subreddit, commentsCount,
                        domain, timestamp, images, nsfw, isSelf, type, flair,
                        link, text, stickied, savedStatus, nsfl, gildedCount, upvoted, downvoted));
            } catch (Exception e) {
                Util.Log("Can't parse post: " + e);
            }
        }
        database.close();
        return posts;
    }

    @SuppressLint("SetJavaScriptEnabled")
    @Override
    public void savePosts(final ArrayList<Post> posts, final SavePostsServiceCallback callback) {
        mDatabase = new DbHelper(MyApplication.getContext())
                .getReadableDatabase();
        mPosts = posts;
        mCallback = callback;

        mCurrentPos = 0;

        mWebView = new WebView(MyApplication.getContext());
        mWebView.getSettings().setJavaScriptEnabled(true);
        mWebView.setWebViewClient(new CustomWebViewClient());
        mWebView.setWebChromeClient(new MyWebChromeClient());

        showSavingPostsNotification();

        Post post = mPosts.get(mCurrentPos);
        new PostSaver(post).save();
    }

    private void showSavingPostsNotification() {
        mNotificationManager = (NotificationManager) MyApplication.getContext()
                .getSystemService(Context.NOTIFICATION_SERVICE);
        mNotificationBuilder = new NotificationCompat.Builder(MyApplication.getContext());
        mNotificationBuilder.setContentTitle("Saving Reddit posts")
                .setSmallIcon(android.R.drawable.stat_sys_download);
        mNotificationManager.notify(NOTIFICATION_ID, mNotificationBuilder.build());
    }

    private class PostSaver {

        PostSaver(final Post post) {
            mPost = post;
            mFileStoredPath = null;
        }

        public void save() {
            mNotificationBuilder.setContentText((mCurrentPos + 1) + "/"
                    + mPosts.size());
            mNotificationBuilder.setProgress(mPosts.size(), mCurrentPos, false);
            mNotificationManager.notify(NOTIFICATION_ID, mNotificationBuilder.build());

            Util.provideCommentsRepository().getComments(mPost.getId(), mPost.getSubreddit(),
                    new CommentsRepository.GetCommentsCallback() {
                        @Override
                        public void onPostLoaded(Post post) {
                        }

                        @Override
                        public void onCommentsLoaded(ArrayList<Comment> comments) {
                            saveComments(comments);
                            savePost();
                        }

                        @Override
                        public void onError(String message) {
                            savePost();
                        }
                    });
        }

        @SuppressWarnings("deprecation")
        private void savePost() {
            if (mPost.getType() == Post.TYPE_PHOTO) {
                saveThumbnail();
            } else if (mPost.getType() == Post.TYPE_GIF) {
                saveGif(mPost.getLink());
            } else {
                mWebView.clearView();
                mWebView.loadUrl(mPost.getLink());
            }
        }

        private void saveComments(ArrayList<Comment> comments) {
            int commentsSize = comments.size();
            for (int i = 0; i < commentsSize; i++) {
                Comment comment = comments.get(i);
                ContentValues contentValues = new ContentValues();
                contentValues.put(DbHelper.COMMENTS_ID_COLUMN, comment.getId());
                contentValues.put(DbHelper.COMMENTS_POST_ID_COLUMN, mPost.getId());
                contentValues.put(DbHelper.COMMENTS_DEPTH_COLUMN, comment.getDepth());
                contentValues.put(DbHelper.COMMENTS_BODY_COLUMN, comment.getBody());
                contentValues.put(DbHelper.COMMENTS_SCORE_COLUMN, comment.getScore());
                contentValues.put(DbHelper.COMMENTS_AUTHOR_COLUMN, comment.getAuthor());
                contentValues.put(DbHelper.COMMENTS_STICKIED_COLUMN, comment.isStickied());
                contentValues.put(DbHelper.COMMENTS_DISTINGUISHED_COLUMN,
                        comment.getDistinguished());
                contentValues.put(DbHelper.COMMENTS_MORE_COUNT_COLUMN, comment.getMoreCount());
                contentValues.put(DbHelper.COMMENTS_TYPE_COLUMN, comment.getType());
                contentValues.put(DbHelper.COMMENTS_TIMESTAMP_COLUMN, comment.getTimestamp());
                contentValues.put(DbHelper.COMMENTS_SCORE_HIDDEN_COLUMN, comment.isScoreHidden());
                contentValues.put(DbHelper.COMMENTS_GILDED_COUNT_COLUMN, comment.getGildedCount());
                mDatabase.replace(DbHelper.COMMENTS_TABLE, null, contentValues);
            }
        }
    }

    private class CustomWebViewClient extends WebViewClient {

        @Override
        public void onPageFinished(WebView view, String url) {
            mWebView.saveWebArchive(MyApplication.getContext().getFilesDir().getAbsolutePath()
                    + "/" + mPost.getId() + ".mht", false, new ValueCallback<String>() {
                @Override
                public void onReceiveValue(String fileStoredPath) {
                    Util.Log("Saved post = " + mPost.getId() + ", " + fileStoredPath);

                    mFileStoredPath = "file://" + fileStoredPath;
                    if (mPost.getImages().size() > 0) {
                        saveThumbnail();
                    } else {
                        savePostToDatabase(mFileStoredPath, null, null, null);
                        mCallback.onPostSaved(mCurrentPos);
                        saveNextPost();
                    }
                }
            });
        }
    }

    private void saveThumbnail() {
        final String link = mPost.getImages().get(0).getUrl();
        Glide.with(MyApplication.getContext())
                .load(link)
                .asBitmap()
                .into(new SimpleTarget<Bitmap>() {
                    @Override
                    public void onResourceReady(Bitmap bitmap, GlideAnimation glideAnimation) {
                        try {
                            savePhoto(mPost.getId(), bitmap);
                            mCallback.onPostSaved(mCurrentPos);
                        } catch (Exception e) {
                            Util.Log("Photo saving failed: " + e);
                            mCallback.onPostFailed(mCurrentPos);
                        }
                        Util.Log("Thumbnail saved");

                        saveNextPost();
                    }

                    @Override
                    public void onLoadFailed(Exception e, Drawable errorDrawable) {
                        Util.Log("Thumbnail failed (" + e + ") = " + link);
                        mCallback.onPostFailed(mCurrentPos);

                        saveNextPost();
                    }
                });
    }

    private void saveGif(final String link) {
        Util.Log("download gif = " + Util.formatGifLink(link));
        Uri downloadUri = Uri.parse(Util.formatGifLink(link));
        mFileStoredPath = MyApplication.getContext().getFilesDir().getAbsolutePath() + "/"
                + mPost.getId() + ".webm";
        Uri destinationUri = Uri.parse(mFileStoredPath);
        DownloadRequest downloadRequest = new DownloadRequest(downloadUri)
                .setRetryPolicy(new DefaultRetryPolicy())
                .setDestinationURI(destinationUri).setPriority(DownloadRequest.Priority.HIGH)
                .setStatusListener(new DownloadStatusListenerV1() {
                    @Override
                    public void onDownloadComplete(DownloadRequest downloadRequest) {
                        Util.Log("GIF download complete");
                        if (mPost.getImages().size() > 0) {
                            saveThumbnail();
                        } else {
                            savePostToDatabase(mFileStoredPath, null, null, null);
                            mCallback.onPostSaved(mCurrentPos);
                            saveNextPost();
                        }
                    }

                    @Override
                    public void onDownloadFailed(DownloadRequest downloadRequest, int errorCode,
                                                 String errorMessage) {
                        Util.Log("GIF download failed");
                        mCallback.onPostFailed(mCurrentPos);
                        saveNextPost();
                    }

                    @Override
                    public void onProgress(DownloadRequest downloadRequest, long totalBytes,
                                           long downloadedBytes, int progress) {
                        Util.Log("Progress (#" + mCurrentPos + ") = " + progress);
                    }
                });
        new ThinDownloadManager().add(downloadRequest);
    }

    private void savePhoto(String postId, Bitmap bitmap) throws Exception {
        String folderPath = MyApplication.getContext().getFilesDir().getAbsolutePath() + "/";
        File folder = new File(folderPath);
        if (folder.exists() || folder.mkdirs()) {
            String path = folderPath + postId + ".jpg";
            File file = new File(path);
            OutputStream os = new FileOutputStream(file);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, os);

            savePostToDatabase(mFileStoredPath, path, bitmap.getWidth(), bitmap.getHeight());
        }
    }

    private void saveNextPost() {
        mCurrentPos++;
        if (mCurrentPos < mPosts.size() - 1) {
            Post post = mPosts.get(mCurrentPos);
            new PostSaver(post).save();
        } else {
            mNotificationManager.cancel(NOTIFICATION_ID);
        }
    }

    private class MyWebChromeClient extends WebChromeClient {

        @Override
        public void onProgressChanged(WebView view, int progress) {
            Util.Log("Progress (#" + mCurrentPos + ") = " + progress);
        }
    }

    private void updateProgress(int progress) {
        mNotificationBuilder.setProgress(100, progress, false);
        mNotificationManager.notify(NOTIFICATION_ID, mNotificationBuilder.build());
    }

    private void savePostToDatabase(String fileStoredPath, String previewStoredPath,
                                    Integer previewHeight, Integer previewWidth) {
        ContentValues contentValues = new ContentValues();
        contentValues.put(DbHelper.POSTS_ID_COLUMN, mPost.getId());
        contentValues.put(DbHelper.POSTS_TITLE_COLUMN, mPost.getTitle());
        contentValues.put(DbHelper.POSTS_SCORE_COLUMN, mPost.getScore());
        contentValues.put(DbHelper.POSTS_AUTHOR_COLUMN, mPost.getAuthor());
        contentValues.put(DbHelper.POSTS_SUBREDDIT_COLUMN, mPost.getSubreddit());
        contentValues.put(DbHelper.POSTS_COMMENTS_COUNT_COLUMN, mPost.getCommentsCount());
        contentValues.put(DbHelper.POSTS_DOMAIN_COLUMN, mPost.getDomain());
        contentValues.put(DbHelper.POSTS_TIMESTAMP_COLUMN, mPost.getTimestamp());
        contentValues.put(DbHelper.POSTS_NSFW_COLUMN, mPost.isNsfw());
        contentValues.put(DbHelper.POSTS_NSFL_COLUMN, mPost.isNsfl());
        contentValues.put(DbHelper.POSTS_SELF_COLUMN, mPost.isSelf());
        contentValues.put(DbHelper.POSTS_TYPE_COLUMN, mPost.getType());
        contentValues.put(DbHelper.POSTS_FLAIR_COLUMN, mPost.getFlair());
        contentValues.put(DbHelper.POSTS_LINK_COLUMN, mPost.getLink());
        contentValues.put(DbHelper.POSTS_TEXT_COLUMN, mPost.getText());
        contentValues.put(DbHelper.POSTS_STICKIED_COLUMN, mPost.isStickied());
        contentValues.put(DbHelper.POSTS_GILDED_COUNT_COLUMN_COLUMN, mPost.getGildedCount());
        contentValues.put(DbHelper.POSTS_FILE_STORED_PATH_COLUMN, fileStoredPath);
        contentValues.put(DbHelper.POSTS_PREVIEW_STORED_PATH_COLUMN, previewStoredPath);
        contentValues.put(DbHelper.POSTS_PREVIEW_HEIGHT_COLUMN, previewHeight);
        contentValues.put(DbHelper.POSTS_PREVIEW_WIDTH_COLUMN, previewWidth);
        contentValues.put(DbHelper.POSTS_UPVOTED_COLUMN, mPost.isUpvoted());
        contentValues.put(DbHelper.POSTS_DOWNVOTED_COLUMN, mPost.isDownvoted());
        mDatabase.replace(DbHelper.POSTS_TABLE, null, contentValues);
    }
}
