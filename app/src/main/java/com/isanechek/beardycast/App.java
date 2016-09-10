package com.isanechek.beardycast;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.ConnectionQuality;
import com.androidnetworking.interfaces.ConnectionQualityChangeListener;
import com.isanechek.beardycast.data.network.OkHttp;
import io.realm.Realm;
import io.realm.RealmConfiguration;
import rx.plugins.RxJavaErrorHandler;
import rx.plugins.RxJavaPlugins;
import timber.log.Timber;

/**
 * Created by isanechek on 26.04.16.
 */
public class App extends Application {

    private static App instance;
    private static SharedPreferences preferences;


    public App() {
        instance = this;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        OkHttp.init();

        AndroidNetworking.initialize(getApplicationContext(), OkHttp.getOkHttpClient());
        if (BuildConfig.DEBUG) {
            AndroidNetworking.enableLogging("Networking");
        }
        AndroidNetworking.setConnectionQualityChangeListener((currentConnectionQuality, currentBandwidth) -> Timber.d(currentConnectionQuality.toString() + " " + currentBandwidth));

        RxJavaPlugins.getInstance().registerErrorHandler(new RxJavaErrorHandler() {
            @Override
            public void handleError(Throwable e) {
                super.handleError(e);
                Timber.e(e.toString());
            }
        });

        RealmConfiguration configuration = new RealmConfiguration.Builder(this)
                .name("beardycast")
                .schemaVersion(1)
                .build();
        Realm.setDefaultConfiguration(configuration);
    }

    public static Context getContext() {
        return instance;
    }

    public static SharedPreferences getPreferences(){
        if(preferences ==null)
            preferences = PreferenceManager.getDefaultSharedPreferences(instance.getApplicationContext());
        return preferences;
    }


}
