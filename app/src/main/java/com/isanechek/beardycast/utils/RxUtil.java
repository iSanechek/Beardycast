package com.isanechek.beardycast.utils;

import com.annimon.stream.Stream;
import com.annimon.stream.function.Consumer;
import com.annimon.stream.function.Predicate;

import rx.Subscription;
import rx.subscriptions.CompositeSubscription;

/**
 * Created by isanechek on 15.07.16.
 */

public class RxUtil {

    public static void unsubscribe(Subscription subscription) {
        if (subscription != null && !subscription.isUnsubscribed()) {
            subscription.unsubscribe();
        }
    }

    public static void unsubscribe(CompositeSubscription subscription) {
        Stream.of(subscription)
                .filter(value -> value != null)
                .forEach(CompositeSubscription::unsubscribe);
    }
}
