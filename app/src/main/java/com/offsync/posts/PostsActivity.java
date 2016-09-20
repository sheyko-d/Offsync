package com.offsync.posts;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.os.AsyncTaskCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.View;

import com.github.jorgecastilloprz.FABProgressCircle;
import com.offsync.R;
import com.offsync.login.LoginActivity;
import com.offsync.subreddits.SubredditsContract;
import com.offsync.subreddits.SubredditsPresenter;
import com.offsync.subreddits.data.Subreddit;
import com.offsync.util.Util;

import net.dean.jraw.RedditClient;

import java.util.ArrayList;

import butterknife.Bind;
import butterknife.ButterKnife;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

public class PostsActivity extends AppCompatActivity implements SubredditsContract.View {

    @Bind(R.id.main_toolbar)
    Toolbar mToolbar;
    @Bind(R.id.main_drawer_layout)
    DrawerLayout mDrawerLayout;
    @Bind(R.id.main_view_pager)
    ViewPager mViewPager;
    @Bind(R.id.main_tab_layout)
    TabLayout mTabLayout;
    @Bind(R.id.main_fab)
    FloatingActionButton mFab;
    @Bind(R.id.main_fab_progress)
    FABProgressCircle mFabProgress;

    public static PostsActivity activity;
    private String mSubreddit = "";
    private RedditClient mRedditClient;
    private SubredditsPresenter mActionsListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTheme(R.style.AppTheme_Light);
        setContentView(R.layout.activity_posts);
        ButterKnife.bind(this);

        activity = this;

        initActionsListener();
        initActionBar();
        initDrawer();
        initViewPager();
        initTabs();
        initSubreddits();

        if (Util.getToken() != null) {
            AsyncTaskCompat.executeParallel(new AsyncTask<Void, Void, Void>() {
                @Override
                protected Void doInBackground(Void... params) {
                    /*mRedditClient = new RedditClient(UserAgent.of("JrawTest 0.1"));
                    mRedditClient.setLoggingMode(LoggingMode.ALWAYS);
                    final Credentials credentials = Credentials.installedApp(getString
                            (R.string.reddit_key), getString(R.string.reddit_redirect_url));
                    OAuthHelper oAuthHelper = mRedditClient.getOAuthHelper();
                    oAuthHelper.setRefreshToken(Util.getToken());
                    try {
                        OAuthData finalData = oAuthHelper.refreshToken(credentials);
                        mRedditClient.authenticate(finalData);
                        if (mRedditClient.isAuthenticated()) {
                            Util.Log("Authenticated posts");
                        }
                    } catch (OAuthException e) {
                        e.printStackTrace();
                    }*/
                    return null;
                }
            });
        }
    }

    private void initActionsListener() {
        mActionsListener = new SubredditsPresenter(Util.provideSubredditsRepository(), this);
    }

    private void initSubreddits() {
        mActionsListener.loadSubreddits();
    }

    private void initViewPager() {
        mViewPager.setAdapter(new MyPagerAdapter(getSupportFragmentManager()));
    }

    private void initTabs() {
        mTabLayout.setupWithViewPager(mViewPager);
    }

    private void initActionBar() {
        mToolbar.setContentInsetsAbsolute(Util.convertDpToPixel(72), 0);
        setSupportActionBar(mToolbar);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayShowHomeEnabled(true);
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
    }

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }

    private void initDrawer() {
        ActionBarDrawerToggle actionBarDrawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout,
                mToolbar, R.string.app_name, R.string.app_name) {
            @Override
            public void onDrawerClosed(View drawerView) {
                super.onDrawerClosed(drawerView);
            }

            @Override
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
            }
        };
        mDrawerLayout.addDrawerListener(actionBarDrawerToggle);
        actionBarDrawerToggle.syncState();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.posts_menu, menu);
        return true;
    }

    public void onLoginClicked(View view) {
        startActivity(new Intent(this, LoginActivity.class));
    }

    @Override
    public void showSubreddits(ArrayList<Subreddit> subreddits) {
        Util.Log("Loaded subreddits: "+subreddits.size());
    }

    @Override
    public void showError(String message) {

    }

    public class MyPagerAdapter extends FragmentPagerAdapter {

        private final String[] mPageTitles = getResources().getStringArray
                (R.array.posts_page_titles);

        public MyPagerAdapter(FragmentManager fragmentManager) {
            super(fragmentManager);
        }

        // Returns total number of pages
        @Override
        public int getCount() {
            return /*TODO: mPageTitles.length*/1;
        }

        // Returns the fragment to display for that page
        @Override
        public Fragment getItem(int position) {
            switch (position) {
                case 0: // Fragment # 0 - This will show FirstFragment
                    return PostsFragment.newInstance(mSubreddit);
                default:
                    return PostsFragment.newInstance(mSubreddit);
            }
        }

        // Returns the page title for the top indicator
        @Override
        public CharSequence getPageTitle(int position) {
            return mPageTitles[position];
        }

    }

}
