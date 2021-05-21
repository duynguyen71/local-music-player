package com.learn.musicplayerv2.db;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

import com.learn.musicplayerv2.dao.PlaylistDao;
import com.learn.musicplayerv2.dao.SongDao;
import com.learn.musicplayerv2.entity.PlaylistEntity;
import com.learn.musicplayerv2.model.Song;

@Database(entities = {PlaylistEntity.class, Song.class}, version = 17)
public abstract class AppDatabase extends RoomDatabase {

     static AppDatabase instance;
     static final String MY_DB = "music_db";

    public static AppDatabase getInstance(Context context) {
        if (instance == null) {
            instance = Room
                    .databaseBuilder(context, AppDatabase.class, MY_DB)
                    .fallbackToDestructiveMigration()
                    .build();

            return instance;
        }
        return instance;
    }

    public abstract SongDao songDao();

    public abstract PlaylistDao playlistDao();


}
