package com.isanechek.beardycast.pref;

import com.isanechek.beardycast.App;
import com.isanechek.beardycast.Constants;

/**
 * Created by isanechek on 04.05.16.
 */
public class Preferences {

    public static class MainPreferences {

        public static boolean isWelcomeDone() {
            return App.getPreferences().getBoolean(Constants.PREF_WELCOME_DONE, false);
        }

        public static void markWelcomeDone() {
            App.getPreferences().edit().putBoolean(Constants.PREF_WELCOME_DONE, true).apply();
        }

        public static boolean isFirstStart() {
            return App.getPreferences().getBoolean(Constants.PREF_FIRST_START, true);
        }

        public static void markFirstStartDone() {
            App.getPreferences().edit().putBoolean(Constants.PREF_FIRST_START, false).apply();
        }
    }


}
