package com.offsync.login;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.os.AsyncTaskCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.offsync.R;
import com.offsync.posts.PostsActivity;
import com.offsync.util.Util;

import net.dean.jraw.RedditClient;
import net.dean.jraw.http.LoggingMode;
import net.dean.jraw.http.NetworkException;
import net.dean.jraw.http.UserAgent;
import net.dean.jraw.http.oauth.Credentials;
import net.dean.jraw.http.oauth.OAuthData;
import net.dean.jraw.http.oauth.OAuthException;
import net.dean.jraw.http.oauth.OAuthHelper;

import butterknife.Bind;
import butterknife.ButterKnife;

public class LoginActivity extends AppCompatActivity {

    @Bind(R.id.login_web_view)
    WebView mWebView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        ButterKnife.bind(this);

        AsyncTaskCompat.executeParallel(new AsyncTask<Void, Void, String>() {
            public RedditClient mRedditClient;
            public OAuthHelper mOAuthHelper;
            public Credentials mCredentials;

            @Override
            protected String doInBackground(Void... params) {
                UserAgent myUserAgent = UserAgent.of("Offsync 1.0");
                mRedditClient = new RedditClient(myUserAgent);
                mRedditClient.setLoggingMode(LoggingMode.ALWAYS);

                mOAuthHelper = mRedditClient.getOAuthHelper();
                // This is Android, so our OAuth app should be an installed app.
                mCredentials = Credentials.installedApp(getString
                        (R.string.reddit_key), getString(R.string.reddit_redirect_url));

                /*TODO:// Try to refresh the token
                if(!mRefreshToken.isEmpty()) {
                    new TokenRefreshTask().execute();
                    return;
                }*/

                // If this is true, then you will be able to refresh to access token
                boolean permanent = true;

                // OAuth2 scopes to request. See https://www.reddit.com/dev/api/oauth for a full list
                String[] scopes = {"identity", "read", "vote", "save", "mysubreddits"};

                String authorizationUrl = mOAuthHelper.getAuthorizationUrl(mCredentials, permanent,
                        scopes)
                        .toExternalForm();
                authorizationUrl = authorizationUrl.replace("www.", "i.");
                Util.Log("Auth URL: " + authorizationUrl);

                //Util.clearCookies();

                return authorizationUrl;
            }

            @Override
            protected void onPostExecute(String authorizationUrl) {
                mWebView.loadUrl(authorizationUrl);
                mWebView.setWebChromeClient(new WebChromeClient());

                mWebView.setWebViewClient(new WebViewClient() {
                    @Override
                    public void onPageStarted(WebView view, String url, Bitmap favicon) {
                        if (url.contains("code=")) {
                            mWebView.setVisibility(View.GONE);

                            Util.Log("WebView URL: " + url);
                            // We've detected the redirect URL
                            new UserChallengeTask(mOAuthHelper, mCredentials).execute(url);
                        }
                    }
                });
            }

            final class UserChallengeTask extends AsyncTask<String, Void, OAuthData> {

                private OAuthHelper mOAuthHelper;
                private Credentials mCredentials;

                public UserChallengeTask(OAuthHelper oAuthHelper, Credentials credentials) {
                    Util.Log("UserChallengeTask()");
                    mOAuthHelper = oAuthHelper;
                    mCredentials = credentials;
                }

                @Override
                protected OAuthData doInBackground(String... params) {
                    Util.Log("doInBackground()");
                    Util.Log("params[0]: " + params[0]);
                    OAuthData oAuthData = null;
                    try {
                        oAuthData = mOAuthHelper.onUserChallenge(params[0], mCredentials);
                    } catch (IllegalStateException | NetworkException | OAuthException e) {
                        // Handle me gracefully
                        Util.Log("OAuth failed");
                        Util.Log(e.getMessage());
                    }
                    Util.Log("onPostExecute()");


                    if (oAuthData != null) {
                        mRedditClient.authenticate(oAuthData);
                        Util.Log("Reddit client authentication: " + mRedditClient
                                .isAuthenticated());
                        Util.Log("LOGGED IN = " + mRedditClient.getOAuthData().getAccessToken()
                                + ", " + mRedditClient.getOAuthData().getRefreshToken() + ", " + mRedditClient.getOAuthData().getExpirationDate());
                        //TODO: Save refresh token:
                        String refreshToken = mRedditClient.getOAuthData().getAccessToken();
                        Util.setToken(refreshToken);
                    } else {
                        Util.Log("Passed in OAuthData was null");
                    }
                    return null;
                }

                @Override
                protected void onPostExecute(OAuthData oAuthData) {
                    startActivity(new Intent(LoginActivity.this, PostsActivity.class));
                }
            }
        });
    }
}
