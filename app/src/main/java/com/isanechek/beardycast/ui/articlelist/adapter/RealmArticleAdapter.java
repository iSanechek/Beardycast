package com.isanechek.beardycast.ui.articlelist.adapter;

import android.content.Context;

import com.isanechek.beardycast.realm.model.Article;
import com.isanechek.beardycast.ui.articlelist.adapter.helper.RealmModelAdapter;

import io.realm.RealmResults;

/**
 * Created by isanechek on 07.05.16.
 */
public class RealmArticleAdapter extends RealmModelAdapter<Article> {
    public RealmArticleAdapter(Context context, RealmResults<Article> results) {
        super(context, results);
    }
}
