package com.offsync.postpreview;

import android.annotation.SuppressLint;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;
import android.widget.MediaController;
import android.widget.ProgressBar;
import android.widget.Toast;
import android.widget.VideoView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.google.android.youtube.player.YouTubeInitializationResult;
import com.google.android.youtube.player.YouTubePlayer;
import com.google.android.youtube.player.YouTubePlayerSupportFragment;
import com.offsync.R;
import com.offsync.album.data.AlbumImage;
import com.offsync.postpreview.adapter.AlbumPagerAdapter;
import com.offsync.posts.data.Post;
import com.offsync.util.DbHelper;
import com.offsync.util.Util;
import com.offsync.view.photoview.PhotoViewAttacher;
import com.viewpagerindicator.CirclePageIndicator;

import java.util.ArrayList;

import butterknife.Bind;
import butterknife.ButterKnife;

public class PostPreviewFragment extends Fragment implements PostPreviewContract.View {

    private static final String ARG_INLINE = "inline";
    private static final String ARG_POST_ID = "post_id";
    private static final String ARG_TYPE = "type";
    private static final String ARG_LINK = "link";
    private static final String ARG_PREVIEW = "preview";
    private static final String ARG_PREVIEW_HEIGHT = "height";
    private static final String ARG_PREVIEW_WIDTH = "width";

    @Bind(R.id.preview_img)
    ImageView mImg;
    @Bind(R.id.preview_video_view)
    VideoView mVideoView;
    @Bind(R.id.preview_progress_bar)
    ProgressBar mProgressBar;
    @Bind(R.id.preview_web_view)
    WebView mWebView;
    @Bind(R.id.preview_youtube_layout)
    View mYouTubeLayout;
    @Bind(R.id.preview_album_pager)
    ViewPager mAlbumPager;
    @Bind(R.id.preview_pager_indicator)
    CirclePageIndicator mPagerIndicator;
    @Bind(R.id.preview_top_shadow)
    View mTopShadow;

    private Boolean mInline;
    private String mPostId;
    private int mType;
    private String mLink;
    private String mPreview;
    private Integer mHeight;
    private Integer mWidth;
    private PhotoViewAttacher mAttacher;
    private PostPreviewPresenter mActionsListener;
    private ArrayList<AlbumImage> mAlbumImages = new ArrayList<>();
    private AlbumPagerAdapter mPagerAdapter;

    public PostPreviewFragment() {
        // Required empty public constructor
    }

    public static PostPreviewFragment newInstance(boolean inline, String postId, int type,
                                                  String link, String preview, int height,
                                                  int width) {
        PostPreviewFragment fragment = new PostPreviewFragment();
        Bundle args = new Bundle();
        args.putBoolean(ARG_INLINE, inline);
        args.putString(ARG_POST_ID, postId);
        args.putInt(ARG_TYPE, type);
        args.putString(ARG_LINK, link);
        args.putString(ARG_PREVIEW, preview);
        args.putInt(ARG_PREVIEW_HEIGHT, height);
        args.putInt(ARG_PREVIEW_WIDTH, width);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mInline = getArguments().getBoolean(ARG_INLINE);
            mPostId = getArguments().getString(ARG_POST_ID);
            mType = getArguments().getInt(ARG_TYPE);
            mLink = getArguments().getString(ARG_LINK);
            mPreview = getArguments().getString(ARG_PREVIEW);
            mHeight = getArguments().getInt(ARG_PREVIEW_HEIGHT);
            mWidth = getArguments().getInt(ARG_PREVIEW_WIDTH);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_post_preview, container, false);
        ButterKnife.bind(this, view);

        initActionsListener();

        if (mType == Post.TYPE_YOUTUBE) {
            initYouTube();
        } else if (mType == Post.TYPE_PHOTO || mType == Post.TYPE_GIF) {
            initImage();
            if (mInline) {
                int height = Util.getScreenWidth() * mHeight / mWidth;
                if (height > Util.getScreenHeight()) {
                    view.getLayoutParams().height = (int) (Util.getScreenHeight()
                            - Util.getToolbarHeight(getActivity()) - Util.getStatusBarHeight()
                            - getResources().getDimension(R.dimen.details_actions_height));
                } else {
                    view.getLayoutParams().height = height;
                }
                view.requestLayout();
            }
        } else if (mType == Post.TYPE_GALLERY) {
            initGallery();
        } else {
            initWebView();
        }
        initTopShadow();

        if (mType == Post.TYPE_GIF || mType == Post.TYPE_GALLERY) {
            view.getLayoutParams().height = Util.getScreenWidth() * mHeight / mWidth;
            view.requestLayout();
        }

