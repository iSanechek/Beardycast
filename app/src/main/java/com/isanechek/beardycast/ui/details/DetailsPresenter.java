package com.isanechek.beardycast.ui.details;

import android.support.annotation.NonNull;

import com.isanechek.beardycast.data.Model;
import com.isanechek.beardycast.data.api.ApiImpl;
import com.isanechek.beardycast.ui.Presenter;
import com.isanechek.beardycast.utils.RxUtil;

import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

import static com.isanechek.beardycast.utils.LogUtil.logD;

/**
 * Created by isanechek on 14.06.16.
 */
class DetailsPresenter implements Presenter {
    private static final String TAG = "DetailsPresenter";

    private final DetailsActivity view;
    private final Model model;
    private final String id;
    private Subscription isNetworkUsed;
    private Subscription loadData;
    private Subscription loadDetailsData;
    private ApiImpl api;

    DetailsPresenter(DetailsActivity view, Model model, String id) {
        this.view = view;
        this.model = model;
        this.id = id;
        api = new ApiImpl();
    }

    @Override
    public void onCreate() {
        logD(TAG, "onCreate");
        isNetworkUsed = model.isNetworkUsed()
                .subscribe(view::showProgress);
        loadDataHelper(id);
    }

    @Override
    public void onResume() {
        logD(TAG, "onResume");
    }


    @Override
    public void onPause() {
        logD(TAG, "onPause");
        RxUtil.unsubscribe(isNetworkUsed);
        RxUtil.unsubscribe(loadData);
        RxUtil.unsubscribe(loadDetailsData);
    }

    @Override
    public void onDestroy() {
        logD(TAG, "onDestroy");
    }

    private void loadDataHelper(@NonNull String url) {
        logD(TAG, "loadDataHelper");
        RxUtil.unsubscribe(loadData);
        loadData = model.getArticle(url)
                .subscribe(view::loadView, view::showErrorView);
    }

    void loadDetails(String url) {
        logD(TAG, "loadDetails");
        RxUtil.unsubscribe(loadDetailsData);
        loadDetailsData = api.getArticleDetails(url)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(view::createView, view::showErrorView);

    }
}
