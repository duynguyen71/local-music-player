package com.learn.musicplayerv2.entity;


import androidx.room.Embedded;
import androidx.room.Entity;
import androidx.room.Relation;

import com.learn.musicplayerv2.entity.PlaylistEntity;
import com.learn.musicplayerv2.model.Song;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PlaylistWithSongs {

    @Embedded
    public PlaylistEntity playlist;

    @Relation(parentColumn = "playlistId"
            , entityColumn = "playlistId"
            , entity = Song.class)
    public List<Song> songs;

    @Override
    public String toString() {
        return "PlaylistWithSongs{" +
                "playlist=" + playlist +
                ", songs=" + songs +
                '}' + "\n";
    }
}
