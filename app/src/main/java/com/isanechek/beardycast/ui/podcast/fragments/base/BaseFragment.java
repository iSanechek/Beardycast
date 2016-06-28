package com.isanechek.beardycast.ui.podcast.fragments.base;

import android.app.Fragment;
import android.content.Context;
import android.view.View;

/**
 * Created by isanechek on 16.06.16.
 */
public class BaseFragment extends Fragment {
    private String title;
    protected Context context;
    protected View view;

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }
}
