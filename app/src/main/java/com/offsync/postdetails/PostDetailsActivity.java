package com.offsync.postdetails;

import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomSheetBehavior;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.text.TextUtils;
import android.view.Menu;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.offsync.R;
import com.offsync.comments.adapter.CommentsAdapter;
import com.offsync.comments.data.Comment;
import com.offsync.postpreview.PostPreviewFragment;
import com.offsync.posts.data.Post;
import com.offsync.util.Util;

import java.util.ArrayList;

import butterknife.Bind;
import butterknife.ButterKnife;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

public class PostDetailsActivity extends AppCompatActivity implements PostDetailsContract.View {

    public static final String EXTRA_POST_ID = "post_id";
    public static final String EXTRA_SUBREDDIT = "subreddit";
    private static final int YOUTUBE_PLAYER_HEIGHT = Util.convertDpToPixel(230);

    @Bind(R.id.details_comments_recycler)
    RecyclerView mCommentsRecycler;
    @Bind(R.id.details_header_layout)
    View mHeaderLayout;
    @Bind(R.id.details_footer_layout)
    View mFooterLayout;
    @Bind(R.id.details_comments_progress_bar)
    ProgressBar mCommentsProgressBar;
    @Bind(R.id.details_title_txt)
    TextView mTitleTxt;
    @Bind(R.id.details_body_txt)
    TextView mBodyTxt;
    @Bind(R.id.details_txt_layout)
    View mTxtLayout;
    @Bind(R.id.details_shade_view)
    View mShadeView;
    @Bind(R.id.details_comments_txt)
    TextView mCommentsTxt;
    @Bind(R.id.details_fab)
    FloatingActionButton mFab;
    @Bind(R.id.details_shadow_bottom_view)
    View mShadowBottomView;

