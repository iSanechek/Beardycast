package com.isanechek.beardycast.ui.mvp;

import android.support.annotation.NonNull;

public interface MvpView {
    void showError(int errorCode, @NonNull String errorMessage);
    void showProgress(boolean visible);
}
