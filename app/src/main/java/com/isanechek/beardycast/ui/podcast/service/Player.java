package com.isanechek.beardycast.ui.podcast.service;

import android.content.Context;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.PowerManager;
import android.util.Log;
import com.isanechek.beardycast.data.model.article.Podcast;
import com.isanechek.beardycast.utils.NetworkUtils;
import io.realm.RealmList;

import java.io.IOException;

/**
 * Created by isanechek on 27.07.16.
 */

public class Player implements android.media.MediaPlayer.OnPreparedListener,
        android.media.MediaPlayer.OnErrorListener,
        android.media.MediaPlayer.OnCompletionListener {

    private static final String TAG = "Player";

    private Context mContext;
    private MediaPlayer mPlayer;
    private RealmList<Podcast> mPlayList;
    private boolean mStreaming = false;
    private boolean mLoading = false;
    private boolean mCurrentTrackLoaded = false;
    private StateChangedListener mStateChangedListener;

    public Player(Context context, RealmList<Podcast> episodes) {
        this.mContext = context;
        mPlayer = new MediaPlayer();
        //set player properties
        mPlayer.setWakeMode(mContext, PowerManager.PARTIAL_WAKE_LOCK);
        mPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
        //set listeners
        mPlayer.setOnPreparedListener(this);
        mPlayer.setOnCompletionListener(this);
        mPlayer.setOnErrorListener(this);

        mPlayList = episodes;
        notifyEpisodeChanged();

    }

    public void recycle() {
        if (isPlaying())
            mPlayer.stop();
        mPlayer.release();
        notifyRecycled();
    }


    @Override
    public void onCompletion(MediaPlayer mediaPlayer) {
        mPlayer.reset();
        mCurrentTrackLoaded = false;
//        notifyStateChanged();
//        notifyFinished();
        if (next()) {
            play();
        }
    }

    @Override
    public boolean onError(MediaPlayer mediaPlayer, int i, int i1) {
        this.mPlayer.reset();
        this.mCurrentTrackLoaded = false;
//        notifyStateChanged();
        return false;
    }

    @Override
    public void onPrepared(MediaPlayer mediaPlayer) {
        mLoading = false;
        mCurrentTrackLoaded = true;
//        notifyEpisodeChanged();
        if (getCurrentEpisode().getPodcastElapsedTime() < getCurrentEpisode().getPodcastTotalTime())
            seek((int) getCurrentEpisode().getPodcastElapsedTime());	//If we haven't listened to the complete Episode, seek to the elapsed time stored in the db.
        else
            seek(0);	//If we have listened to the entire Episode, the player should start over.
        play();
    }

    public Podcast getCurrentEpisode() {
        if (mPlayList.size() > 0)
            return mPlayList.get(0);
        else
            return null;
    }

    public RealmList<Podcast> getPlayList() {
        return mPlayList;
    }

    public void play() {
        if (isCurrentTrackLoaded()) {
            mPlayer.start();
            notifyStateChanged();
        }
        else {
            if (loadCurrentTrack());
        }
    }

    public void pause() {
        if (isPlaying()) {
            mPlayer.pause();
            notifyStateChanged();
        }
    }

    public void stop() {
        mPlayer.stop();
        mPlayer.reset();
        mCurrentTrackLoaded = false;
        notifyStateChanged();
    }

    public void seek(int position) {
        if (isCurrentTrackLoaded())
        {
            if (position >= mPlayer.getDuration())
                mPlayer.seekTo(mPlayer.getDuration()-100);
            else if (position < 0)
                mPlayer.seekTo(0);
            else
                mPlayer.seekTo(position);
        }
    }

    public void skipForward() {
        if (isCurrentTrackLoaded())
        {
            seek(mPlayer.getCurrentPosition() + 10000);
        }
    }

    public void skipBackward() {
        if (isCurrentTrackLoaded())
        {
            seek(mPlayer.getCurrentPosition() - 10000);
        }
    }

    public int getDuration() {
        if (isCurrentTrackLoaded())
            return mPlayer.getDuration();
        else
            return 0;
    }

    public int getPosition() {
        if (isCurrentTrackLoaded())
            return mPlayer.getCurrentPosition();
        else
            return 0;
    }

    public boolean isPlaying() {
        try {
            return mPlayer.isPlaying();
        }
        catch (IllegalStateException e) {
            return false;
        }
    }

    /**
     * @return True if the player is currently loading a track. False otherwise.
     */
    public boolean isLoading() { return mLoading; }
    /**
     * @return True if the player is currently playing a track from an internet resource. False if it's playing from a local file.
     */
    public boolean isStreaming() { return mStreaming; }
    /**
     * @return True if the current track is loaded. False otherwise.
     */
    public boolean isCurrentTrackLoaded() { return mCurrentTrackLoaded; }


    private boolean isOnline() { return NetworkUtils.isOnline(mContext); }

    /**
     * Loads the current track locally if it is available, otherwise from an internet resource.
     * @return True if the track was successfully loaded. False if it failed.
     */
    private boolean loadCurrentTrack() {
        Podcast ep = mPlayList.get(0);
        Log.i(TAG,"Loading " + ep.getPodcastTitle());
        if (!ep.isPodcastDownloaded() && !isOnline())
            return false;

        try {
            if (ep.isPodcastDownloaded()) {
                mStreaming = false;
                mPlayer.setDataSource(mContext, Uri.parse(ep.getPodcastLocalUrl()));
            }
            else {
                mStreaming = true;
                mPlayer.setDataSource(mContext, Uri.parse(ep.getPodcastUrl()));
            }
        }
        catch (IOException e) {
            return false;
        }
        catch (IllegalStateException e) {
            mPlayer.stop();
            mPlayer.reset();
            return loadCurrentTrack();
        }
        mLoading = true;
        mPlayer.prepareAsync();
        notifyStateChanged();
        return true;
    }


    private boolean next() {
        if (mPlayList.size() > 1) {
            mPlayList.remove(0);
            mCurrentTrackLoaded = false;
            Log.i(TAG, "Next.");
            return true;
        }
        else {
            Log.i(TAG, "Failed to proceed to the next Episode.");
            return false;
        }
    }

    public boolean playEpisode(Podcast ep) {
        int index = getEpisodeIndex(ep);
        if (index > 0) {
            mPlayList.remove(index);
            if (mPlayList.size() > 0) {
                mPlayList.remove(0);
                mPlayList.add(0, ep);
            }
            else
                mPlayList.add(ep); //In case we removed the only episode in the list, we can't remove[0] because there's nothing there. We just add ep.
        }
        else {
            if (mPlayList.size() > 0) {
                mPlayList.remove(0);
                mPlayList.add(0,ep);
            }
            else
                mPlayList.add(ep);
        }
        return loadCurrentTrack();
    }

    private int getEpisodeIndex(Podcast ep) {
        for (int i=0; i<mPlayList.size(); i++) {
            if (mPlayList.get(i).equals(ep))
                return i;
        }
        return -1;
    }

    private void notifyStateChanged() {
        if (mStateChangedListener != null)
        {
            if (isPlaying())
                mStateChangedListener.onStateChanged(PlayerState.PLAYING);
            else if (!isPlaying() && isCurrentTrackLoaded())
                mStateChangedListener.onStateChanged(PlayerState.PAUSED);
            else if (isLoading())
                mStateChangedListener.onStateChanged(PlayerState.LOADING);
            else
                mStateChangedListener.onStateChanged(PlayerState.STOPPED);
        }
    }


    private void notifyFinished() {
        if (mStateChangedListener != null)
            mStateChangedListener.onStateChanged(PlayerState.FINISHED);
    }

    private void notifyRecycled() {
        if (mStateChangedListener != null)
            mStateChangedListener.onStateChanged(PlayerState.RECYCLED);
    }

    private void notifyEpisodeChanged() {
        if (mStateChangedListener != null)
            mStateChangedListener.onStateChanged(PlayerState.EPISODE_CHANGED);
    }


    /**
     * Update the MediaPlayer internal playlist.
     * @param updatedPlaylist New playlist.
     */
    public void updatePlaylist(RealmList<Podcast> updatedPlaylist) {
        mPlayList = updatedPlaylist;
    }

    public interface StateChangedListener   {
        public void onStateChanged(PlayerState newPlayerState);
    }


    public void setStateChangedListener(StateChangedListener listener) {
        this.mStateChangedListener = listener;
    }


    /**
     * Enum for describing the current MediaPlayer state.
     */
    public static enum PlayerState {
        /**
         * The MediaPlayer is paused. The current track is loaded and can be resumed without loading.
         */
        PAUSED,
        /**
         * The MediaPlayer is playing.
         */
        PLAYING,
        /**
         * The MediaPlayer has been stopped.
         */
        STOPPED,
        /**
         * The MediaPlayer is loading the current track.
         */
        LOADING,
        /**
         * The MediaPlayer has finished playback and released the episode resources. Next track to play has to be loaded before playing.
         */
        FINISHED,
        /**
         * The MediaPlayer has been recycled and needs to be reinitialized if you want to play anything with it.
         */
        RECYCLED,
        /**
         * The current episode has changed.
         */
        EPISODE_CHANGED};
}