    private PostDetailsPresenter mActionsListener;
    private ArrayList<Comment> mComments = new ArrayList<>();
    private CommentsAdapter mAdapter;
    private Post mPost;
    private String mPostId;
    private String mLink;
    private int mType;
    private String mPreview;
    private int mHeight;
    private int mWidth;
    private Integer mHeaderHeight;
    private int mClickedImageHeight = /*TODO: Get real image height*/-1;
    private String mSubreddit /*TODO: */;
    private LinearLayoutManager mLayoutManager;
    private BottomSheetBehavior<View> mBottomSheetBehavior;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post_details);
        ButterKnife.bind(this);

        initActionsListener();
        initFab();
    }

    private void initFab() {
        mFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mActionsListener.scrollToNextComment();
            }
        });
        mFab.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                mActionsListener.scrollToNextBranch();
                return true;
            }
        });
    }

    private void initPreview() {
        if (mType != Post.TYPE_TEXT) {
            Fragment fragment = PostPreviewFragment.newInstance(true, mPostId, mType, mLink,
                    mPreview, mHeight, mWidth);
            getSupportFragmentManager().beginTransaction().add(R.id.details_preview_layout,
                    fragment).commit();
        } else {
            mTxtLayout.setVisibility(View.VISIBLE);
            mTitleTxt.setText(mPost.getTitle());
            mBodyTxt.setVisibility(TextUtils.isEmpty(mPost.getText()) || mPost.getText().equals
                    ("null") ? View.GONE : View.VISIBLE);
            mBodyTxt.setText(Html.fromHtml(mPost.getText()));
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                mFooterLayout.setElevation(Util.convertDpToPixel(4));
                mShadowBottomView.setVisibility(View.GONE);
            }
        }
    }

    private void initBottomSheet() {
        mBottomSheetBehavior = BottomSheetBehavior.from(mFooterLayout);
        mHeaderHeight = null;

        if (mPost.getImages().size() > 0) {
            mClickedImageHeight = Util.getScreenWidth() * mPost.getImages().get(0).getHeight()
                    / mPost.getImages().get(0).getWidth();
        }

        boolean resizeHeader = false;
        if (mType == Post.TYPE_PHOTO || mType == Post.TYPE_GIF || mType == Post.TYPE_GALLERY) {
            Util.Log("mHeaderHeight1");
            int peekHeight = Util.getScreenHeight() - mClickedImageHeight
                    - Util.getToolbarHeight(this) - Util.getStatusBarHeight();
            if (peekHeight > (int)
                    getResources().getDimension
                            (R.dimen.details_actions_height)) {
                mBottomSheetBehavior.setPeekHeight(peekHeight);
                mHeaderHeight = mClickedImageHeight;
                resizeHeader = true;
                Util.Log("mHeaderHeight2");
            } else {
                mBottomSheetBehavior.setPeekHeight((int) getResources().getDimension
                        (R.dimen.details_actions_height));
            }
        } else if (mType == Post.TYPE_YOUTUBE) {
            int peekHeight = Util.getScreenHeight() - YOUTUBE_PLAYER_HEIGHT
                    - Util.getToolbarHeight(this) - Util.getStatusBarHeight();
            mBottomSheetBehavior.setPeekHeight(peekHeight);

            mHeaderHeight = YOUTUBE_PLAYER_HEIGHT;
            resizeHeader = true;
        } else if (mType == Post.TYPE_TEXT) {
            mTxtLayout.measure(View.MeasureSpec.makeMeasureSpec(Util.getScreenWidth(), View.MeasureSpec.AT_MOST),
                    View.MeasureSpec.makeMeasureSpec(Util.getScreenHeight(), View.MeasureSpec.AT_MOST));
            int peekHeight = Util.getScreenHeight() - mTxtLayout.getMeasuredHeight()
                    - Util.getToolbarHeight(this) - Util.getStatusBarHeight();

            mHeaderHeight = mTxtLayout.getMeasuredHeight();

            if (peekHeight > (int)
                    getResources().getDimension
                            (R.dimen.details_actions_height)) {
                mBottomSheetBehavior.setPeekHeight(peekHeight);
                mHeaderHeight = mTxtLayout.getMeasuredHeight();
                resizeHeader = true;
            } else {
                mBottomSheetBehavior.setPeekHeight((int) getResources().getDimension
                        (R.dimen.details_actions_height));
            }
        } else if (mType == Post.TYPE_WEBSITE) {
            mBottomSheetBehavior.setPeekHeight((int) getResources().getDimension
                    (R.dimen.details_actions_height));
        }

        Util.Log("mHeaderHeight = " + mHeaderHeight);

        if (mHeaderHeight == null) {
            mHeaderHeight = Util.getScreenHeight() - Util.getToolbarHeight(this)
                    - Util.getStatusBarHeight();
        }

        if (resizeHeader) {
            mHeaderLayout.getLayoutParams().height = mHeaderHeight;
            mHeaderLayout.requestLayout();
        }

        mBottomSheetBehavior.setBottomSheetCallback(new BottomSheetBehavior.BottomSheetCallback() {
            @Override
            public void onStateChanged(@NonNull View bottomSheet, int newState) {
            }

            @Override
            public void onSlide(@NonNull View bottomSheet, float slideOffset) {
                float translationY = -mHeaderHeight * slideOffset;

                // Achieve the parallax effect on the header when the bottom sheet moves
                mHeaderLayout.setTranslationY(translationY / 2f);
                mShadeView.setAlpha(slideOffset);
            }
        });
    }

    private void initComments() {
        mCommentsRecycler.setHasFixedSize(true);
        mCommentsRecycler.setItemAnimator(new DefaultItemAnimator());
        mLayoutManager = new LinearLayoutManager(this);
        mCommentsRecycler.setLayoutManager(mLayoutManager);
        mAdapter = new CommentsAdapter(this, mComments, mActionsListener);
        mCommentsRecycler.setAdapter(mAdapter);
    }

    private void initActionsListener() {
        mActionsListener = new PostDetailsPresenter(Util.provideCommentsRepository(), this);
        mActionsListener.loadComments(getIntent().getStringExtra(EXTRA_POST_ID), getIntent()
                .getStringExtra(EXTRA_SUBREDDIT));
    }

    private void initActionBar() {
        /*TODO: Remove if not needed
        setSupportActionBar(mToolbar);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayShowCustomEnabled(true);
            actionBar.setCustomView(R.layout.view_details_toolbar);
        }*/
    }

    private void initContent() {
        mPostId = getIntent().getStringExtra(EXTRA_POST_ID);
    }

    private void initText() {
        /*mBodyTxt.setVisibility(View.VISIBLE);
        String body = getIntent().getStringExtra(EXTRA_BODY);
        mHeaderLayout.setVisibility(TextUtils.isEmpty(body) ? View.GONE
                : View.VISIBLE);
        mBodyTxt.setText(body);*/
    }

    private void initImage() {
        /*mImg.setImageDrawable(PostsFragment.clickedImageDrawable);
        mImg.setVisibility(View.VISIBLE);

        ImageLoader imageLoader = ImageLoader.getInstance();
        DisplayImageOptions options = new DisplayImageOptions.Builder()
                .cacheInMemory(true)
                .cacheOnDisk(false)
                .imageScaleType(ImageScaleType.EXACTLY)
                .bitmapConfig(Bitmap.Config.RGB_565)
                .delayBeforeLoading(getResources().getInteger
                        (android.R.integer.config_mediumAnimTime))
                .build();
        ImageLoaderConfiguration configuration = new ImageLoaderConfiguration
                .Builder(this)
                .defaultDisplayImageOptions(options).build();
        imageLoader.init(configuration);
        Util.Log("height before = " + mImg.getHeight());
        ImageLoader.getInstance().displayImage(getIntent().getStringExtra
                (EXTRA_PREVIEW), mImg, options, new ImageLoadingListener() {
            @Override
            public void onLoadingStarted(String imageUri, View view) {

            }

            @Override
            public void onLoadingFailed(String imageUri, View view, FailReason failReason) {

            }

            @Override
            public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
            }

            @Override
            public void onLoadingCancelled(String imageUri, View view) {

            }
        });*/
    }

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }

    @Override
    public void showComments(ArrayList<Comment> comments) {
        mComments.clear();
        mComments.addAll(comments);
        mAdapter.notifyDataSetChanged();
    }

    @Override
    public void showPost(Post post) {
        mPost = post;
        mType = post.getType();
        mLink = post.getLink();
        if (post.getImages().size() > 0) {
            mPreview = post.getImages().get(0).getUrl();
            mHeight = post.getImages().get(0).getHeight();
            mWidth = post.getImages().get(0).getWidth();
        }

        initActionBar();
        initContent();
        initPreview();
        initBottomSheet();
        initComments();
        initDetailsTxt();
    }

    private void initDetailsTxt() {
        mCommentsTxt.setText(getString(R.string.posts_details, mPost.getCommentsCount(),
                Util.formatTime(mPost.getTimestamp()), mPost.getAuthor()));
    }

    @Override
    public void showError(String message) {
        Toast.makeText(this, message, Toast.LENGTH_LONG).show();
        Util.Log("Comments error: " + message);
    }

    @Override
    public void setProgressBar(boolean visible) {
        mCommentsProgressBar.setVisibility(visible ? View.VISIBLE : View.INVISIBLE);
    }

    @Override
    public void scrollToNextComment() {
        int scrollPos = mLayoutManager.findFirstVisibleItemPosition() + 1;
        while (mComments.get(scrollPos).getType() == Comment.TYPE_MORE && scrollPos <
                mComments.size() - 1) {
            scrollPos++;
        }
        mLayoutManager.scrollToPositionWithOffset(scrollPos, 0);
    }

    @Override
    public void scrollToNextBranch() {
        int scrollPos = mLayoutManager.findFirstVisibleItemPosition() + 1;
        while (mComments.get(scrollPos).getDepth() > 0 && scrollPos < mComments.size() - 1) {
            scrollPos++;
        }
        mLayoutManager.scrollToPositionWithOffset(scrollPos, 0);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.post_details_menu, menu);
        return true;
    }
}
