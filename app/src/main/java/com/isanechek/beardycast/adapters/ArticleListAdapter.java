package com.isanechek.beardycast.adapters;

import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.isanechek.beardycast.R;
import com.isanechek.beardycast.realm.model.Article;

import java.util.ArrayList;
import java.util.List;

import butterknife.ButterKnife;

/**
 * Created by isanechek on 03.05.16.
 */
public class ArticleListAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static final int VIEW_ITEM = 0;
    private static final int VIEW_LOADING = 1;
    private static final int VISIBLE_THRESHOLD = 5;

    private List<Article> list;
    private boolean mIsLoading;
    private OnLoadMoreCallback mOnLoadMoreListener;

    public ArticleListAdapter() {
        list = new ArrayList<>();
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

    public void setOnLoadMoreListener(OnLoadMoreCallback onLoadMoreListener) {
        this.mOnLoadMoreListener = onLoadMoreListener;
    }

    public List<Article> getArticle() {
        return list;
    }

    public void setArticle(List<Article> mList) {
        list = mList;
    }

    public void setLoaded() {
        mIsLoading = false;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        RecyclerView.ViewHolder holder;

        if (viewType == VIEW_ITEM) {
            View itemView = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.main_list_item, parent, false);
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
            final Article model = list.get(position);
            final ItemViewHolder itemHolder = (ItemViewHolder) holder;
            itemHolder.setArticle(model);

        } else {
            LoadingViewHolder loadingHolder = (LoadingViewHolder) holder;
            loadingHolder.mLoading.setIndeterminate(true);
        }

    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    @Override
    public int getItemViewType(int position) {
        return list.get(position) != null ? VIEW_ITEM : VIEW_LOADING;
    }

    private class ItemViewHolder extends RecyclerView.ViewHolder {

        TextView textView;

        public ItemViewHolder(View itemView) {
            super(itemView);
            textView = ButterKnife.findById(itemView, R.id.description);
        }

        public void setArticle(Article model) {
            textView.setText(model.getArtTitle());
        }
    }

    private class LoadingViewHolder extends RecyclerView.ViewHolder {

        ProgressBar mLoading;

        public LoadingViewHolder(View itemView) {
            super(itemView);
            mLoading = ButterKnife.findById(itemView, R.id.loading);
        }
    }

    public interface OnLoadMoreCallback {
        void onLoadMore();
    }
}
