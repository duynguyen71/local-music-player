package com.learn.musicplayerv2.model;

import android.os.Build;

import androidx.annotation.RequiresApi;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import java.util.Objects;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Song {

    @PrimaryKey(autoGenerate = true)
    private int id;
    private int songId;
    private String name;
    private String artist;
    private String album;
    private Long albumId;
    private String uriPath;
    private int playlistId;



    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Song song = (Song) o;
        return songId == song.songId &&
                playlistId == song.playlistId;
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    public int hashCode() {
        return Objects.hash(playlistId);
    }

    @Override
    public String toString() {
        return "Song{" +
                "id=" + songId +
                ", name='" + name + '\'' +
                ", artist='" + artist + '\'' +
                ", album='" + album + '\'' +
                ", albumId='" + albumId + '\'' +
                ", uriPath='" + uriPath + '\'' +
                '}';
    }
}
