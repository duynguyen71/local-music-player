package com.learn.musicplayerv2.utils;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.provider.MediaStore;
import android.util.Log;

import com.learn.musicplayerv2.AppCache;
import com.learn.musicplayerv2.model.Song;

import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class AppUtil {
    static final String TAG = AppUtil.class.getName();

    public static List<Song> findAllLibrarySong(Context context) {
        Log.i(TAG, "findAllLibrarySong: ");
        List<Song> rs = new ArrayList<>();
        Uri uri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
        ContentResolver resolver = context.getContentResolver();
        Cursor c = resolver.query(uri, null, null, null, null, null);
        if (c.moveToFirst()) {
            do {
                Song song = new Song();

                int indexCol = c.getColumnIndexOrThrow(MediaStore.Audio.Media._ID);
                int nameCol = c.getColumnIndexOrThrow(MediaStore.Audio.Media.TITLE);
                int artistCol = c.getColumnIndexOrThrow(MediaStore.Audio.Media.ARTIST);
                int albumCol = c.getColumnIndexOrThrow(MediaStore.Audio.Media.ALBUM);
                int albumIdCol = c.getColumnIndexOrThrow(MediaStore.Audio.Media.ALBUM_ID);
                int dataCol = c.getColumnIndexOrThrow(MediaStore.Audio.Media.DATA);

                song.setSongId(c.getInt(indexCol));
                song.setName(c.getString(nameCol));
                song.setArtist(c.getString(artistCol));
                song.setAlbum(c.getString(albumCol));
                song.setAlbumId(c.getLong(albumIdCol));
                song.setUriPath(String.valueOf(Uri.fromFile(new File(c.getString(dataCol)))));

                rs.add(song);

            } while (c.moveToNext());
        }
        Log.i(TAG, "findAllLibrarySong: "+rs);
        c.close();
        return rs;

    }

    @Nullable
    public static Bitmap toBitmap(Context context, Long alID) {
        Uri sArtworkUri = Uri
                .parse("content://media/external/audio/albumart");
        Uri albumArtUri = ContentUris.withAppendedId(sArtworkUri, alID);
        Bitmap bitmap;
        try {
            bitmap = MediaStore.Images.Media.getBitmap(context.getContentResolver(), albumArtUri);
            return bitmap;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }


    public static Song findSongByPosition(List<Song> songs, int pos) {
        for (int i = 0; i < songs.size(); i++) {
            if (i == pos) {
                return songs.get(i);
            }
        }
        return null;
    }

}
