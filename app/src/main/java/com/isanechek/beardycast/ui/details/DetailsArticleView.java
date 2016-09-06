package com.isanechek.beardycast.ui.details;

import com.isanechek.beardycast.data.model.article.Article;
import com.isanechek.beardycast.ui.mvp.MvpView;

/**
 * Created by isanechek on 10.08.16.
 */

public interface DetailsArticleView extends MvpView {
    void bindView(Article article);
    void bindContentView(String body);
}
