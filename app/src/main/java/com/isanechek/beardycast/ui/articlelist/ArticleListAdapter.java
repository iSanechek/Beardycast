package com.isanechek.beardycast.ui.articlelist;

import android.content.Context;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import com.bumptech.glide.Glide;
import com.isanechek.beardycast.R;
import com.isanechek.beardycast.data.model.article.Article;
import com.isanechek.beardycast.utils.Util;
import io.realm.RealmList;

/**
 * Created by isanechek on 03.07.16.
 */

class ArticleListAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private static final String TAG = ArticleListAdapter.class.getSimpleName();

    private static final int VIEW_ITEM = 0;
    private static final int VIEW_LOADING = 1;
    private static final int VISIBLE_THRESHOLD = 5;

    private RealmList<Article> articleList;
    private boolean mIsLoading;
    private OnLoadMoreCallback mOnLoadMoreListener;
    private OnArticleClickListener mOnClickItemCallback;
    private Context mContext;

    public ArticleListAdapter(Context context) {
        articleList = new RealmList<>();
        this.mContext = context;
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

    public void setOnCallbackListener(OnLoadMoreCallback onLoadMoreListener, OnArticleClickListener mOnClickItemCallback) {
        this.mOnLoadMoreListener = onLoadMoreListener;
        this.mOnClickItemCallback = mOnClickItemCallback;
    }

    RealmList<Article> getListArticle() {
        return articleList;
    }

    public void setArticleList(RealmList<Article> list) {
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
                    .inflate(R.layout.article_list_item, parent, false);
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
            itemHolder.setArticle(article);
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

    private class ItemViewHolder extends RecyclerView.ViewHolder {

        TextView description;
        TextView title;
        TextView date;
        CardView cardView;
        ImageView pic;

        public ItemViewHolder(View v) {
            super(v);
            description = (TextView) v.findViewById(R.id.list_description);
            title = (TextView) v.findViewById(R.id.list_art_title);
            date = (TextView) v.findViewById(R.id.list_art_date);
            cardView = (CardView) v.findViewById(R.id.cardView);
            pic = (ImageView) v.findViewById(R.id.list_art_img);
        }

        public void setArticle(Article article) {
            title.setText(article.getArtTitle());
            description.setText(article.getArtDescription());
            String d1 = Util.getDate(article.getArtDatePost());
            date.setText(d1);

            Glide.with(mContext)
                    .load(article.getArtImgLink())
                    .asBitmap()
                    .thumbnail(0.1f)
                    .placeholder(R.drawable.h1)
                    .into(pic);
            cardView.setOnClickListener(view -> {
                mOnClickItemCallback.onArticleClicked(article.getArtLink());
            });

        }
    }

    private class LoadingViewHolder extends RecyclerView.ViewHolder {
        ProgressBar mLoading;

        LoadingViewHolder(View itemView) {
            super(itemView);
            mLoading = (ProgressBar) itemView.findViewById(R.id.loading);
        }
    }

    public interface OnLoadMoreCallback {
        void onLoadMore();
    }

    public interface OnArticleClickListener {
        void onArticleClicked(final String articleId);
    }
}
