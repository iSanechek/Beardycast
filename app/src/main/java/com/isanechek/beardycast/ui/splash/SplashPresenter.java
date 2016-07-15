package com.isanechek.beardycast.ui.splash;

import android.util.Log;

import com.isanechek.beardycast.pref.Preferences;
import com.isanechek.beardycast.ui.Presenter;

import rx.Subscription;

/**
 * Created by isanechek on 30.05.16.
 */
public class SplashPresenter implements Presenter {

    private void msg(String s) {
        Log.d("SplashPresenter", s);
    }
    private void msge(String s) {
        Log.e("SplashPresenter", s);
    }

    private final SplashActivity view;

    private Subscription loaderSubscription;
    private Subscription dataSubscription;

    public SplashPresenter(SplashActivity view) {
        this.view = view;
    }

    @Override
    public void onCreate() {

    }

    @Override
    public void onResume() {
        if (Preferences.MainPreferences.isFirstStart()) {
            msg("First Start");
//            loaderSubscription = model.isNetworkUsed()
//                    .subscribe(view::showProgress);

            // implements load data
            loadDate();
        }
    }

    private void loadDate() {
//        model.firstStart();
    }

    @Override
    public void onPause() {
        if (loaderSubscription != null)
            loaderSubscription.unsubscribe();

        if (dataSubscription != null)
            dataSubscription.unsubscribe();

    }

    @Override
    public void onDestroy() {
        if (loaderSubscription != null)
            loaderSubscription.unsubscribe();

        if (dataSubscription != null)
            dataSubscription.unsubscribe();

    }
}
