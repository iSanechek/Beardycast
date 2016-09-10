package com.isanechek.beardycast.ui.details;

import android.os.AsyncTask;
import android.support.annotation.NonNull;
import com.isanechek.beardycast.data.Model;
import com.isanechek.beardycast.data.api.ApiImpl;
import com.isanechek.beardycast.data.model.podcast.Podcast;
import com.isanechek.beardycast.data.network.OkHelper;
import com.isanechek.beardycast.ui.mvp.MvpPresenter;
import com.isanechek.beardycast.utils.RxUtil;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.schedulers.Schedulers;
import timber.log.Timber;

import static com.isanechek.beardycast.utils.LogUtil.logD;

/**
 * Created by isanechek on 10.08.16.
 */

public class DetailsArticlePresenter extends MvpPresenter<DetailsArticleView> {
    private static final String TAG = DetailsArticlePresenter.class.getSimpleName();

    private Subscription subscription;
    private Subscription subscription1;
    private Subscription subscription2;
    private Model model;
    private ApiImpl api;

    public DetailsArticlePresenter() {
        model = Model.getInstance();
        api = ApiImpl.getInstance();
    }

    public void loadData(@NonNull String url) {
        logD(TAG, "loadData");
        logD(TAG, "url: " + url);
        checkViewAttached();

        RxUtil.unsubscribe(subscription);
        getMvpView().showProgress(true);
        subscription = model.getArticle(url)
                .subscribe(article -> {
                    getMvpView().showProgress(false);
                    getMvpView().bindView(article);
                    new TaskHelper().execute(url);
                }, throwable -> {
                    getMvpView().showProgress(false);
                    getMvpView().showError(0, throwable.toString());
                });
    }

    public void updatePodcastUrl(String id, String url) {
        model.addPodcastUrl(id, url);
    }

    private class TaskHelper extends AsyncTask<String, Void, String> {
        private TaskHelper() {
        }

        @Override
        protected String doInBackground(String... url) {
            return OkHelper.getBody(url[0]);
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            getMvpView().bindContentView(s);
        }
    }

    public void getPodcastUrl(String id, String url) {
        RxUtil.unsubscribe(subscription2);

        subscription2 = api.getPodcastUrl(url)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(s -> updatePodcastUrl(id, s));
    }

    public void getPodcastInfo(String id) {
        RxUtil.unsubscribe(subscription1);

        subscription1 = model.getPodcast(id)
                .subscribe(new Action1<Podcast>() {
                    @Override
                    public void call(Podcast podcast) {
                        Timber.d("Podcast Url: " + podcast.getPodcastUrl());
                    }
                }, new Action1<Throwable>() {
                    @Override
                    public void call(Throwable throwable) {
                        Timber.e("Podcast Url Null");
                    }
                });
    }
}
