package com.learn.musicplayerv2;

import android.media.MediaPlayer;
import android.util.Log;
import android.widget.Toast;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.learn.musicplayerv2.model.Song;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@Getter
@Setter
public class AppViewModel extends ViewModel {

     MutableLiveData<MediaPlayer> mediaPlayer = new MutableLiveData<MediaPlayer>();
     MutableLiveData<Song> currentSong = new MutableLiveData<Song>();
     final String TAG = getClass().getSimpleName();
//     int currentPos;
     static AppViewModel appViewModel = null;

    public static AppViewModel getInstance() {
        return appViewModel;
    }

    public static void setInstance(AppViewModel instance) {
        appViewModel = instance;
    }


    @Override
    protected void onCleared() {
        super.onCleared();
    }

    //getters setters
    public MutableLiveData<MediaPlayer> getMPLiveDate() {
        return mediaPlayer;
    }

    public MediaPlayer getMediaPlayer() {
        return mediaPlayer.getValue();
    }

    public MutableLiveData<Song> getCurrentSong() {
        return currentSong;
    }

    public void setMediaPlayer(MediaPlayer mediaPlayer) {
        this.mediaPlayer.setValue(mediaPlayer);
    }

    public void setPlayingSong(Song song) {
        currentSong.setValue(song);
    }

//    public void setPlayingPosition(int currentPos) {
//        this.currentPos = currentPos;
//    }

}