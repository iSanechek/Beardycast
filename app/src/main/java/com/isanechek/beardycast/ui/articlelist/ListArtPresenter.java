package com.isanechek.beardycast.ui.articlelist;

import android.support.annotation.NonNull;

import com.isanechek.beardycast.data.Model;
import com.isanechek.beardycast.realm.model.Article;
import com.isanechek.beardycast.ui.Presenter;

import java.util.List;
import java.util.Map;

import io.realm.RealmResults;
import rx.Subscription;
import rx.functions.Action1;

/**
 * Created by isanechek on 04.05.16.
 */
public class ListArtPresenter implements Presenter {

    private final ListArtActivity activity;
    private final Model model;

    private Subscription loaderSubscription;
    private Subscription listDataSubscription;

    public ListArtPresenter(ListArtActivity activity, Model model) {
        this.activity = activity;
        this.model = model;
    }

    @Override
    public void onCreate() {

    }

    @Override
    public void onResume() {
        loaderSubscription = model.isNetworkUsed()
                .subscribe(activity::showNetworkLoading);

        sectionSelected(model.getCurrentSectionKey());
    }

    @Override
    public void onPause() {
        loaderSubscription.unsubscribe();
        listDataSubscription.unsubscribe();
    }

    @Override
    public void onDestroy() {

    }

    public void refreshList() {
        model.reloadArticleFeed();
        activity.hideRefreshing();
    }

    public void loadMore() {
        activity.showLoadMore();
        model.loadMoreArticle();
//        activity.hideLoadMore();
    }

    private void sectionSelected(@NonNull String url) {
        model.selectSection(url);
        if (listDataSubscription != null) {
            listDataSubscription.unsubscribe();
        }

        listDataSubscription = model.getSelectedArticleFeed()
                .subscribe(activity::showList);
    }
}
