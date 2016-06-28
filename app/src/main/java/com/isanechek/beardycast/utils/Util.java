package com.isanechek.beardycast.utils;

import android.os.Build;
import android.util.DisplayMetrics;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

import static com.isanechek.beardycast.utils.LogUtil.logD;
import static com.isanechek.beardycast.utils.LogUtil.logE;

/**
 * Created by isanechek on 11.05.16.
 */
public class Util {

    /*Get Date For Sort*/
    public static Date getTimeStamp() {
        Calendar calendar = null;
        try {
            long time = System.currentTimeMillis();
            logD("getTimeStamp", "time -->> " + time);

            calendar = Calendar.getInstance();
            TimeZone timeZone = TimeZone.getDefault();
            calendar.setTimeInMillis(time);
            calendar.add(Calendar.MILLISECOND, timeZone.getOffset(calendar.getTimeInMillis()));
//            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        } catch (Exception e) {
            e.printStackTrace();
            logE("getTimeStamp", "Error -->> " + e.getMessage());
        }
        return calendar != null ? calendar.getTime() : null;

    }

    public static String getDate(Date date) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd MMM");
        return dateFormat.format(date);
    }

    public static String getCheckDate(Date date) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd MMM yyyy");
        return dateFormat.format(date);
    }

    public static boolean isAndroid5Plus() {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP;
    }

    public static int dpToPix(int dp, DisplayMetrics metrics) {
        return (dp * metrics.densityDpi) / DisplayMetrics.DENSITY_DEFAULT;
    }

    public static String getPodName(String name) {
        if (name.contains("BeardyCast")) {
            return ("BeardyCast");
        } else if (name.contains("BEARDYCARS")) {
            return ("BEARDYCARS");
        } else if (name.contains("Theory")) {
            return ("Theory");
        } else if (name.contains("Crowd")) {
            return ("Crowd");
        }
        return "";
    }
}
