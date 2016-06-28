package com.isanechek.beardycast.ui.articlelist.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.isanechek.beardycast.R;
import com.isanechek.beardycast.realm.model.Article;
import com.isanechek.beardycast.ui.articlelist.adapter.helper.RealmRecyclerViewAdapter;

/**
 * Created by isanechek on 07.05.16.
 */
public class ArticleAdapter extends RealmRecyclerViewAdapter<Article> {

    private class ArticleViewHolder extends RecyclerView.ViewHolder {

        TextView description;

        public ArticleViewHolder(View itemView) {
            super(itemView);
//            ButterKnife.bind(this, itemView);
            description = (TextView) itemView.findViewById(R.id.description);
        }

        public void setArticleModel(Article model) {
            description.setText(model.getArtTitle());
        }
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.main_list_item, parent, false);
        return new ArticleViewHolder(view);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        ArticleViewHolder viewHolder = (ArticleViewHolder) holder;
        Article article = getItem(position);
        viewHolder.setArticleModel(article);
    }

    @Override
    public int getItemCount() {
        if (getRealmAdapter() != null) {
            return getRealmAdapter().getCount();
        }
        return 0;
    }
}
