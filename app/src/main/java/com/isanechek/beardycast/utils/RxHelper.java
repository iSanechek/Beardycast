package com.isanechek.beardycast.utils;

import rx.Scheduler;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class RxHelper {
    public Scheduler io() {
        return Schedulers.io();
    }

    public Scheduler androidMainThread() {
        return AndroidSchedulers.mainThread();
    }
}
