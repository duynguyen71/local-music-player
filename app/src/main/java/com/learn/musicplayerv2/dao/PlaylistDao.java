package com.learn.musicplayerv2.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Transaction;
import androidx.room.Update;

import com.learn.musicplayerv2.entity.PlaylistEntity;
import com.learn.musicplayerv2.entity.relation.PlaylistWithSongs;

import java.util.List;

@Dao
public interface PlaylistDao {

    @Transaction
    @Query("SELECT * FROM PlaylistEntity")
    List<PlaylistWithSongs> findAllPlaylistWithSongs();

    @Query("SELECT * FROM PlaylistEntity")
    LiveData<List<PlaylistEntity>> findAllPlaylist();

    @Transaction
    @Query("SELECT * FROM PlaylistEntity WHERE playlistId=:id")
    PlaylistWithSongs findPlaylistAndSongsById(int id);

    @Query("SELECT * FROM PlaylistEntity WHERE playlistId=:playlistId")
    PlaylistWithSongs findById(int playlistId);

    @Insert
    long insertAndGetId(PlaylistEntity playlistEntity);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(PlaylistEntity playlistEntity);


    @Update
    void updatePlaylist(PlaylistEntity playlist);

    @Delete
    void deletePlaylist(PlaylistEntity playlistEntity);


}
