package com.isanechek.beardycast.utils;

import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;

import com.isanechek.beardycast.App;
import com.isanechek.beardycast.Constants;

/**
 * Created by isanechek on 03.05.16.
 */
public class UrlUtil {

    public static String getPageLoadedCount(String id) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(App.getContext());
        return Constants.LINK_MORE + preferences.getInt(id, 1) + "/";
    }

    public static void savePageLoadedCount(String id) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(App.getContext());
        int count = preferences.getInt(id, 1);
        Log.d("URL Util", "count " + count);
        count++;
        preferences.edit().putInt(id, count).apply();
    }

    public static String tryUrl(String url) {
        return Constants.HOME_LINK + url;
    }
}
