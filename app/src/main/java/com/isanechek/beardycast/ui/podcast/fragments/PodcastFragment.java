package com.isanechek.beardycast.ui.podcast.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.isanechek.beardycast.R;
import com.isanechek.beardycast.ui.podcast.fragments.base.BaseFragment;
import com.isanechek.beardycast.ui.podcast.fragments.utils.FLifecycle;

/**
 * Created by isanechek on 16.06.16.
 */
public class PodcastFragment extends BaseFragment implements FLifecycle {
    private static final int LAYOUT = R.layout.podcast_control_fragment;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(LAYOUT, container, false);
        return view;
    }

    @Override
    public void onPauseFragment() {

    }

    @Override
    public void onResumeFragment() {

    }
}
