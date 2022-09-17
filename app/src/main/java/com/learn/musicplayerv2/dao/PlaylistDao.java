package com.learn.musicplayerv2.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Transaction;


import com.learn.musicplayerv2.entity.PlaylistEntity;
import com.learn.musicplayerv2.entity.PlaylistWithSongs;

import java.util.List;

@Dao
public interface PlaylistDao {


    @Query("SELECT * FROM PlaylistEntity")
    LiveData<List<PlaylistEntity>> findAllPlaylist();

    @Transaction
    @Query("SELECT * FROM PlaylistEntity WHERE playlistId=:id")
    PlaylistWithSongs findPlaylistAndSongsById(int id);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(PlaylistEntity playlistEntity);

    @Delete
    void deletePlaylist(PlaylistEntity playlistEntity);


}
