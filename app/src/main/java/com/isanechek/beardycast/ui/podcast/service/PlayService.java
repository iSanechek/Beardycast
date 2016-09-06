package com.isanechek.beardycast.ui.podcast.service;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.Handler;
import android.os.IBinder;
import android.support.annotation.Nullable;

import com.isanechek.beardycast.data.model.podcast.Episode;
import com.isanechek.beardycast.ui.podcast.service.Player.PlayerState;

/**
 * Created by isanechek on 22.07.16.
 */

public class PlayService extends Service implements Player.StateChangedListener {
    private static final String TAG = "PlayService";

    private final int saveIntervalMillis = 15000;

    private final IBinder iBinder = new PlayServiceBinder();
    private ServiceNotification notification;
    private Player player;
    private Handler mHandler;							//Handler object (for threading)
    private boolean mTimerSet = false;
    private boolean mIgnoreNextPlaylistUpdate = false;
//    private SleepTimerListener mSleepTimerListener;
    private StateChangedListener mStateChangedListener;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return iBinder;
    }

    @Override
    public boolean onUnbind(Intent intent) {
        if (player != null)
            player.recycle();
        return false;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        mHandler = new Handler();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (player != null) {
            player.recycle();
        }
    }


    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (intent != null && intent.getAction() != null) {
            if (intent.getAction().equals("play")) {
                //mNotification.showNotify(this);
                player.play();
            } else if (intent.getAction().equals("pause")) {
                //mNotification.pauseNotify(this);
                player.pause();
            } else if (intent.getAction().equals("forward")) {
                player.skipForward();
            } else if (intent.getAction().equals("backward")) {
                player.skipBackward();
            } else if (intent.getAction().equals("close")) {
                player.stop();
                this.stopForeground(true);
            }
        }

        return super.onStartCommand(intent, flags, startId);
    }

    public Player getMediaPlayer() { return player; };

    public void playEpisode(Episode ep) {
        player.playEpisode(ep);
    }

    @Override
    public void onStateChanged(com.isanechek.beardycast.ui.podcast.service.Player.PlayerState newPlayerState) {
        switch (newPlayerState) {
            case PLAYING:
                notification.showNotify(this);
                mHandler.post(UpdateRunnable);
                break;
            case PAUSED:
                notification.pauseNotify(this);
                break;
            case STOPPED:
                notification.pauseNotify(this);
                break;
            case LOADING:
                break;
            case FINISHED:
                mIgnoreNextPlaylistUpdate = true;
                // Отметить как прослушано
                notification.pauseNotify(this);
                break;
            case EPISODE_CHANGED:



                break;
            default:
                break;
        }

        if (mStateChangedListener != null) {
            mStateChangedListener.onStateChanged(newPlayerState);
        }
    }


    private Runnable UpdateRunnable= new Runnable() {
        @Override
        public void run() {
            if (player.isPlaying()) {
                Episode episode = player.getCurrentEpisode();
                if ((player.getPosition() - saveIntervalMillis) <= 0) {
                    episode.setPodElapsedTime(0);
                } else {
                    episode.setPodElapsedTime(player.getPosition() - saveIntervalMillis);
                    //Тут надо обновить в БД

                    mHandler.postDelayed(UpdateRunnable, saveIntervalMillis);
                }
            } else {
                mHandler.removeCallbacks(this);
            }
        }
    };


    public interface StateChangedListener {
        public void onStateChanged(PlayerState newPlayerState);
    }

    public class PlayServiceBinder extends Binder {
        public PlayService getService() {
            return PlayService.this;
        }
    }
}
