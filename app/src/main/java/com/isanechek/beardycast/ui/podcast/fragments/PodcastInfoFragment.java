package com.isanechek.beardycast.ui.podcast.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.isanechek.beardycast.R;
import com.isanechek.beardycast.ui.podcast.PodcastActivity;
import com.isanechek.beardycast.ui.podcast.fragments.base.BaseFragment;

/**
 * Created by isanechek on 16.06.16.
 */
public class PodcastInfoFragment extends BaseFragment {
    private static final int LAYOUT = R.layout.podcast_info_fragment;

    public PodcastInfoFragment() {
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(LAYOUT, container, false);

        Button button = (Button) view.findViewById(R.id.test_click);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            }
        });

        return view;
    }
}
