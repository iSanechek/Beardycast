package com.isanechek.beardycast.ui.articlelist;

import android.content.Context;
import android.graphics.Color;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.SpannableString;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.annimon.stream.Stream;
import com.bumptech.glide.Glide;
import com.devspark.robototextview.widget.RobotoTextView;
import com.isanechek.beardycast.R;
import com.isanechek.beardycast.data.model.article.Article;
import com.isanechek.beardycast.ui.widget.BadgeDrawable;
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
    private int countBreak = 0;

    private RealmList<Article> articleList;
    private boolean mIsLoading;
    private OnLoadMoreCallback mOnLoadMoreListener;
    private final OnArticleClickListener clickListener;
    private Context mContext;

    ArticleListAdapter(Context context, OnArticleClickListener clickListener) {
        articleList = new RealmList<>();
        this.mContext = context;
        this.clickListener = clickListener;
    }

    void bindRecyclerView(RecyclerView recyclerView) {
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

    RealmList<Article> getListArticle() {
        return articleList;
    }

    void setArticleList(RealmList<Article> list) {
        articleList = list;
    }

    void setLoaded() {
        mIsLoading = false;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        RecyclerView.ViewHolder holder;

        if (viewType == VIEW_ITEM) {
            View itemView = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item, parent, false);
            holder = new ItemViewHolder(itemView, clickListener);
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

        RobotoTextView description;
        RobotoTextView title;
        TextView date;
        CardView cardView;
        ImageView pic;
        LinearLayout scrollView;

        ItemViewHolder(View v, final OnArticleClickListener clickListener) {
            super(v);
            description = (RobotoTextView) v.findViewById(R.id.list_description);
            title = (RobotoTextView) v.findViewById(R.id.list_art_title);
            date = (TextView) v.findViewById(R.id.list_art_date);
            cardView = (CardView) v.findViewById(R.id.cardView);
            pic = (ImageView) v.findViewById(R.id.list_art_img);
            scrollView = (LinearLayout) v.findViewById(R.id.list_category_container);
        }

        public void setArticle(Article article) {
            title.setText(article.getArtTitle());
            description.setText(article.getArtDescription());
            String d1 = Util.getDate(article.getArtDatePost());
            date.setText(d1);

            LinearLayout layout = new LinearLayout(mContext);
            layout.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
            layout.setOrientation(LinearLayout.HORIZONTAL);

            Stream.of(article.getTags()).forEach(tag -> {
                BadgeDrawable drawable = new BadgeDrawable.Builder()
                        .type(BadgeDrawable.TYPE_ONLY_ONE_TEXT)
                        .badgeColor(R.color.accent)
                        .text1(tag.getTagName())
                        .build();

                SpannableString spannableString = new SpannableString(TextUtils.concat(drawable.toSpannable()));

                TextView textView = new TextView(mContext);
                textView.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
                        ViewGroup.LayoutParams.WRAP_CONTENT));
                textView.setText(spannableString);
                textView.setTextSize(12f);
                textView.setTextColor(Color.parseColor("#EEEEEE"));
                textView.setPadding(4,4,4,4);
                layout.addView(textView);
            });
            scrollView.addView(layout);

            Glide.with(mContext)
                    .load(article.getArtImgLink())
                    .asBitmap()
                    .thumbnail(0.1f)
                    .placeholder(R.drawable.h1)
                    .into(pic);
            cardView.setOnClickListener(view -> clickListener.onArticleClicked(article.getArtLink()));
        }
    }

    private class LoadingViewHolder extends RecyclerView.ViewHolder {
        ProgressBar mLoading;

        LoadingViewHolder(View itemView) {
            super(itemView);
            mLoading = (ProgressBar) itemView.findViewById(R.id.loading);
        }
    }

    interface OnLoadMoreCallback {
        void onLoadMore();
    }

    interface OnArticleClickListener {
        void onArticleClicked(final String articleId);
    }
}
