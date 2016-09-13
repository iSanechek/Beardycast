package com.isanechek.beardycast.ui.categorylist;

import com.isanechek.beardycast.ui.mvp.MvpView;

/**
 * Created by isanechek on 10.09.16.
 */
public interface CategoryView extends MvpView {
    void bindView();
    void showError();
}
