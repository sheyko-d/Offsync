package com.offsync.posts.adapter;

import android.content.Context;
import android.graphics.PorterDuff;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.offsync.R;
import com.offsync.posts.PostsPresenter;
import com.offsync.posts.data.Post;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import butterknife.Bind;
import butterknife.ButterKnife;
import jp.wasabeef.picasso.transformations.BlurTransformation;

public class PostsAdapter extends RecyclerView.Adapter<PostsAdapter.ViewHolder> {

    private final Context mContext;
    private final PostsPresenter mActionsListener;
    private ArrayList<Post> mPosts;

    public PostsAdapter(Context context, ArrayList<Post> posts,
                        PostsPresenter actionsListener) {
        mContext = context;
        mPosts = posts;
        mActionsListener = actionsListener;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View drawerItemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_post2,
                parent, false);
        return new ViewHolder(drawerItemView);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        Post post = mPosts.get(position);
        holder.scoreTxt.setText(String.valueOf(post.getScore()));
        holder.titleTxt.setText(Html.fromHtml(post.getTitle()));
        holder.sourceTxt.setText(mContext.getString(R.string.posts_source, post.getSubreddit()));

        holder.commentsTxt.setText(mContext.getString(R.string.posts_info, post
                .getCommentsCount()));

        if (!post.getImages().isEmpty()) {
            if (post.isNsfl()) {
                Picasso.with(mContext).load(post.getImages().get(0).getUrl()).transform
                        (new BlurTransformation(mContext, 30)).placeholder
                        (R.color.photo_placeholder).fit().centerCrop().into(post.getType()
                        == Post.TYPE_WEBSITE ? holder.websiteImg : holder.img);
            } else {
                /*holder.img.setImageDrawable(null);
                mImageLoader.displayImage(post.getImages().get(0), holder.img, mOptions);*/

                Glide.with(mContext).load(post.getImages().get(0).getUrl()).asBitmap()
                        .skipMemoryCache(true)
                        .diskCacheStrategy(DiskCacheStrategy.NONE)
                        .animate(R.anim.fade_in)
                        .placeholder(R.color.photo_placeholder)
                        .into(post.getType() == Post.TYPE_WEBSITE ? holder.websiteImg : holder.img);
            }
        }

        ((View) holder.img.getParent()).setVisibility(post.getImages().isEmpty() || (post.getType()
                == Post.TYPE_WEBSITE || post.getType() == Post.TYPE_TEXT) ? View.GONE
                : View.VISIBLE);
        holder.img.setClickable(post.getType() != Post.TYPE_WEBSITE);

        holder.websiteImg.setVisibility(post.getImages().isEmpty()
                || post.getType() != Post.TYPE_WEBSITE ? View.GONE : View.VISIBLE);

        holder.nsfwTxt.setVisibility(post.isNsfw() ? View.VISIBLE : View.GONE);

        if (post.getType() == Post.TYPE_GIF) {
            holder.typeImg.setImageResource(R.drawable.type_gif);
            holder.typeImg.setBackgroundResource(R.drawable.type_gif_bg);
        } else if (post.getType() == Post.TYPE_GALLERY) {
            holder.typeImg.setImageResource(R.drawable.type_slideshow);
            holder.typeImg.setBackgroundResource(R.drawable.type_slideshow_bg);
        } else if (post.getType() == Post.TYPE_YOUTUBE) {
            holder.typeImg.setImageResource(R.drawable.type_video);
            holder.typeImg.setBackgroundResource(R.drawable.type_video_bg);
        }
        // Don't show type badge for text and photo posts
        holder.typeImg.setVisibility((post.getType() == Post.TYPE_WEBSITE || post.getType()
                == Post.TYPE_TEXT || post.getType() == Post.TYPE_PHOTO) ? View.GONE : View.VISIBLE);

        if (!TextUtils.isEmpty(post.getFlair())) {
            holder.flairTxt.setText(Html.fromHtml(post.getFlair()));
            holder.flairTxt.setVisibility(View.VISIBLE);
        } else {
            holder.flairTxt.setVisibility(View.GONE);
        }

