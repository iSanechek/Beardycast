package com.isanechek.beardycast.ui.articlelist;

import com.isanechek.beardycast.data.Model;
import com.isanechek.beardycast.ui.articlelist.interfaces.ArticleListView;
import com.isanechek.beardycast.ui.mvp.MvpPresenter;
import com.isanechek.beardycast.utils.RxUtil;
import rx.Subscription;

/**
 * Created by isanechek on 04.09.16.
 */
public class ArticleListPresenter extends MvpPresenter<ArticleListView> {
    private static final String TAG = "Article List Presenter";

    private Model model;
    private Subscription subscription;
    private Subscription isNetworkUsed;
    public ArticleListPresenter() {
        model = Model.getInstance();
    }

    public void loadData(String url) {
        checkViewAttached();

        isNetworkUsed = model.isNetworkUsed()
                .subscribe();

        getArticle(url);
    }


    private void getArticle(String url) {
        RxUtil.unsubscribe(subscription);

        subscription = model.getSelectedArticleFeed()
                .subscribe(articles -> getMvpView().bindView(articles),
                        throwable -> getMvpView().showError(0, throwable.toString()));
    }
}
