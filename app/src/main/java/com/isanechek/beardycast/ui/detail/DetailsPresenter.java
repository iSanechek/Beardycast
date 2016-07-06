package com.isanechek.beardycast.ui.detail;

import android.support.annotation.NonNull;
import android.util.Log;

import com.isanechek.beardycast.data.Model;
import com.isanechek.beardycast.data.api.ApiImpl;
import com.isanechek.beardycast.ui.Presenter;
import com.isanechek.beardycast.utils.LogUtil;

import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

import static com.isanechek.beardycast.utils.LogUtil.logE;

/**
 * Created by isanechek on 11.05.16.
 */
public class DetailsPresenter implements Presenter {

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

    }

    @Override
    public void onResume() {
        loaderSubscription = model.isNetworkUsed().subscribe(view::showProgress);

        loadData(id);
        loadDetails(id);
    }

    @Override
    public void onPause() {
        if (loaderSubscription != null)
            loaderSubscription.unsubscribe();

        if (dataSubscription != null)
            dataSubscription.unsubscribe();

        if (detailsSubscription != null)
            detailsSubscription.unsubscribe();
    }

    @Override
    public void onDestroy() {

        if (loaderSubscription != null)
            loaderSubscription.unsubscribe();

        if (dataSubscription != null)
            dataSubscription.unsubscribe();

        if (detailsSubscription != null)
            detailsSubscription.unsubscribe();
    }

    private void loadData(@NonNull String url) {
        if (dataSubscription != null) {
            dataSubscription.unsubscribe();
        }

        dataSubscription = model.getArticle(url)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(view::loadView, throwable -> {
                    Log.e("load Data", "error -->> " + throwable.toString());
                    view.showErrorView();
                });


    }

    private void loadDetails(@NonNull String id) {
        if (detailsSubscription != null)
            detailsSubscription.unsubscribe();

        // load details content
//
//        detailsSubscription = api.getArticleDetails(id)
//                .subscribeOn(Schedulers.io())
//                .observeOn(AndroidSchedulers.mainThread())
//                .subscribe(view::createUI, throwable -> {
//                    logE("load Details", "error -->> " + throwable.toString());
//                });
    }
}
