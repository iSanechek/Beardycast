package com.isanechek.beardycast.adapters;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.isanechek.beardycast.R;
import com.isanechek.beardycast.realm.model.Article;

import java.util.List;

import butterknife.ButterKnife;

/**
 * Created by isanechek on 05.05.16.
 */
public class ArticleAdapter extends RecyclerView.Adapter<ArticleAdapter.ViewHolder> {

    private List<Article> list;

    public ArticleAdapter(List<Article> list) {
        this.list = list;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.main_list_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Article article = list.get(position);
        holder.setArticle(article);
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        private TextView description;

        public ViewHolder(View itemView) {
            super(itemView);

            description = ButterKnife.findById(itemView, R.id.description);
        }

        public void setArticle(Article article) {
            description.setText(article.getArtTitle());
        }
    }
}
