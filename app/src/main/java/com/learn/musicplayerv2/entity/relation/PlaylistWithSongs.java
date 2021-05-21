package com.learn.musicplayerv2.entity.relation;


import androidx.room.Embedded;
import androidx.room.Relation;

import com.learn.musicplayerv2.entity.PlaylistEntity;
import com.learn.musicplayerv2.model.Song;

import java.util.List;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
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
