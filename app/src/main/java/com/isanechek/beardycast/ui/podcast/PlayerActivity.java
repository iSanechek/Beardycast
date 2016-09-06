package com.isanechek.beardycast.ui.podcast;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.transition.Transition;
import android.view.View;

import com.isanechek.beardycast.R;
import com.isanechek.beardycast.ui.adapters.TransitionAdapter;
import com.isanechek.beardycast.ui.widget.CoverView;

public class PlayerActivity extends AppCompatActivity {
    private static final int LAYOUT = R.layout.player_activity;
    private static final String TAG = "Player Activity";

    private CoverView mCoverView;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(LAYOUT);

        mCoverView = (CoverView) findViewById(R.id.cover);
        mCoverView.setCallbacks(this::supportFinishAfterTransition);

        getWindow().getSharedElementEnterTransition().addListener(new TransitionAdapter() {
            @Override
            public void onTransitionEnd(Transition transition) {
//                play();
                mCoverView.start();
            }
        });
    }

    @Override
    public void onBackPressed() {
        onFabClick(null);
    }

    public void onFabClick(View view) {
//        pause();
        mCoverView.stop();
    }
}
