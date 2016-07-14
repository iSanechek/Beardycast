package com.isanechek.beardycast.ui.articlelist;

import android.graphics.Color;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.devspark.robototextview.widget.RobotoTextView;
import com.isanechek.beardycast.R;
import com.isanechek.beardycast.data.model.article.ArtTag;
import com.isanechek.beardycast.data.model.article.Article;
import com.isanechek.beardycast.ui.details.DetailsActivity;
import com.isanechek.beardycast.utils.Util;

import io.realm.RealmList;
import me.kaede.tagview.Tag;
import me.kaede.tagview.TagView;

/**
 * Created by isanechek on 03.07.16.
 */

public class ListTAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private static final String TAG = ListTAdapter.class.getSimpleName();

    private static final int VIEW_ITEM = 0;
    private static final int VIEW_LOADING = 1;
    private static final int VISIBLE_THRESHOLD = 5;

    private RealmList<Article> articleList;
    private boolean mIsLoading;
    private OnLoadMoreCallback mOnLoadMoreListener;

    public ListTAdapter() {
        articleList = new RealmList<>();
    }

    public void bindRecyclerView(RecyclerView recyclerView) {
        if (recyclerView.getLayoutManager() instanceof LinearLayoutManager) {
            final LinearLayoutManager layoutManager = (LinearLayoutManager) recyclerView.getLayoutManager();
            recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
                @Override
                public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                    super.onScrolled(recyclerView, dx, dy);

                    int totalItemCount = layoutManager.getItemCount();
                    int lastVisibleItem = layoutManager.findLastVisibleItemPosition();
                    if (!mIsLoading && totalItemCount <= (lastVisibleItem + VISIBLE_THRESHOLD)) {
                        if (mOnLoadMoreListener != null) {
                            mOnLoadMoreListener.onLoadMore();
                        }
                        mIsLoading = true;
                    }
                }
            });
        }
    }

    void setOnLoadMoreListener(OnLoadMoreCallback onLoadMoreListener) {
        this.mOnLoadMoreListener = onLoadMoreListener;
    }

    public RealmList<Article> getListArticle() {
        return articleList;
    }

    void setArticleList(RealmList<Article> list) {
        articleList = list;
    }

    public void setLoaded() {
        mIsLoading = false;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        RecyclerView.ViewHolder holder;

        if (viewType == VIEW_ITEM) {
            View itemView = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item, parent, false);
            holder = new ItemViewHolder(itemView);
        } else {
            View loadingView = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_loading, parent, false);
            holder = new LoadingViewHolder(loadingView);
        }

        return holder;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if (holder.getItemViewType() == VIEW_ITEM) {
            final Article article = articleList.get(position);

            final ItemViewHolder itemHolder = (ItemViewHolder) holder;
            itemHolder.title.setText(article.getArtTitle());
            itemHolder.description.setText(article.getArtDescription());
            String d1 = Util.getDate(article.getArtDatePost());
            itemHolder.date.setText(d1);

            itemHolder.tagView.removeAllTags();
            for (ArtTag t : article.getTags()) {
                Tag t1 = new Tag(t.getTagName());
                t1.tagTextSize = 12f;
                t1.layoutColor =  Color.parseColor("#FFFFFF");
                t1.tagTextColor = Color.parseColor("#ffe100");
                t1.layoutBorderSize = 1f;
                t1.layoutBorderColor = Color.parseColor("#ffe100");
                itemHolder.tagView.addTag(t1);
            }

            Glide.with(itemHolder.pic.getContext())
                    .load(article.getArtImgLink())
                    .asBitmap()
                    .placeholder(R.drawable.h1)
                    .into(itemHolder.pic);

            itemHolder.cardView.setOnClickListener((v) -> DetailsActivity.startActivity(itemHolder.cardView.getContext(), article.getArtLink()));
        }
        else {
            LoadingViewHolder loadingHolder = (LoadingViewHolder) holder;
            loadingHolder.mLoading.setIndeterminate(true);
        }
    }

    @Override
    public int getItemCount() {
        return articleList.size();
    }

    @Override
    public int getItemViewType(int position) {
        return articleList.get(position) != null ? VIEW_ITEM : VIEW_LOADING;
    }

    public class ItemViewHolder extends RecyclerView.ViewHolder {

        RobotoTextView description;
        RobotoTextView title;
        TextView date;
        CardView cardView;
        ImageView pic;
        TagView tagView;

        ItemViewHolder(View v) {
            super(v);
            description = (RobotoTextView) v.findViewById(R.id.list_description);
            title = (RobotoTextView) v.findViewById(R.id.list_art_title);
            date = (TextView) v.findViewById(R.id.list_art_date);
            cardView = (CardView) v.findViewById(R.id.cardView);
            pic = (ImageView) v.findViewById(R.id.list_art_img);
            tagView = (TagView) v.findViewById(R.id.list_art_tags);
        }
    }

    public class LoadingViewHolder extends RecyclerView.ViewHolder {
        ProgressBar mLoading;

        LoadingViewHolder(View itemView) {
            super(itemView);
            mLoading = (ProgressBar) itemView.findViewById(R.id.loading);
        }
    }

    interface OnLoadMoreCallback {
        void onLoadMore();
    }
}
