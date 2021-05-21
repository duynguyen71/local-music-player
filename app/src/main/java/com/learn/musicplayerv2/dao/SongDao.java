package com.learn.musicplayerv2.dao;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;

import com.learn.musicplayerv2.model.Song;

import java.util.List;

@Dao
public interface SongDao {

    @Query("SELECT * FROM Song")
    List<Song> findAll();

    @Query("SELECT * FROM Song WHERE songId =:songId")
    Song findSongById(int songId);

    @Insert
    void insert(Song song);

    @Query("SELECT * FROM Song WHERE playlistId=:playlistId")
    List<Song> findByPlaylistId(int playlistId);


    @Delete
    void remove(Song song);


}
