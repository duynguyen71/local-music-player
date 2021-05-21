package com.learn.musicplayerv2.entity;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
public class PlaylistEntity {

    @PrimaryKey(autoGenerate = true)
    public int playlistId;

    public String playlistName;

    public PlaylistEntity(String playlistName) {
        this.playlistName = playlistName;
    }

    @Override
    public String toString() {
        return "PlaylistEntity{" +
                "playlistId=" + playlistId +
                ", playlistName='" + playlistName + '\'' +
                '}';
    }
}
