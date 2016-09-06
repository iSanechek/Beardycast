package com.isanechek.beardycast.ui.articlelist;

import android.support.annotation.NonNull;
import com.isanechek.beardycast.data.Model;
import com.isanechek.beardycast.ui.Presenter;
import com.isanechek.beardycast.utils.LogUtil;
import com.isanechek.beardycast.utils.RxUtil;
import rx.Subscription;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Map;

/**
 * Created by isanechek on 04.05.16.
 */
public class nArticleListPresenter implements Presenter {

    private final nArticleListActivity activity;
    private final Model model;

    private Subscription isNetworkUsed;
    private Subscription loadData;
    private Subscription loadMoreData;

    private Map<String, String> sections;

    nArticleListPresenter(nArticleListActivity activity, Model model) {
        this.activity = activity;
        this.model = model;
    }

    @Override
    public void onCreate() {
        sections = model.getSections();
        ArrayList<String> sectionList = new ArrayList<>(sections.values());
        Collections.sort(sectionList, (lhs, rhs) -> {
            if (lhs.equals("Home")) return -1;
            if (rhs.equals("Home")) return 1;
            return lhs.compareToIgnoreCase(rhs);
        });
    }


    @Override
    public void onResume() {
        isNetworkUsed = model.isNetworkUsed()
                .subscribe(activity::showNetworkLoading);

        sectionSelected(model.getCurrentSectionKey());
    }

    @Override
    public void onPause() {
        RxUtil.unsubscribe(isNetworkUsed);
        RxUtil.unsubscribe(loadData);
        RxUtil.unsubscribe(loadMoreData);
    }

    @Override
    public void onDestroy() {
        // Do nothing
    }

    void titleSpinnerSectionSelected(@NonNull String sectionLabel) {
        for (String key : sections.keySet()) {
            if (sections.get(key).equals(sectionLabel)) {
                sectionSelected(key);
                break;
            }
        }
    }

    void refreshList() {
        model.reloadArticleFeed();
    }

    void loadMore() {
        msg("Load More Presenter");
        RxUtil.unsubscribe(loadMoreData);
        loadMoreData = model.loadMoreArticleList()
                .subscribe(activity::loadMore, activity::showErrorMessage);
    }

    private void sectionSelected(@NonNull String url) {
        model.selectSection(url);
        RxUtil.unsubscribe(loadData);
        loadData = model.getSelectedArticleFeed()
                .subscribe(activity::showList, activity::showErrorMessage);
    }

    private void msg(String log) {
        LogUtil.logD("List Article Presenter", log);
    }
}
