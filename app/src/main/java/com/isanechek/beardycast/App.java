package com.isanechek.beardycast;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.os.Handler;
import android.preference.PreferenceManager;

import com.isanechek.beardycast.data.network.OkHttp;

import com.nostra13.universalimageloader.cache.disc.naming.HashCodeFileNameGenerator;
import com.nostra13.universalimageloader.cache.memory.impl.UsingFreqLimitedMemoryCache;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.display.FadeInBitmapDisplayer;
import io.realm.Realm;
import io.realm.RealmConfiguration;
import rx.plugins.RxJavaErrorHandler;
import rx.plugins.RxJavaPlugins;

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

        RxJavaPlugins.getInstance().registerErrorHandler(new RxJavaErrorHandler() {
            @Override
            public void handleError(Throwable e) {
                super.handleError(e);
                // implement error message
            }
        });

        RealmConfiguration configuration = new RealmConfiguration.Builder(this).build();
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
