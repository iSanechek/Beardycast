package com.isanechek.beardycast.ui.podcast.service;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.Handler;
import android.os.IBinder;
import android.support.annotation.Nullable;

import com.isanechek.beardycast.data.Model;
import com.isanechek.beardycast.data.model.article.Podcast;
import com.isanechek.beardycast.ui.podcast.service.Player.PlayerState;
import io.realm.RealmList;
import timber.log.Timber;

import java.util.ArrayList;
import java.util.List;

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

    private Model model;
    private String podcastId;
    private Podcast podcast;

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
        Timber.tag("Play Service");
        Timber.d("onCreate");
        mHandler = new Handler();
        model = Model.getInstance();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (player != null) {
            player.recycle();
        }
        stopForeground(true);
        Timber.d("onDestroy");
    }


    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (intent != null && intent.getAction() != null) {
            if (intent.getAction().equals("play")) {
                //mNotification.showNotify(this);
                player.play();
                Timber.d("Play");
            } else if (intent.getAction().equals("pause")) {
                //mNotification.pauseNotify(this);
                player.pause();
                Timber.d("Pause");
            } else if (intent.getAction().equals("forward")) {
                player.skipForward();
                Timber.d("Skip Forward");
            } else if (intent.getAction().equals("backward")) {
                player.skipBackward();
                Timber.d("Skip Backward");
            } else if (intent.getAction().equals("close")) {
                player.stop();
                Timber.d("Stop");
                this.stopForeground(true);
            }
        }
        return super.onStartCommand(intent, flags, startId);
    }

    public Player getMediaPlayer() { return player; };

    public void playEpisode(Podcast ep) {
        player.playEpisode(ep);
    }

    @Override
    public void onStateChanged(com.isanechek.beardycast.ui.podcast.service.Player.PlayerState newPlayerState) {
        switch (newPlayerState) {
            case PLAYING:
                notification.showNotify(this);
                mHandler.post(UpdateRunnable);
                Timber.d("Playing");
                break;
            case PAUSED:
                notification.pauseNotify(this);
                Timber.d("Paused");
                break;
            case STOPPED:
                notification.pauseNotify(this);
                Timber.d("Stoped");
                break;
            case LOADING:
                Timber.d("Loading");
                break;
            case FINISHED:
                mIgnoreNextPlaylistUpdate = true;
                model.markEpisodeAsListened(podcastId);
                notification.pauseNotify(this);
                Timber.d("Finished");
                break;
            case EPISODE_CHANGED:
                final Podcast podcast = player.getCurrentEpisode();
                if (podcast != null) {
                    if (podcast.getPodcastTotalTime() == 0 || podcast.getPodcastTotalTime() == 100) {
                        podcast.setPodcastTotalTime(player.getDuration());
//                        model.updateEpisode(player.getCurrentEpisode());
                    }
                    notification = new ServiceNotification(this);
                    Timber.d("Episode Changed");
                }
                break;
        }

        if (mStateChangedListener != null) {
            mStateChangedListener.onStateChanged(newPlayerState);
        }
    }

    public void setPodcastToservice(Podcast podcast) {
        this.podcast = podcast;
        RealmList<Podcast> cache = new RealmList<>();
        String podId = podcast.getPodcastId();
        if (!isHave(podId, cache)) {
            cache.add(podcast);
        }
        player = new Player(this, cache);
        player.setStateChangedListener(this);
        onStateChanged(PlayerState.EPISODE_CHANGED);
    }

    public Podcast getPodcastEpisode() {
        return podcast;
    }

    private Runnable UpdateRunnable= new Runnable() {
        @Override
        public void run() {
            if (player.isPlaying()) {
                Podcast episode = player.getCurrentEpisode();
                if ((player.getPosition() - saveIntervalMillis) <= 0) {
                    episode.setPodcastElapsedTime(0);
                } else {
                    episode.setPodcastElapsedTime(player.getPosition() - saveIntervalMillis);
                    model.updatePodcastElapsedTime(episode.getPodcastElapsedTime());
                    mHandler.postDelayed(UpdateRunnable, saveIntervalMillis);
                }
            } else {
                mHandler.removeCallbacks(this);
            }
        }
    };

    private boolean isHave(String podcastId, RealmList<Podcast> podcasts) {
        List<String> cache = new ArrayList<>();
        if (podcasts.size() == 0) {
            Timber.d("podcasts size null");
            String podId = "podcast id add: " + podcastId;
            Timber.d(podId);
            cache.add(podcastId);
        } else {
            for (Podcast podcast : podcasts) {
                if (podcast.getPodcastId().equals(podcastId)) {
                    cache.add(podcastId);
                }
            }
        }
        return cache.size() != 0;
    }

    public interface StateChangedListener {
        void onStateChanged(PlayerState newPlayerState);
    }

    public class PlayServiceBinder extends Binder {
        public PlayService getService() {
            return PlayService.this;
        }
    }
}
