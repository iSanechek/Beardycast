package com.isanechek.beardycast.utils;

import rx.Subscription;

/**
 * Created by isanechek on 15.07.16.
 */

public class RxUtil {

    public static void unsubscribe(Subscription subscription) {
        if (subscription != null && !subscription.isUnsubscribed()) {
            subscription.unsubscribe();
        }
    }
}
