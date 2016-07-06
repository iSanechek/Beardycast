package com.isanechek.beardycast.ui.articlelist;

import android.content.Context;
import android.graphics.Color;
import android.support.v7.widget.CardView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.devspark.robototextview.widget.RobotoTextView;
import com.isanechek.beardycast.R;
import com.isanechek.beardycast.data.model.article.ArtTag;
import com.isanechek.beardycast.data.model.article.Article;
import com.isanechek.beardycast.utils.Util;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.realm.RealmBasedRecyclerViewAdapter;
import io.realm.RealmResults;
import io.realm.RealmViewHolder;
import me.kaede.tagview.Tag;
import me.kaede.tagview.TagView;

public class ListAdapter extends RealmBasedRecyclerViewAdapter<Article, ListAdapter.ViewHolder> {

    public ListAdapter(Context context, RealmResults<Article> realmResults, boolean automaticUpdate, boolean animateResults) {
        super(context, realmResults, automaticUpdate, animateResults);
    }

    @Override
    public ViewHolder onCreateRealmViewHolder(ViewGroup viewGroup, int i) {
        View view = inflater.inflate(R.layout.item, viewGroup, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindRealmViewHolder(ViewHolder viewHolder, int i) {
        final Article article = realmResults.get(i);
        viewHolder.setArticle(article);
    }

    @Override
    public ViewHolder onCreateFooterViewHolder(ViewGroup viewGroup) {
        View v = inflater.inflate(R.layout.footer_view, viewGroup, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindFooterViewHolder(ViewHolder holder, int position) {
        holder.footerTextView.setText("I'm a footer");
    }

    public class ViewHolder extends RealmViewHolder {

        @BindView(R.id.list_description)
        RobotoTextView description;
        @BindView(R.id.list_art_title)
        RobotoTextView title;
        @BindView(R.id.list_art_date)
        TextView date;
        @BindView(R.id.cardView)
        CardView cardView;
        @BindView(R.id.list_art_img)
        ImageView pic;
        @BindView(R.id.list_art_tags)
        TagView tagView;
        TextView footerTextView;

        public ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
            footerTextView = ButterKnife.findById(itemView, R.id.footer_text_view);
        }

        public void setArticle(Article article) {
            title.setText(article.getArtTitle());
            description.setText(article.getArtDescription());
            String d1 = Util.getDate(article.getArtDatePost());
            date.setText(d1);

            tagView.removeAllTags();
            for (ArtTag t : article.getTags()) {
                Tag t1 = new Tag(t.getTagName());
                t1.tagTextSize = 12f;
                t1.layoutColor =  Color.parseColor("#FFFFFF");
                t1.tagTextColor = Color.parseColor("#ffe100");
                t1.layoutBorderSize = 1f;
                t1.layoutBorderColor = Color.parseColor("#ffe100");
                tagView.addTag(t1);
            }

            Glide.with(itemView.getContext())
                    .load(article.getArtImgLink())
                    .asBitmap()
                    .placeholder(R.drawable.h1)
                    .into(pic);

            cardView.setOnClickListener(v -> {
//                com.isanechek.beardycast.ui.details.DetailsActivity.startActivity(itemView.getContext(), article);
//                if (article.isPodcast())
//                    startPodcastActivity(itemView.getContext(), article);
//                else
//                    startDetailsIntent(itemView.getContext(), article);
            });
        }
    }
}
