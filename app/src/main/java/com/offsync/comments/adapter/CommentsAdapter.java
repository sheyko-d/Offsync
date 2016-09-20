package com.offsync.comments.adapter;

import android.content.Context;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.offsync.R;
import com.offsync.comments.data.Comment;
import com.offsync.postdetails.PostDetailsPresenter;
import com.offsync.util.HtmlTagHandler;
import com.offsync.util.Util;

import java.util.ArrayList;

import butterknife.Bind;
import butterknife.ButterKnife;

public class CommentsAdapter extends RecyclerView.Adapter<CommentsAdapter.ViewHolder> {

    private final Context mContext;
    private final PostDetailsPresenter mActionsListener;
    private ArrayList<Comment> mComments;
    private int[] mCommentDepthColors = new int[]{R.color.depth_0, R.color.depth_1, R.color.depth_2,
            R.color.depth_3, R.color.depth_4, R.color.depth_5, R.color.depth_6, R.color.depth_7,
            R.color.depth_8, R.color.depth_9, R.color.depth_10};

    public CommentsAdapter(Context context, ArrayList<Comment> comments,
                           PostDetailsPresenter actionsListener) {
        mContext = context;
        mComments = comments;
        mActionsListener = actionsListener;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View drawerItemView = LayoutInflater.from(parent.getContext()).inflate(viewType == Comment
                .TYPE_REPLY ? R.layout.item_comment : R.layout.item_comment_more, parent, false);
        return new ViewHolder(drawerItemView);
    }

    @Override
    public int getItemViewType(int position) {
        return mComments.get(position).getType();
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        Comment comment = mComments.get(position);
        if (comment.getType() == Comment.TYPE_REPLY) {
            assert holder.scoreTxt != null;
            assert holder.sourceTxt != null;
            assert holder.stickiedTxt != null;
            assert holder.depthView != null;
            assert holder.gildedLayout != null;
            assert holder.gildedTxt != null;

            holder.bodyTxt.setText(Html.fromHtml(comment.getBody(), null, new HtmlTagHandler()));
            holder.bodyTxt.setMovementMethod(LinkMovementMethod.getInstance());

            holder.scoreTxt.setText(String.valueOf(comment.getScore()));
            holder.scoreTxt.setVisibility(comment.isScoreHidden() ? View.GONE : View.VISIBLE);

            holder.sourceTxt.setText(mContext.getString(comment.isScoreHidden() ?
                            R.string.comment_source_score_hidden : R.string.comment_source,
                    comment.getAuthor(), Util.formatTime(comment.getTimestamp()))
                    .replace("/u/[deleted]", "[deleted]"));
            holder.stickiedTxt.setVisibility(comment.isStickied() ? View.VISIBLE : View.GONE);
            holder.sourceTxt.setTextColor(ContextCompat.getColor(mContext, comment
                    .getDistinguished().equals("moderator") ? R.color.green :
                    R.color.secondary_text));

            holder.gildedLayout.setVisibility(comment.getGildedCount() > 0 ? View.VISIBLE : View.GONE);
            holder.gildedTxt.setVisibility(comment.getGildedCount() > 1 ? View.VISIBLE : View.GONE);
            holder.gildedTxt.setText(String.valueOf(comment.getGildedCount()));
        } else {
            if (comment.getMoreCount() > 0) {
                holder.bodyTxt.setText(mContext.getString(R.string.comment_more,
                        comment.getMoreCount()));
            } else {
                holder.bodyTxt.setText(mContext.getString(R.string.comment_continue));
            }
        }
        holder.depthView.setBackgroundResource(mCommentDepthColors[comment.getDepth()]);
        holder.itemView.setPadding(Util.convertDpToPixel(4) * comment.getDepth(), 0, 0, 0);
    }

    @Override
    public int getItemCount() {
        return mComments == null ? 0 : mComments.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        @Bind(R.id.comment_body_txt)
        TextView bodyTxt;
        @Nullable
        @Bind(R.id.comment_score_txt)
        TextView scoreTxt;
        @Nullable
        @Bind(R.id.comment_source_txt)
        TextView sourceTxt;
        @Nullable
        @Bind(R.id.comment_stickied_txt)
        TextView stickiedTxt;
        @Nullable
        @Bind(R.id.comment_depth_view)
        View depthView;
        @Nullable
        @Bind(R.id.comment_gilded_layout)
        View gildedLayout;
        @Nullable
        @Bind(R.id.comment_gilded_txt)
        TextView gildedTxt;

        public ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }

        @Override
        public void onClick(View v) {
        }
    }
}
