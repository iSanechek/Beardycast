package com.isanechek.beardycast.ui.details;

import android.support.annotation.NonNull;
import android.util.Log;

import com.annimon.stream.Stream;
import com.isanechek.beardycast.data.Model;
import com.isanechek.beardycast.data.ModelT;
import com.isanechek.beardycast.data.api.ApiImpl;
import com.isanechek.beardycast.data.model.podcast.Podcast;
import com.isanechek.beardycast.data.model.details.DetailsModel;
import com.isanechek.beardycast.data.model.details.DetailsObject;
import com.isanechek.beardycast.data.parser.model.details.ParserModelArticle;
import com.isanechek.beardycast.ui.Presenter;

import java.util.ArrayList;
import java.util.List;

import rx.Scheduler;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
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
    private List<Subscription> subscriptions;
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
        subscriptions = new ArrayList<>();
    }

    @Override
    public void onResume() {
        logD(TAG, "onResume");
        subscriptions.add(model.isNetworkUsed()
                .subscribe(view::showProgress));
        loadData(id);
    }


    @Override
    public void onPause() {
        logD(TAG, "onPause");
        unSubscribeAll();
    }

    @Override
    public void onDestroy() {
        logD(TAG, "onDestroy");
    }

    private void loadData(@NonNull String url) {
        logD(TAG, "loadData");

        subscriptions.add(model.getArticle(url)
                .subscribe(view::loadView, view::showErrorView));

        loadDetails(url);

    }

    private void loadDetails(String url) {
        subscriptions.add(api.getArticleDetails(url)
        .observeOn(Schedulers.io())
        .subscribeOn(AndroidSchedulers.mainThread())
        .subscribe(view::createView, view::showErrorView));
    }

    private void unSubscribeAll() {
        Stream.of(subscriptions)
                .filter(subscription -> !subscription.isUnsubscribed())
                .forEach(Subscription::unsubscribe);
    }
}
