package com.isanechek.beardycast.ui.podcast;

import com.isanechek.beardycast.data.Model;
import com.isanechek.beardycast.ui.Presenter;

import rx.Subscription;

/**
 * Created by isanechek on 14.05.16.
 */
public class PodcastPresenter implements Presenter {

    private final String url;
    private final Model model;
    private final PodcastActivity view;

    private Subscription loaderSubscription;
    private Subscription dataSubscription;

    public PodcastPresenter(String url, Model model, PodcastActivity view) {
        this.url = url;
        this.model = model;
        this.view = view;
    }

    @Override
    public void onCreate() {

    }

    @Override
    public void onResume() {
        loaderSubscription = model.isNetworkUsed()
                .subscribe(aBoolean -> {
                    // implements progress bar
                });

        loadData(url);
    }

    @Override
    public void onPause() {
        loaderSubscription.unsubscribe();
        dataSubscription.unsubscribe();
    }

    @Override
    public void onDestroy() {
        if (!loaderSubscription.isUnsubscribed() || !dataSubscription.isUnsubscribed()) {
            loaderSubscription.unsubscribe();
            dataSubscription.unsubscribe();
        }
    }

    private void loadData(String url) {
        if (dataSubscription != null) {
            dataSubscription.unsubscribe();
        }

//        dataSubscription = model
//                .getArticle(url)
//                .subscribe()
    }

    private void blackMagicPodcast() {



    }


}
