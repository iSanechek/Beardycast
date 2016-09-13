package com.isanechek.beardycast.utils;

import android.util.Log;
import timber.log.Timber;

/**
 * Created by isanechek on 12.09.16.
 */
public final class CrashReportUtils {

    public static void log(int priority, String tag, String message) {
        // TODO add log entry to circular buffer.
    }

    public static void logWarning(Throwable t) {
        // TODO report non-fatal warning.
    }

    public static void logError(Throwable t) {
        // TODO report non-fatal error.
    }

    private CrashReportUtils() {
        throw new AssertionError("No instances.");
    }

    public static class CrashReportingTree extends Timber.Tree {

        @Override
        protected void log(int priority, String tag, String message, Throwable t) {
            if (priority == Log.VERBOSE || priority == Log.DEBUG) {
                return;
            }

            CrashReportUtils.log(priority, tag, message);

            if (t != null) {
                if (priority == Log.ERROR) {
                    CrashReportUtils.logError(t);
                } else if (priority == Log.WARN) {
                    CrashReportUtils.logWarning(t);
                }
            }
        }
    }
}