        holder.titleTxt.setTextColor(ContextCompat.getColor(mContext, post.isStickied()
                ? R.color.green : R.color.primary_text));
        holder.downloadedImg.setVisibility(post.getDownloadedStatus() == Post.SAVED_STATUS_DEFAULT
                ? View.GONE : View.VISIBLE);

        holder.gildedLayout.setVisibility(post.getGildedCount() > 0 ? View.VISIBLE : View.GONE);
        holder.gildedTxt.setVisibility(post.getGildedCount() > 1 ? View.VISIBLE : View.GONE);
        holder.gildedTxt.setText(String.valueOf(post.getGildedCount()));

        // Init website layout
        holder.websiteLayout.setVisibility(post.getType() == Post.TYPE_WEBSITE ? View.VISIBLE
                : View.GONE);
        holder.websiteTxt.setText(post.getDomain());

        // Set body text
        holder.bodyTxt.setText(Html.fromHtml(post.getText()));
        holder.bodyTxt.setVisibility(TextUtils.isEmpty(post.getText()) || post.getText()
                .equals("null") || post.getTitle().equals(post.getText()) ? View.GONE
                : View.VISIBLE);

        // Set the vote state
        holder.upvoteBtn.getDrawable().setColorFilter(ContextCompat.getColor(mContext, post
                .isUpvoted() ? R.color.upvoted : R.color.secondary_text), PorterDuff.Mode.SRC_ATOP);
        holder.downvoteBtn.getDrawable().setColorFilter(ContextCompat.getColor(mContext, post
                .isDownvoted() ? R.color.downvoted : R.color.secondary_text),
                PorterDuff.Mode.SRC_ATOP);
    }

    @Override
    public int getItemCount() {
        return mPosts == null ? 0 : mPosts.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        @Bind(R.id.post_title_txt)
        TextView titleTxt;
        @Bind(R.id.post_score_txt)
        TextView scoreTxt;
        @Bind(R.id.post_source_txt)
        TextView sourceTxt;
        @Bind(R.id.post_comments_txt)
        TextView commentsTxt;
        @Bind(R.id.post_img)
        ImageView img;
        @Bind(R.id.post_nsfw_txt)
        TextView nsfwTxt;
        @Bind(R.id.post_type_img)
        ImageView typeImg;
        @Bind(R.id.post_flair_txt)
        TextView flairTxt;
        @Bind(R.id.post_downloaded_img)
        ImageView downloadedImg;
        @Bind(R.id.post_gilded_layout)
        View gildedLayout;
        @Bind(R.id.post_gilded_txt)
        TextView gildedTxt;
        @Bind(R.id.post_website_layout)
        View websiteLayout;
        @Bind(R.id.post_website_img)
        ImageView websiteImg;
        @Bind(R.id.post_website_txt)
        TextView websiteTxt;
        @Bind(R.id.post_body_txt)
        TextView bodyTxt;
        @Bind(R.id.post_content_layout)
        View contentLayout;
        @Bind(R.id.post_upvote_btn)
        ImageButton upvoteBtn;
        @Bind(R.id.post_downvote_btn)
        ImageButton downvoteBtn;

        public ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);

            img.setOnClickListener(this);
            contentLayout.setOnClickListener(this);
            upvoteBtn.setOnClickListener(this);
            downvoteBtn.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            Post post = mPosts.get(getAdapterPosition());
            if (v.getId() == R.id.post_img) {
                mActionsListener.openPostFullscreen(v, post);
            } else if (v.getId() == R.id.post_upvote_btn) {
                mActionsListener.upvote(getAdapterPosition(), post);
            } else if (v.getId() == R.id.post_downvote_btn) {
                mActionsListener.downvote(getAdapterPosition(), post);
            } else {
                mActionsListener.openPostDetails(img, post);
            }
        }
    }
}
