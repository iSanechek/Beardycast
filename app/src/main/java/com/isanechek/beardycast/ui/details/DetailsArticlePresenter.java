package com.isanechek.beardycast.ui.details;

import android.os.AsyncTask;
import android.support.annotation.NonNull;
import com.isanechek.beardycast.data.Model;
import com.isanechek.beardycast.data.api.ApiImpl;
import com.isanechek.beardycast.data.network.OkHelper;
import com.isanechek.beardycast.ui.mvp.MvpPresenter;
import com.isanechek.beardycast.utils.RxUtil;
import rx.Subscription;

import static com.isanechek.beardycast.utils.LogUtil.logD;

/**
 * Created by isanechek on 10.08.16.
 */

public class DetailsArticlePresenter extends MvpPresenter<DetailsArticleView> {
    private static final String TAG = DetailsArticlePresenter.class.getSimpleName();

    private Subscription subscription;
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
}
