package com.offsync.posts;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.offsync.R;
import com.offsync.postdetails.PostDetailsActivity;
import com.offsync.postpreview.PostPreviewActivity;
import com.offsync.posts.adapter.PostsAdapter;
import com.offsync.posts.data.Post;
import com.offsync.util.EndlessRecyclerViewScrollListener;
import com.offsync.util.Util;

import java.util.ArrayList;

import butterknife.Bind;
import butterknife.ButterKnife;

public class PostsFragment extends Fragment implements PostsContract.View {
    private static final String ARG_SUBREDDIT = "param1";

    @Bind(R.id.posts_recycler)
    RecyclerView mRecycler;
    @Bind(R.id.posts_more_progress_bar)
    ProgressBar mMoreProgressBar;

    private String mSubreddit;
    private PostsPresenter mActionsListener;
    private ArrayList<Post> mPosts = new ArrayList<>();
    private PostsAdapter mAdapter;

    public PostsFragment() {
        // Required empty public constructor
    }

    public static PostsFragment newInstance(String subreddit) {
        PostsFragment fragment = new PostsFragment();
        Bundle args = new Bundle();
        args.putString(ARG_SUBREDDIT, subreddit);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mSubreddit = getArguments().getString(ARG_SUBREDDIT);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_posts, container, false);
        ButterKnife.bind(this, view);

        initActionsListener();
        initRecycler();
        loadPosts();

        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        getActivity().findViewById(R.id.main_fab).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mActionsListener.savePosts(mPosts);
            }
        });
    }

    private void initRecycler() {
        mRecycler.setHasFixedSize(false);
        GridLayoutManager layoutManager = new GridLayoutManager(getActivity(), getResources()
                .getInteger(R.integer.column_num));
        mRecycler.setLayoutManager(layoutManager);
        mAdapter = new PostsAdapter(getActivity(), mPosts, mActionsListener);
        mRecycler.setAdapter(mAdapter);
        mRecycler.addOnScrollListener(new EndlessRecyclerViewScrollListener(layoutManager) {

            @Override
            public void onLoadMore(int page, int totalItemsCount) {
                mActionsListener.loadMorePosts(mSubreddit);
            }
        });
    }

    private void loadPosts() {
        mActionsListener.loadPosts(mSubreddit);
    }

    private void initActionsListener() {
        mActionsListener = new PostsPresenter(Util.providePostsRepository(), this);
    }

    @Override
    public void showPosts(ArrayList<Post> posts) {
        mPosts.clear();
        mPosts.addAll(posts);
        mAdapter.notifyDataSetChanged();
    }

    @Override
    public void showError(String message) {
        Toast.makeText(getActivity(), message, Toast.LENGTH_LONG).show();
    }

    @Override
    public void setProgressBar(boolean visible) {

    }

    @Override
    public void setMoreProgressBar(boolean visible) {
        mMoreProgressBar.setVisibility(visible ? View.VISIBLE : View.GONE);
    }

    @Override
    public void openPostFullscreen(View view, Post post) {
        try {
            Intent intent = new Intent(getActivity(), PostPreviewActivity.class)
                    .putExtra(PostPreviewActivity.EXTRA_POST_ID, post.getId())
                    .putExtra(PostPreviewActivity.EXTRA_TYPE, post.getType())
                    .putExtra(PostPreviewActivity.EXTRA_LINK, post.getLink());
            if (post.getImages().size() > 0) {
                intent.putExtra(PostPreviewActivity.EXTRA_PREVIEW, post.getImages().get(0)
                        .getUrl()).putExtra(PostPreviewActivity.EXTRA_PREVIEW_HEIGHT, post
                        .getImages().get(0).getHeight()).putExtra(PostPreviewActivity
                        .EXTRA_PREVIEW_WIDTH, post.getImages().get(0).getWidth());

            }
            startActivity(intent);
        } catch (Exception e) {
            Util.Log("exception = " + e);
        }
    }

    @Override
    public void openPostDetails(View view, Post post) {
        Intent intent = new Intent(getActivity(), PostDetailsActivity.class)
                .putExtra(PostDetailsActivity.EXTRA_POST_ID, post.getId())
                .putExtra(PostDetailsActivity.EXTRA_SUBREDDIT, post.getSubreddit());
        startActivity(intent);
    }

    @Override
    public void markPostAsSavedAt(int pos) {
        mPosts.get(pos).setSavedStatus(Post.SAVED_STATUS_DOWNLOADED);
        mAdapter.notifyItemChanged(pos);
    }

    @Override
    public void markPostAsFailedAt(int pos) {
        mPosts.get(pos).setSavedStatus(Post.SAVED_STATUS_FAILED);
    }

    @Override
    public void setUpvoted(int pos, boolean upvoted) {
        mPosts.get(pos).setUpvoted(upvoted);
        mAdapter.notifyItemChanged(pos);
    }

    @Override
    public void setDownvoted(int pos, boolean downvoted) {
        mPosts.get(pos).setDownvoted(downvoted);
        mAdapter.notifyItemChanged(pos);
    }
}
