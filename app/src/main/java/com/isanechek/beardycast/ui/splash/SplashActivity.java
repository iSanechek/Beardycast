package com.isanechek.beardycast.ui.splash;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;

import com.isanechek.beardycast.R;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by isanechek on 30.05.16.
 */
public class SplashActivity extends AppCompatActivity {
    private static final int LAYOUT = R.layout.splash_activity;
    private static final String TAG = "SplashActivity";

    private SplashPresenter presenter;

    @BindView(R.id.splash_progress)
    ProgressBar progress;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(LAYOUT);
        ButterKnife.bind(this);
        msg("HELLO");
        presenter = new SplashPresenter(this);

    }

    @Override
    protected void onResume() {
        super.onResume();
        presenter.onResume();
        msg("onResume");
    }

    @Override
    protected void onPause() {
        super.onPause();
        presenter.onPause();
        msg("onPause");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        presenter.onDestroy();
        msg("onDestroy");
    }

    public void showError() {

    }

    public void showProgress(boolean show) {
        progress.setVisibility(show ? View.VISIBLE : View.INVISIBLE);
    }

    private void msg(String s) {
        Log.d(TAG, s);
    }
    private void msge(String s) {
        Log.e(TAG, s);
    }
}
