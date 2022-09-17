package com.learn.musicplayerv2.entity;

import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
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
