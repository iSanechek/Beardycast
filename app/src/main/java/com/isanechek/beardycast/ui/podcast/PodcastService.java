package com.isanechek.beardycast.ui.podcast;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Binder;
import android.os.Build;
import android.os.IBinder;
import android.os.PowerManager;

import com.isanechek.beardycast.R;
import com.isanechek.beardycast.data.model.podcast.Podcast;

import java.util.Random;

import io.realm.Realm;
import io.realm.RealmList;

import static com.isanechek.beardycast.utils.LogUtil.logD;
import static com.isanechek.beardycast.utils.LogUtil.logE;
import static com.isanechek.beardycast.utils.LogUtil.logI;

/**
 * Created by isanechek on 29.05.16.
 */
public class PodcastService extends Service implements MediaPlayer.OnPreparedListener, MediaPlayer.OnErrorListener,
        MediaPlayer.OnCompletionListener, AudioManager.OnAudioFocusChangeListener {
    private static final String TAG = "podcastService";

    public static final String ACTION_PREVIOUS = "action_previous";
    // Notification id used when starting foreground Activity
    private static final int NOTIFY_ID = 606;

    // Podcast Information Strings
    public static String podcastName = "";
    public static String podcastTitle = "";
    public static String podcastDuration = "";
    public static String podcastCurrentPosition = "";
    public static long podcastBookmarkSeekPosition = 0L;

    // MediaPlayer reference
    public static MediaPlayer mPlayer;
    // The position of current song in the Song ArrayList
    public static int podcastPosition = 0;
    // De decide between loading from bookmark or playing from start
    public static boolean loadFromBookmark;
    public static boolean exiting;
    // Ensure MediaPlayer isn;t preparing
    public static boolean isPreparing = false;
    // Millisecond value of current bookmark
    public static int millisecondToSeekTo;

    // Binder returned to Activity
    private final IBinder podcastBind = new PodcastBinder();
    public String NOTI_PLAY = "notificationPlay";
    public String NOTI_RESUME = "notificationResume";
    public String NOTI_PAUSE = "notificationPause";


    /*For test*/
    private Realm realm;

    private static RealmList<Podcast> podcasts;

    public static void updateTextViews() {

        Podcast podcast = podcasts.get(podcastPosition);

//        podcastName = podcast.getPodName();
//        podcastTitle = podcast.getPodTitle();
        podcastDuration = podcast.getDuration();

        podcastBookmarkSeekPosition = millisecondToSeekTo / podcast.getPodTotalTime();

        // Format time to minutes, secs
        long second = (millisecondToSeekTo / 1000) % 60;
        int minutes = (millisecondToSeekTo / 1000) / 60;

//        podcastCurrentPosition =

    }

    public static int getCurrentProgress() {
        double pos = mPlayer.getCurrentPosition();
        double dur = mPlayer.getDuration();
        double prog = (pos / dur) * PodcastActivity.SEEKBAR_RATIO;
        return (int) Math.round(prog * 10) / 10;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        logD(TAG, "onCreate");

        /*For test*/

        realm = Realm.getDefaultInstance();

        /*Test end*/

        // Create new MediaPlayer
        mPlayer = new MediaPlayer();

        // Set listeners as implemented methods
        mPlayer.setOnPreparedListener(this);
        mPlayer.setOnCompletionListener(this);
        mPlayer.setOnErrorListener(this);

        // Request Audio Focus to ensure app has priority when in use
        AudioManager audioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
        audioManager.requestAudioFocus(this, AudioManager.STREAM_MUSIC, AudioManager.AUDIOFOCUS_GAIN);


        // Start initialization methods
        initMusicPlayer();
    }

    /**
     * For setting up PodcastPlayer settings
     */
    public void initMusicPlayer() {
        mPlayer.setWakeMode(getApplicationContext(),
                PowerManager.PARTIAL_WAKE_LOCK);
        mPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        logD(TAG, "onDestroy");

        exiting = true;
        mPlayer.release();
        // Remove Service from foreground when closed
        stopForeground(true);
    }

    /**
     * Method for prepping MediaPlayer for new file and updating track information.
     */
    public void playPodcast() {

        logI(TAG, "Play Song");

        // Reset player
        mPlayer.reset();

        // Ensure track list isn't empty
//        if (podcasts.size() >= 0) {
//
//            // Get reference to current song
//            Podcast playSong = podcasts.get(podcastPosition);
//
//            // Get song ID, then create track URI
//            long currSong = playSong.getIdPodcast();
//            Uri trackUri = ContentUris.withAppendedId(
//                    android.provider.MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
//                    currSong);
//
//            // Try to pass new Track to MediaPlayer
//            try {
//                mPlayer.setDataSource(getApplicationContext(), trackUri);
//            } catch (Exception e) {
//                logE("MUSIC SERVICE", "Error setting data source" + e);
//            }
//
//
//            isPreparing = true;
//
//            // Prepare Asynchronously
//            mPlayer.prepareAsync();
//
//            // Update TextViews
//            updateTextViews();
//        }

    }

    /**
     * Method for passing device song list from MainActivity
     */
    public void setList(RealmList<Podcast> thePodcast) {
        podcasts = thePodcast;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        super.onStartCommand(intent, flags, startId);
        logI(TAG, "onStartCommand");
        handleIntent(intent);
        return START_STICKY;
    }

    private void handleIntent(Intent intent) {

        if (intent == null || intent.getAction() == null) {
            return;
        }

        String action = intent.getAction();
        logI(TAG, "INTENT: " + action);

        if (action.equalsIgnoreCase(ACTION_PREVIOUS)) {
            logI(TAG, "ACTION PREVIOUS!!!!!!!!!!!!");
            // mController.getTransportControls().skipToPrevious();
        }
    }

    @Override
    public void onAudioFocusChange(int focusChange) {
        logI(TAG, "AudioFocusChanged: " + focusChange);
    }

    @Override
    public void onCompletion(MediaPlayer mp) {
        logI(TAG, "onCompletion()");
        mPlayer.reset();

    }

    @Override
    public boolean onError(MediaPlayer mp, int what, int extra) {
        logE(getClass().getSimpleName(), String.format("onError() - Error(%s%s)", what, extra));

        // Specific error handling
        // if (what == MediaPlayer.MEDIA_ERROR_SERVER_DIED) {

        // Reset Player
        mp.reset();

        return false;
    }

    @Override
    public void onPrepared(MediaPlayer mp) {

        logI(TAG, "onPrepared");

        isPreparing = false;

        // If loading from a bookmark, seek to required position
        if (loadFromBookmark) {
            mPlayer.seekTo(millisecondToSeekTo);
            millisecondToSeekTo = 0;
        }

//        if (MainActivity.firstPreparedSong) {
//            MainActivity.firstPreparedSong = false;
//            mp.start();
//            mp.pause();
//
//
//        } else {
//            // Start playback
//            mp.start();
//            launchNotification(NOTI_PLAY);
//            //noinspection deprecation
////            PlayerFragment.playPause.setImageDrawable(getResources().getDrawable(R.drawable.pause));
//
//            removePauseNotification();
//
//        }

        loadFromBookmark = false;

    }

    public void launchNotification(String noti) {

        if (noti.equals(NOTI_PLAY) || noti.equals(NOTI_RESUME)) {

            // Create Intents for notification builder
            Intent notIntent = new Intent(this, PodcastActivity.class);
            notIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
            PendingIntent pendInt = PendingIntent.getActivity(this, 0,
                    notIntent, PendingIntent.FLAG_UPDATE_CURRENT);

            // Initialize builder
            Notification.Builder builder = new Notification.Builder(this);

            // Get Bitmap drawable for notification image
            Bitmap largeIconPlay = BitmapFactory.decodeResource(getResources(), R.drawable.ic_play_arrow_black_24dp);
            Bitmap largeIconPause = BitmapFactory.decodeResource(getResources(), R.drawable.ic_pause_black_24dp);

            // Build notification with required settings
            builder.setContentIntent(pendInt)
                    .setSmallIcon(R.drawable.ic_play_arrow_black_24dp)
                    .setTicker(podcastTitle)
                    .setContentTitle(podcastTitle)
                    .setContentText(podcastName)
                    .setOngoing(true);

            /*
                stopForeground(false);
                builder.setSmallIcon(R.drawable.ic_pause_dark);
                builder.setLargeIcon(largeIconPause);
                builder.setOngoing(false);
                */

            // Generic notification reference, needed to differentiate between old / new code below.
            Notification notification;





            // If running 5+, set as media controller
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {

                /* // todo ignore this ftm
                Intent intent = new Intent(MusicService.ACTION_PREVIOUS);
                PendingIntent pendingIntent = PendingIntent.getService(this, */
                //  0 /* no requestCode */, intent, 0 /* no flags */);

                //builder.setStyle(new Notification.MediaStyle());
                //builder.addAction(new Notification.Action(android.R.drawable.ic_media_previous, "Previous", pendingIntent));


                builder.setLargeIcon(largeIconPlay);
                notification = builder.build();

                // If running Android version 16+
            } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                notification = builder.build();

            } else {
                // Running version 15 or below
                //noinspection deprecation
                notification = builder.getNotification();
            }

            // Start ongoing notification
            startForeground(NOTIFY_ID, notification);

        } else if (noti.equals(NOTI_PAUSE)) {
            stopForeground(true);
            //stopForeground(false);
        }
    }

    public void removePauseNotification() {
        NotificationManager notificationManager = (NotificationManager) getApplicationContext().getSystemService(Context.NOTIFICATION_SERVICE);
//        notificationManager.cancel(PlayerFragment.mNotificationId);
    }

    /**
     * For playing chosen song
     */
    public void setSong(int songIndex) {
        mPlayer.reset();
        podcastPosition = songIndex;
        playPodcast();
    }

    /**
     * For playing song through bookmark Fragment.
     */
    public void setSongAtPos(int songIndex) {
        mPlayer.reset();
        podcastPosition = songIndex;
        playPodcast();
        loadFromBookmark = true;
    }

    /**
     * For playing song through bookmark Fragment.
     */
    public void setSongAtPosButDontPlay(int songIndex) {
        mPlayer.reset();
        podcastPosition = songIndex;
        playPodcast();
        loadFromBookmark = true;
    }

    /**
     * MediaPlayer Methods
     */
    public boolean isPng() {

        return mPlayer.isPlaying();
    }

    public void pausePlayer() {
        mPlayer.pause();
        launchNotification(NOTI_PAUSE);
    }

    public void seek(int position) {
        mPlayer.seekTo((getLength() / PodcastActivity.SEEKBAR_RATIO) * position);
    }

    public int getLength() {
        return mPlayer.getDuration();
    }

    public void resume() {
        mPlayer.start();
        launchNotification(NOTI_RESUME);
    }

    public void playPrev() {
        podcastPosition--;
        if (podcastPosition < 0) podcastPosition = podcasts.size() - 1;
        playPodcast();
    }

    // play current song again
    public void playCurrent() {
        if (podcastPosition == podcasts.size()) podcastPosition = 0;
        playPodcast();
    }

    //skip to next
    public void playNext() {
        podcastPosition++;
        if (podcastPosition == podcasts.size()) podcastPosition = 0;
        playPodcast();
    }

    // Find random song
    public void playRandom() {

        int oldPos = podcastPosition;

        podcastPosition = new Random().nextInt(podcasts.size() + 1);

        for (int j = 0; j < 5; j++) {
            if (oldPos == podcastPosition) {
                podcastPosition = new Random().nextInt(podcasts.size() + 1);
            } else {
                break;
            }
        }

        if (podcastPosition == podcasts.size()) {
            podcastPosition = 0;
        }

        playPodcast();
    }

    @Override
    public IBinder onBind(Intent intent) {
        logI(TAG, "onBind");
        return podcastBind;
    }

    @Override
    public boolean onUnbind(Intent intent) {
        logI(TAG, "onUnbind");
        //mPlayer.stop();
        //mPlayer.reset();
        return false;
    }

    /**
     * Required Bind Methods
     */
    public class PodcastBinder extends Binder {
        PodcastService getService() {
            return PodcastService.this;
        }
    }
}
