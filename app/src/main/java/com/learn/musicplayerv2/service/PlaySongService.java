package com.learn.musicplayerv2.service;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Build;
import android.os.IBinder;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.core.content.ContextCompat;

import com.learn.musicplayerv2.AppCache;
import com.learn.musicplayerv2.AppViewModel;
import com.learn.musicplayerv2.R;
import com.learn.musicplayerv2.model.Song;
import com.learn.musicplayerv2.utils.AppUtil;

import java.util.List;

//@NoArgsConstructor
public class PlaySongService extends Service {

    MediaPlayer player = null;
    public static PlaySongService instance;
    int playingPosition;
    Context applicationContext;
    Song playingSong;
    List<Song> playList = null;
    AppViewModel viewModel;

    public PlaySongService() {
    }

    public static PlaySongService getInstance() {
        return instance;
    }

    public static void setInstance(PlaySongService service) {
        instance = service;
    }


    public PlaySongService(Context applicationContext) {
        this.applicationContext = applicationContext;
    }

    @Override
    public void onCreate() {
        if (instance == null) {
            setInstance(new PlaySongService(getApplicationContext()));
        }
        super.onCreate();
    }

    //call when change song
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        MyNotification myNotification = new MyNotification();
        //for android 8.0 above
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            myNotification.createNotificationChanel(getSystemService(NotificationManager.class));
        }

        if (instance.getPlayer().isPlaying()) {
            myNotification.setPlayIcon(R.drawable.ic_pause_24);
        } else {
            myNotification.setPlayIcon(R.drawable.ic_play_arrow_24);
        }

        Notification notification = myNotification.createNotification(getApplicationContext(), instance.getPlayingSong());
        startForeground(1, notification);
        return START_STICKY;
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public void play(int songPosition) {
        if (this.player != null) {
            removePlayer();
        }
        Song song = AppUtil.findSongByPosition(AppCache.PLAYING_PLAYLIST, songPosition);
        viewModel = AppViewModel.getInstance();
        player = MediaPlayer.create(applicationContext, Uri.parse(song.getUriPath()));
        instance.setPlayingSong(song);
        instance.setPlayingPosition(songPosition);


        viewModel.setMediaPlayer(player);
        viewModel.setPlayingSong(playingSong);
//        viewModel.setPlayingPosition(songPosition);
        player.start();
//        start service
        ContextCompat.startForegroundService(applicationContext, new Intent(applicationContext, PlaySongService.class));
        player.setOnCompletionListener(mp -> next());
        return;

    }

    public void removePlayer() {
        this.player.stop();
        this.player.release();
        this.player = null;
    }

    public void setPlayingPosition(int playingPosition) {
        this.playingPosition = playingPosition;
        return;
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public void next() {
//        if (playingPosition < AppCache.PLAYING_PLAYLIST.size() - 1) {
//            try {
//                play(playingPosition + 1);
//                return;
//            } catch (Exception e) {
//                play(playingPosition + 2);
//                return;
//            }
//        }
//        play(0);
        if (playingPosition == AppCache.PLAYING_PLAYLIST.size() - 1) {
            play(0);
        } else {
            int n = playingPosition + 1;
            play(n);
        }
        return;
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public void prev() {
        if (playingPosition >= 1) {
            play(playingPosition - 1);
        } else {
            play(AppCache.PLAYING_PLAYLIST.size() - 1);
        }
    }

    public boolean pause() {
        if (instance.getPlayer().isPlaying()) {
            instance.getPlayer().pause();
            ContextCompat.startForegroundService(applicationContext, new Intent(applicationContext, PlaySongService.class));
            return true;
        }
        instance.getPlayer().start();
        ContextCompat.startForegroundService(applicationContext, new Intent(applicationContext, PlaySongService.class));
        return false;

    }

    public Song getPlayingSong() {
        return playingSong;
    }

    public void setPlayingSong(Song playingSong) {
        this.playingSong = playingSong;
    }

    @Override
    public void onDestroy() {

        super.onDestroy();
        if (this.getPlayer() != null) {
            removePlayer();
            AppViewModel.setInstance(null);
        }
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    public MediaPlayer getPlayer() {
        return this.player;
    }

    public int getPlayingPosition() {
        return playingPosition;
    }

    public void setPlayList(List<Song> playList) {
        this.playList = playList;
    }
}