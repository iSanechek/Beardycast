package com.isanechek.beardycast.utils;

import android.util.Log;

/**
 * Created by isanechek on 25.05.16.
 */
public class LogUtil {

    public static void logE(String tag, String text) {
        Log.e(tag, text);
    }

    public static void logD(String tag, String text) {
        Log.d(tag, text);
    }

    public static void logI(String tag, String text) {
        Log.i(tag, text);
    }
}
