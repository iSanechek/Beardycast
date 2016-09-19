package com.isanechek.beardycast.ui.articlelist.interfaces;

import com.isanechek.beardycast.data.model.article.Article;
import com.isanechek.beardycast.ui.mvp.MvpView;
import io.realm.RealmList;
import io.realm.RealmResults;

/**
 * Created by isanechek on 04.09.16.
 */
public interface ArticleListView extends MvpView {
    void bindView(RealmResults<Article> articles);
    void bindMoreView(RealmList<Article> articles);
}
