package com.isanechek.beardycast.ui.podcast.service;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.support.v4.app.NotificationCompat;
import android.widget.RemoteViews;

import com.isanechek.beardycast.R;
import com.isanechek.beardycast.ui.podcast.PlayerActivity;

/**
 * Created by isanechek on 28.07.16.
 */

public class ServiceNotification {

    private final static int ID_REMOTESERVICE = 113;
    private RemoteViews mSmallView, mBigView;
    private Notification mNotification;
    private NotificationCompat.Builder mBuilder;
    private Intent _playIntent, navigateIntent, forwardIntent, backwardIntent, closeIntent;
    private PendingIntent playPendingIntent, navigatePendingIntent, forwardPendingIntent, backwardPendingIntent, closePendingIntent;


    public ServiceNotification(PlayService context) {
        navigateIntent = new Intent(context, PlayerActivity.class);
        navigateIntent.setAction(PlayerActivity.ACTION_NAVIGATE_PLAYER);
        navigatePendingIntent = PendingIntent.getActivity(context, 0, navigateIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        _playIntent = new Intent(context, PlayService.class);
        _playIntent.setAction("play");
        playPendingIntent = PendingIntent.getService(context, 0, _playIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        forwardIntent = new Intent(context, PlayService.class);
        forwardIntent.setAction("forward");
        forwardPendingIntent = PendingIntent.getService(context, 0, forwardIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        backwardIntent = new Intent(context, PlayService.class);
        backwardIntent.setAction("backward");
        backwardPendingIntent = PendingIntent.getService(context, 0, backwardIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        closeIntent = new Intent(context, PlayService.class);
        closeIntent.setAction("close");
        closePendingIntent = PendingIntent.getService(context, 0, closeIntent, PendingIntent.FLAG_UPDATE_CURRENT);

//        final Feed currentFeed = context.getDataProvider().getFeed(context.getMediaPlayer().getCurrentEpisode().getFeedId());
//        final Episode episode = context.

//        final Bitmap image = BitmapFactory.decodeFile(currentFeed.getSmallImageURL().replace("file://",""));

        mSmallView = new RemoteViews(context.getPackageName(), R.layout.service_notification);
        mSmallView.setOnClickPendingIntent(R.id.service_notification_playpausebutton, playPendingIntent);
        mSmallView.setOnClickPendingIntent(R.id.service_notification_forwardbutton, forwardPendingIntent);
        //mSmallView.setImageViewUri(R.id.service_notification_image, Uri.parse(""));
        mSmallView.setImageViewBitmap(R.id.service_notification_image, image);
        mSmallView.setTextViewText(R.id.service_notification_title, context.getMediaPlayer().getCurrentEpisode().getTitle());
        mSmallView.setTextViewText(R.id.service_notification_subtitle, currentFeed.getTitle());

        mBigView = new RemoteViews(context.getPackageName(), R.layout.service_notification_expanded);
        mBigView.setOnClickPendingIntent(R.id.service_notification_playpausebutton, playPendingIntent);
        mBigView.setOnClickPendingIntent(R.id.service_notification_forwardbutton, forwardPendingIntent);
        mBigView.setOnClickPendingIntent(R.id.service_notification_backwardbutton, backwardPendingIntent);
        //mBigView.setOnClickPendingIntent(R.id.service_notification_closeButton, closePendingIntent);
        //mBigView.setImageViewUri(R.id.service_notification_image, Uri.parse(""));
        mBigView.setImageViewBitmap(R.id.service_notification_image, image);
        mBigView.setTextViewText(R.id.service_notification_title, context.getMediaPlayer().getCurrentEpisode().getTitle());
        mBigView.setTextViewText(R.id.service_notification_subtitle, currentFeed.getTitle());
        //mBigView.setTextViewText(R.id.service_notification_apptitle, "by " + $context.getText(R.string.app_name));

        Resources res = context.getResources();
        mBuilder = new NotificationCompat.Builder(context).setSmallIcon(R.mipmap.icon)
                .setTicker(res.getString(R.string.app_name))
                .setContentTitle(res.getString(R.string.app_name))
                .setCategory(Notification.CATEGORY_TRANSPORT)
                .setOngoing(true)
                .setDeleteIntent(closePendingIntent)
                .setContentIntent(navigatePendingIntent);
        mNotification = mBuilder.build();
        mNotification.contentView = mSmallView;
        mNotification.bigContentView = mBigView;
    }

    public void showNotify(Service context) {
        if(Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
            mSmallView.setInt(R.id.service_notification_playpausebutton, "setImageResource", R.drawable.ic_pause_white_24dp);
            mBigView.setInt(R.id.service_notification_playpausebutton, "setImageResource", R.drawable.ic_pause_white_24dp);
        } else {
            mSmallView.setInt(R.id.service_notification_playpausebutton, "setImageResource", R.drawable.ic_pause_black_24dp);
            mBigView.setInt(R.id.service_notification_playpausebutton, "setImageResource", R.drawable.ic_pause_black_24dp);
        }

        _playIntent.setAction("pause");
        playPendingIntent = PendingIntent.getService(context, 0, _playIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        mSmallView.setOnClickPendingIntent(R.id.service_notification_playpausebutton, playPendingIntent);
        mBigView.setOnClickPendingIntent(R.id.service_notification_playpausebutton, playPendingIntent);
        Resources res = context.getResources();
        mBuilder = new NotificationCompat.Builder(context).setSmallIcon(R.drawable.ic_launcher)
                .setTicker(res.getString(R.string.app_name))
                .setContentTitle(res.getString(R.string.app_name))
                .setCategory(Notification.CATEGORY_TRANSPORT)
                .setOngoing(true)
                .setDeleteIntent(closePendingIntent)
                .setContentIntent(navigatePendingIntent);
        mNotification = mBuilder.build();
        mNotification.contentView = mSmallView;
        mNotification.bigContentView = mBigView;
        context.startForeground(ID_REMOTESERVICE, mNotification);
    }


    public void pauseNotify(Service context)
    {
        if(Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
            mSmallView.setInt(R.id.service_notification_playpausebutton, "setImageResource", R.drawable.ic_play_arrow_white_24dp);
            mBigView.setInt(R.id.service_notification_playpausebutton, "setImageResource", R.drawable.ic_play_arrow_white_24dp);
        }
        else {
            mSmallView.setInt(R.id.service_notification_playpausebutton, "setImageResource", R.drawable.ic_play_arrow_black_24dp);
            mBigView.setInt(R.id.service_notification_playpausebutton, "setImageResource", R.drawable.ic_play_arrow_black_24dp);
        }


        _playIntent.setAction("play");
        playPendingIntent = PendingIntent.getService(context, 0, _playIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        mSmallView.setOnClickPendingIntent(R.id.service_notification_playpausebutton, playPendingIntent);
        mBigView.setOnClickPendingIntent(R.id.service_notification_playpausebutton, playPendingIntent);
        Resources res = context.getResources();
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context).setSmallIcon(R.drawable.ic_launcher)
                .setTicker(res.getString(R.string.app_name))
                .setContentTitle(res.getString(R.string.app_name))
                .setCategory(Notification.CATEGORY_TRANSPORT)
                .setOngoing(false)
                .setDeleteIntent(closePendingIntent)
                .setContentIntent(navigatePendingIntent);
        mNotification = builder.build();
        mNotification.contentView = mSmallView;
        mNotification.bigContentView = mBigView;
        context.stopForeground(false);
        NotificationManager mNotificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        mNotificationManager.notify(ID_REMOTESERVICE, mNotification);   //Replace the foreground service notification with the new dismissable notification.
    }

}
