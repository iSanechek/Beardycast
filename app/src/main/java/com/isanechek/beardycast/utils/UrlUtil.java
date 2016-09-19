package com.isanechek.beardycast.utils;

import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;

import com.isanechek.beardycast.App;
import com.isanechek.beardycast.Constants;

import org.jsoup.nodes.Element;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by isanechek on 03.05.16.
 */
public class UrlUtil {

    public static String getPageLoadedCount(String id) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(App.getContext());
        return Constants.LINK_MORE + preferences.getInt(id, 2) + "/";
    }

    public static void savePageLoadedCount(String id) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(App.getContext());
        int count = preferences.getInt(id, 2);
        Log.d("URL Utils", "count " + count);
        count++;
        preferences.edit().putInt(id, count).apply();
    }

    public static String tryUrl(String url) {
        return Constants.HOME_LINK + url;
    }

    public static String getImageUrlFromElement(Element element) {
        if (element != null) {
            return element.select("img[src]").attr("src");
        }
        return null;
    }

    private static final String YOUTUBE_PATTERN = "^https?://.*(?:youtu.be/|v/|u/\\w/|embed/|watch?v=)([^#&?]*).*$";
    public static boolean isYouTubeUrl(String url) {
        Pattern pattern = Pattern.compile(YOUTUBE_PATTERN, Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(url);
        return matcher.find();
    }

    private static final String re1=".*?";	// Non-greedy match on filler
    private static final String re2="(\"imgur-embed-pub\")";	// Double Quote String 1
    public static boolean isImgurUrl(String s) {
        Pattern pattern = Pattern.compile(re1+re2,Pattern.CASE_INSENSITIVE | Pattern.DOTALL);
        Matcher matcher = pattern.matcher(s);
        return matcher.find();
    }

    private static final String re3="(\\/imgur\\.com\\/a\\/vAJrb)";	// Unix Path 1
    public static String getImgurUrl(String s) {
        Pattern p = Pattern.compile(re1+re3,Pattern.CASE_INSENSITIVE | Pattern.DOTALL);
        Matcher m = p.matcher(s);
        if (m.find()) {
            return m.group(1);
        }
        return null;
    }
}