        return view;
    }

    private void initTopShadow() {
        if (mInline && (mHeight == 0 || Util.getScreenWidth() * mHeight
                / mWidth > Util.getScreenHeight())) {
            mTopShadow.setVisibility(View.VISIBLE);
        }
    }

    private void initActionsListener() {
        mActionsListener = new PostPreviewPresenter(Util.provideAlbumRepository(), this);
    }

    @SuppressLint("SetJavaScriptEnabled")
    private void initWebView() {
        mWebView.setVisibility(View.VISIBLE);
        mWebView.getSettings().setJavaScriptEnabled(true);
        mWebView.setWebViewClient(new WebViewClient());
        mWebView.setWebChromeClient(new WebChromeClient());

        if (mInline) {
            mWebView.getLayoutParams().height = (int) (Util.getScreenHeight() - Util
                    .getStatusBarHeight() - Util.getToolbarHeight(getActivity()) - getResources()
                    .getDimension(R.dimen.details_actions_height));
            mWebView.requestLayout();
        }

        // Check if website is saved in device memory
        SQLiteDatabase database = new DbHelper(getActivity()).getReadableDatabase();
        Cursor cursor = database.query(DbHelper.POSTS_TABLE, new String[]{DbHelper
                        .POSTS_FILE_STORED_PATH_COLUMN}, DbHelper.POSTS_ID_COLUMN + "=?",
                new String[]{mPostId}, null, null, null);
        if (cursor != null && cursor.moveToFirst()) {
            Util.Log("found (" + mPostId + ")= " + cursor.getString(0));
            // Load saved website
            mWebView.loadUrl(cursor.getString(0));
            cursor.close();
        } else {
            Util.Log("not found = " + mLink);
            // Load website from internet
            mWebView.loadUrl(mLink);
        }
        database.close();
    }

    private void initYouTube() {
        final String videoId = Util.getYouTubeIdFromUrl(mLink);
        mYouTubeLayout.setVisibility(View.VISIBLE);

        YouTubePlayerSupportFragment youTubeFragment = YouTubePlayerSupportFragment.newInstance();
        getActivity().getSupportFragmentManager().beginTransaction()
                .add(R.id.preview_youtube_layout, youTubeFragment).commit();

        youTubeFragment.initialize(getString(R.string.google_api_key), new YouTubePlayer
                .OnInitializedListener() {
            @Override
            public void onInitializationSuccess(YouTubePlayer.Provider provider,
                                                YouTubePlayer youTubePlayer, boolean b) {
                youTubePlayer.cueVideo(videoId);
            }

            @Override
            public void onInitializationFailure(YouTubePlayer.Provider provider,
                                                YouTubeInitializationResult
                                                        youTubeInitializationResult) {
                Util.Log("Error initializing YouTube player: " + youTubeInitializationResult
                        .name());
            }
        });
    }

    private void initImage() {
        mImg.setVisibility(View.VISIBLE);
        mAttacher = new PhotoViewAttacher(mImg);
        mAttacher.setMaximumScale(20);

        if (Util.getScreenWidth() * mHeight / mWidth > Util.getScreenHeight()) {
            mAttacher.setScaleType(ImageView.ScaleType.CENTER_CROP);
        } else {
            mAttacher.setScaleType(ImageView.ScaleType.FIT_CENTER);
        }

        Glide.with(getActivity()).load(mPreview).asBitmap().skipMemoryCache(true)
                .diskCacheStrategy(DiskCacheStrategy.NONE).listener(new RequestListener
                <String, Bitmap>() {
            @Override
            public boolean onException(Exception e, String model, Target<Bitmap> target,
                                       boolean isFirstResource) {
                return false;
            }

            @Override
            public boolean onResourceReady(Bitmap resource, String model, Target<Bitmap> target,
                                           boolean isFromMemoryCache, boolean isFirstResource) {
                if (mType == Post.TYPE_GIF) {
                    initGif();
                }
                mImg.post(new Runnable() {
                    @Override
                    public void run() {
                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                mAttacher.update();
                            }
                        });
                    }
                });
                return false;
            }
        }).into(mImg);
    }

    private void initGif() {
        mImg.setVisibility(View.VISIBLE);

        // Show the progress bar
        mProgressBar.setVisibility(View.VISIBLE);

        Uri video = Uri.parse(Util.formatGifLink(mLink));
        Util.Log("Load video link = " + mLink);
        mVideoView.setVideoURI(video);
        mVideoView.setVisibility(View.VISIBLE);
        mVideoView.start();
        mVideoView.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {

            @Override
            public void onPrepared(MediaPlayer mp) {
                mp.setLooping(true);
                mp.setOnInfoListener(new MediaPlayer.OnInfoListener() {
                    @Override
                    public boolean onInfo(MediaPlayer mp, int what, int extra) {
                        if (what == MediaPlayer.MEDIA_INFO_VIDEO_RENDERING_START) {
                            // video started; hide the placeholder.
                            mProgressBar.setVisibility(View.GONE);
                            mImg.setVisibility(View.INVISIBLE);
                            return true;
                        }
                        return false;
                    }
                });
                mp.setOnVideoSizeChangedListener(new MediaPlayer.OnVideoSizeChangedListener() {
                    @Override
                    public void onVideoSizeChanged(MediaPlayer mp, int width, int height) {
                        MediaController mediaController = new MediaController(getActivity());
                        mVideoView.setMediaController(mediaController);
                        mediaController.setAnchorView(mVideoView);
                    }
                });
            }
        });
    }

    private void initGallery() {
        mAlbumPager.setVisibility(View.VISIBLE);
        mPagerIndicator.setVisibility(View.VISIBLE);

        mPagerAdapter = new AlbumPagerAdapter(getActivity().getSupportFragmentManager(),
                mAlbumImages);
        mAlbumPager.setAdapter(mPagerAdapter);

        mPagerIndicator.setViewPager(mAlbumPager);

        String id = Util.parseImgurId(mLink);
        Util.Log("Imgur album id = " + id);
        mActionsListener.loadAlbumImages(id);
    }

    @Override
    public void onDestroy() {
        if (mType == Post.TYPE_GIF) {
            mImg.setVisibility(View.VISIBLE);
            mVideoView.setZOrderOnTop(false);
            mVideoView.stopPlayback();
            mVideoView.setVisibility(View.GONE);
        }
        super.onDestroy();
    }

    @Override
    public void showAlbumImages(ArrayList<AlbumImage> albumImages) {
        mAlbumImages.clear();
        mAlbumImages.addAll(albumImages);
        mPagerAdapter.notifyDataSetChanged();
    }

    @Override
    public void showError(String message) {
        Toast.makeText(getActivity(), message, Toast.LENGTH_LONG).show();
    }
}
