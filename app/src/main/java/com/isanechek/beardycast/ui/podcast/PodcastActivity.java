package com.isanechek.beardycast.ui.podcast;

import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatImageButton;
import android.support.v7.widget.AppCompatSeekBar;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import com.isanechek.beardycast.R;
import com.isanechek.beardycast.data.Model;
import com.isanechek.beardycast.ui.podcast.service.PlayService;
import com.isanechek.beardycast.utils.HardwareIntentReceiver;
import timber.log.Timber;

/**
 * Created by isanechek on 13.09.16.
 */
public class PodcastActivity extends AppCompatActivity {
    private static final int LAYOUT = R.layout.podcast_activity;

    public static Intent startPodcastActivity(Context context, String idPodcast) {
        Intent intent = new Intent(context, PodcastActivity.class);
        intent.putExtra("idPodcast", idPodcast);
        context.startActivity(intent);
        return intent;
    }

    private AppCompatImageButton close_btn;
    private ImageView cover_img;
    private TextView elapsed_time_tv;
    private TextView total_time_tv;
    private TextView title_tv;
    private AppCompatImageButton replay_btn;
    private AppCompatImageButton play_pause_btn;
    private AppCompatImageButton forward_btn;
    private AppCompatSeekBar seekBar;

    private Model model;
    private String podcastId;

    private PlayService playService;
    private ServiceConnection serviceConnection;
    private boolean isPlayServiceBound = false;
    private HardwareIntentReceiver hardwareIntentReceiver;
    private Intent playIntent;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Timber.tag("Podcast Activity");
        Timber.d("onCreate");
        setContentView(LAYOUT);
        initView();
        model = Model.getInstance();

        podcastId = getIntent().getExtras().getString("idPodcast");
        Timber.d(podcastId);

//        ini

    }

    @Override
    protected void onPostCreate(@Nullable Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);

    }



    private void initView() {
        close_btn = (AppCompatImageButton) findViewById(R.id.pa_close_btn);
        close_btn.setOnClickListener(v -> finishAfterTransition());
        cover_img = (ImageView) findViewById(R.id.pa_cover_image);
        elapsed_time_tv = (TextView) findViewById(R.id.pa_elapsed_time);
        total_time_tv = (TextView) findViewById(R.id.pa_total_time);
        title_tv = (TextView) findViewById(R.id.pa_title);
        replay_btn = (AppCompatImageButton) findViewById(R.id.pa_replay_btn);
        play_pause_btn = (AppCompatImageButton) findViewById(R.id.pa_play_pause_btn);
        forward_btn = (AppCompatImageButton) findViewById(R.id.pa_btn_forward);
        seekBar = (AppCompatSeekBar) findViewById(R.id.pa_seek_bar);

    }
}
