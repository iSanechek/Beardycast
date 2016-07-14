package com.isanechek.beardycast.ui.details;

import android.support.annotation.NonNull;

import com.annimon.stream.Stream;
import com.isanechek.beardycast.data.ModelT;
import com.isanechek.beardycast.data.api.ApiImpl;
import com.isanechek.beardycast.ui.Presenter;

import java.util.ArrayList;
import java.util.List;

import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

import static com.isanechek.beardycast.utils.LogUtil.logD;

/**
 * Created by isanechek on 14.06.16.
 */
public class DetailsPresenter implements Presenter {
    private static final String TAG = "DetailsPresenter";

    private final DetailsActivity view;
    private final ModelT model;
    private final String id;
    private Subscription isNetworkUsed;
    private Subscription loadData;
    private Subscription loadDetailsData;
    private ApiImpl api;


    public DetailsPresenter(DetailsActivity view, ModelT model, String id) {
        this.view = view;
        this.model = model;
        this.id = id;
        api = new ApiImpl();
    }

    @Override
    public void onCreate() {
        logD(TAG, "onCreate");
    }

    @Override
    public void onResume() {
        logD(TAG, "onResume");

        isNetworkUsed = model.isNetworkUsed()
                .subscribe(view::showProgress);
        loadData(id);
    }


    @Override
    public void onPause() {
        logD(TAG, "onPause");
        isNetworkUsed.unsubscribe();
        loadData.unsubscribe();
        loadDetailsData.unsubscribe();
    }

    @Override
    public void onDestroy() {
        logD(TAG, "onDestroy");
    }

    private void loadData(@NonNull String url) {
        logD(TAG, "loadData");

        loadData = model.getArticle(url)
                .subscribe(view::loadView, view::showErrorView);

        loadDetails(url);

    }

    private void loadDetails(String url) {
        loadDetailsData = api.getArticleDetails(url)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(view::createView, view::showErrorView);
    }
}
