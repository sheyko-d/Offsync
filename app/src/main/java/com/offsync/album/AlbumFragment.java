package com.offsync.album;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.Html;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.offsync.R;
import com.offsync.util.Util;
import com.offsync.view.photoview.PhotoViewAttacher;

import butterknife.Bind;
import butterknife.ButterKnife;

public class AlbumFragment extends Fragment {

    private static final String ARG_TITLE = "title";
    private static final String ARG_DESCRIPTION = "type";
    private static final String ARG_LINK = "link";
    private static final String ARG_PREVIEW_HEIGHT = "height";
    private static final String ARG_PREVIEW_WIDTH = "width";

    @Bind(R.id.album_img)
    ImageView mImg;
    @Bind(R.id.album_txt)
    TextView mTxt;
    @Bind(R.id.album_scroll_view)
    View mScrollView;

    private String mTitle;
    private String mDescription;
    private String mLink;
    private int mHeight;
    private int mWidth;

    public AlbumFragment() {
        // Required empty public constructor
    }

    public static AlbumFragment newInstance(String title, String description, String link,
                                            int height, int width) {
        AlbumFragment fragment = new AlbumFragment();
        Bundle args = new Bundle();
        args.putString(ARG_TITLE, title);
        args.putString(ARG_DESCRIPTION, description);
        args.putString(ARG_LINK, link);
        args.putInt(ARG_PREVIEW_HEIGHT, height);
        args.putInt(ARG_PREVIEW_WIDTH, width);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mTitle = getArguments().getString(ARG_TITLE);
            mDescription = getArguments().getString(ARG_DESCRIPTION);
            mLink = getArguments().getString(ARG_LINK);
            mHeight = getArguments().getInt(ARG_PREVIEW_HEIGHT);
            mWidth = getArguments().getInt(ARG_PREVIEW_WIDTH);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_album, container, false);
        ButterKnife.bind(this, view);

        initImage();
        initDescription();

        return view;
    }

    private void initImage() {
        final PhotoViewAttacher attacher = new PhotoViewAttacher(mImg);
        attacher.setMaximumScale(20);
        attacher.setScaleType(ImageView.ScaleType.CENTER_CROP);
        Glide.with(getContext()).load(mLink).error(R.color.primary).listener(new RequestListener
                <String, GlideDrawable>() {
            @Override
            public boolean onException(Exception e, String model, Target<GlideDrawable> target,
                                       boolean isFirstResource) {
                return false;
            }

            @Override
            public boolean onResourceReady(GlideDrawable resource, String model,
                                           Target<GlideDrawable> target, boolean isFromMemoryCache,
                                           boolean isFirstResource) {
                mImg.post(new Runnable() {
                    @Override
                    public void run() {
                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                attacher.update();
                            }
                        });
                    }
                });
                return false;
            }
        }).into(mImg);
    }

    private void initDescription() {
        Util.Log("mDescription = "+mDescription);
        mTxt.setText(Html.fromHtml(mDescription));
        mTxt.setVisibility(TextUtils.isEmpty(mDescription) || mDescription.equals("null")
                ? View.GONE : View.VISIBLE);
        mScrollView.setVisibility(TextUtils.isEmpty(mDescription) || mDescription.equals("null")
                ? View.GONE : View.VISIBLE);
    }
}
