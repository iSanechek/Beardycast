package com.isanechek.beardycast.utils;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.telephony.TelephonyManager;
import com.isanechek.beardycast.data.Model;
import com.isanechek.beardycast.ui.podcast.service.PlayService;
import timber.log.Timber;

/**
 * Created by isanechek on 14.09.16.
 */
public class HardwareIntentReceiver extends BroadcastReceiver {

    private final PlayService playService;
    private final Model model;

    private boolean podcastPlaying = false;

    public HardwareIntentReceiver(final PlayService playService, final Model model) {
        this.playService = playService;
        this.model = model;
        Timber.tag("Hardware Intent Receiver");
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent.getAction().equals(Intent.ACTION_HEADSET_PLUG)) {
            int state = intent.getIntExtra("state", -1);
            switch (state) {
                case 0:
                    // Когда наушники выдернули
                    pauseService();
                    break;
                case 1:
                    // Когда наушники присунули
                    break;
                default:
                    break;
            }
        }
        if (intent.getAction().equals(TelephonyManager.ACTION_PHONE_STATE_CHANGED)) {
            String state = intent.getStringExtra(TelephonyManager.EXTRA_STATE);
            if (state.equals(TelephonyManager.EXTRA_STATE_RINGING)) {
                pauseService();
            } else if (state.equals(TelephonyManager.EXTRA_STATE_IDLE)) {
                if (this.playService != null && this.podcastPlaying) {
                    this.podcastPlaying = false;
                    playService.getMediaPlayer().play();
                }
            }
        }

//        if (intent.getAction().equals(ConnectivityManager.CONNECTIVITY_ACTION)) {
//
//        }

    }

    private void pauseService() {
        if (this.playService != null && this.playService.getMediaPlayer().isPlaying()) {
            this.playService.getMediaPlayer().pause();
        }
    }
}
