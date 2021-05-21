package com.learn.musicplayerv2.service;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;

import androidx.annotation.RequiresApi;
import androidx.core.app.NotificationCompat;

import com.learn.musicplayerv2.MainActivity;
import com.learn.musicplayerv2.R;
import com.learn.musicplayerv2.model.Song;
import com.learn.musicplayerv2.utils.AppUtil;

import lombok.Getter;
import lombok.Setter;

public class MyNotification {

    static final String CHANNEL_ID = "ForegroundServiceChannel";
    public static final String ACTION_PLAY = "actionPlay";
    public static final String ACTION_PREV = "actionPrev";
    public static final String ACTION_NEXT = "actionNext";

    @Getter
    @Setter
    private int playIcon;

    @RequiresApi(api = Build.VERSION_CODES.O)
    public void createNotificationChanel(NotificationManager notificationManager) {
        NotificationChannel serviceChannel = new NotificationChannel(
                CHANNEL_ID,
                "Foreground Service Channel",
                NotificationManager.IMPORTANCE_LOW
        );
        notificationManager.createNotificationChannel(serviceChannel);
    }

    public Notification createNotification(Context context, Song playingSong) {
        String title = "KD PLayer";
        String text = "service";
        Bitmap largeIcon = null;
        if (playingSong != null) {
            title = playingSong.getName();
            text = playingSong.getArtist();
            largeIcon = AppUtil.toBitmap(context, playingSong.getAlbumId());
            if (largeIcon == null) {
                largeIcon = BitmapFactory.decodeResource(context.getResources(), R.mipmap.ic_launcher_foreground);
            }
        }

        //prev
        Intent prevIntent = new Intent(context, NotificationActionService.class).setAction(ACTION_PREV);
        PendingIntent prevPendingIntent = PendingIntent.getBroadcast(context, 0, prevIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        //next
        Intent nextIntent = new Intent(context, NotificationActionService.class).setAction(ACTION_NEXT);
        PendingIntent nextPendingIntent = PendingIntent.getBroadcast(context, 0, nextIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        //play
        Intent playIntent = new Intent(context, NotificationActionService.class).setAction(ACTION_PLAY);
        PendingIntent playPendingIntent = PendingIntent.getBroadcast(context, 0, playIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        Notification notification = new NotificationCompat.Builder(context, CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_baseline_music_note_24)
                .setContentTitle(title)
                .setContentText(text)
                .setShowWhen(false)
//                .setProgress(1000,500,true)
                .setLargeIcon(largeIcon)
                .addAction(R.drawable.ic_previous_24, "prev", prevPendingIntent)
                .addAction(getPlayIcon(), "play", playPendingIntent)
                .addAction(R.drawable.ic_next_24, "next", nextPendingIntent)
                .setStyle(
                        new androidx.media.app.NotificationCompat.MediaStyle()
                                .setShowActionsInCompactView(0, 1, 2)
                )
                .setPriority(NotificationCompat.PRIORITY_LOW)
                .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
                .setContentIntent(PendingIntent.getActivity(context,0,new Intent(context, MainActivity.class),0,null))
                .build();

        return notification;
    }
}
