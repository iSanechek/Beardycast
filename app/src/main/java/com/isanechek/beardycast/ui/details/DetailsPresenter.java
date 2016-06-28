package com.isanechek.beardycast.ui.details;

import android.support.annotation.NonNull;
import android.util.Log;

import com.isanechek.beardycast.data.Model;
import com.isanechek.beardycast.data.api.ApiImpl;
import com.isanechek.beardycast.ui.Presenter;

import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;

import static com.isanechek.beardycast.utils.LogUtil.logD;

/**
 * Created by isanechek on 14.06.16.
 */
public class DetailsPresenter implements Presenter {
    private static final String TAG = "DetailsPresenter";

    private final DetailsActivity view;
    private final Model model;
    private final String id;

    private Subscription loaderSubscription;
    private Subscription dataSubscription;
    private Subscription detailsSubscription;
    private ApiImpl api = new ApiImpl();


    public DetailsPresenter(DetailsActivity view, Model model, String id) {
        this.view = view;
        this.model = model;
        this.id = id;
    }

    @Override
    public void onCreate() {
        logD(TAG, "onCreate");
    }

    @Override
    public void onResume() {
        logD(TAG, "onResume");
        loaderSubscription = model.isNetworkUsed().subscribe(view::showProgress);
        loadData(id);
    }

    @Override
    public void onPause() {
        logD(TAG, "onPause");
        if (dataSubscription != null)
            dataSubscription.unsubscribe();
    }

    @Override
    public void onDestroy() {
        logD(TAG, "onDestroy");
        if (dataSubscription != null)
            dataSubscription.unsubscribe();

    }

    private void loadData(@NonNull String url) {
        logD(TAG, "loadData");
        if (dataSubscription != null) {
            dataSubscription.unsubscribe();
        }

        dataSubscription = model.getArticle(url)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(view::testCreate, throwable -> {
                    Log.e("load Data", "error -->> " + throwable.toString());
//                    view.showErrorView();
                });
    }
}
