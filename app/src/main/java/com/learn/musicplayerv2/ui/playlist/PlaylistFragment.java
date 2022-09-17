package com.learn.musicplayerv2.ui.playlist;

import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.learn.musicplayerv2.AppCache;
import com.learn.musicplayerv2.AppViewModel;
import com.learn.musicplayerv2.MainActivity;
import com.learn.musicplayerv2.R;
import com.learn.musicplayerv2.dao.PlaylistDao;
import com.learn.musicplayerv2.dao.SongDao;
import com.learn.musicplayerv2.db.AppDatabase;
import com.learn.musicplayerv2.entity.PlaylistWithSongs;
import com.learn.musicplayerv2.model.Song;
import com.learn.musicplayerv2.service.PlaySongService;
import com.learn.musicplayerv2.ui.library.LibraryViewAdapter;
import com.learn.musicplayerv2.utils.AppUtil;

import org.jetbrains.annotations.NotNull;

import java.util.List;

import lombok.NoArgsConstructor;

import static android.widget.Toast.LENGTH_SHORT;

@NoArgsConstructor
public class PlaylistFragment extends Fragment implements LibraryViewAdapter.OnSongClickListener, LibraryViewAdapter.OnSongOptionsClickListener {

    public static final String PLAYLIST_ID = "playlistId";
    private ProgressBar progressBar;
    private RecyclerView playlistView;
    private LibraryViewAdapter adapter;
    private TextView tvPlaylistName, tvPlaylistDate;
    private ImageView ivPlaylistThumb;
    private AppViewModel appViewModel;
    private PlaylistDao playlistDao;
    private int playlistId;
    private PlaySongService service;
    private SongDao songDao;
    private AppDatabase db;
    private List<Song> songList;
    private PlaylistWithSongs playlistWithSongs;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            playlistId = getArguments().getInt(PLAYLIST_ID);
        }
        appViewModel = AppViewModel.getInstance();
    }

    @Override
    public View onCreateView(@NotNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View root = inflater.inflate(R.layout.fragment_playlist, container, false);
        // init views
        playlistView = root.findViewById(R.id.rvPlaylist);
        tvPlaylistName = root.findViewById(R.id.tv_playlist_name);
        tvPlaylistDate = root.findViewById(R.id.tv_playlist_date);
        ivPlaylistThumb = root.findViewById(R.id.iv_playlist_thumb);
        progressBar = root.findViewById(R.id.progressBar3);
        progressBar.setVisibility(View.GONE);

        //
        observerLiveDataChangeListener();
        //
        new AccessDatabaseAndGetPlaylist().execute();
        return root;
    }

    void observerLiveDataChangeListener() {
        appViewModel.getCurrentSong().observe(getViewLifecycleOwner(), song -> {
            if (song != null) {
                Bitmap img = AppUtil.toBitmap(getContext(), song.getAlbumId());
                Glide.with(getContext()).load(img).into(ivPlaylistThumb);

                tvPlaylistName.setText(song.getName());
                tvPlaylistDate.setText(song.getArtist());
                return;
            }
        });
    }


    @Override
    public void callPlayingService(int position) {
        AppCache.PLAYING_PLAYLIST = songList;
        service = PlaySongService.getInstance();
        if (service == null) {
            service = new PlaySongService(getContext().getApplicationContext());
            PlaySongService.setInstance(service);
        }
        service.setPlayList(AppCache.LIBRARY_SONGS);
        if (service.getPlayer() != null) {
            if (position != service.getPlayingPosition()) {
                service.play(position);
            } else {
                Navigation.findNavController((MainActivity) getContext(), R.id.nav_host_fragment)
                        .navigate(R.id.playlist_to_player, null);
            }
        } else {
            Bundle bundle = new Bundle();
            bundle.putInt("songPosition", position);
            Navigation.findNavController((MainActivity) getContext(), R.id.nav_host_fragment)
                    .navigate(R.id.playlist_to_player, bundle);
        }
    }

    @Override
    public void showPopup(View view, int position) {
        PopupMenu popupMenu = new PopupMenu(getContext(), view);
        popupMenu.inflate(R.menu.menu_song_in_playlist);
        popupMenu.show();
        popupMenu.setOnMenuItemClickListener(item -> {
            switch (item.getItemId()) {
                case R.id.item_removeFromPlaylist: {
                    Handler handler = new Handler();
                    Thread song = new Thread(() -> {
                        Song songEntity = songList.get(position);
                        songDao.remove(songEntity);
                        handler.post(() -> {
                            songList.remove(position);
                            adapter.notifyItemRemoved(position);
                            adapter.notifyItemRangeRemoved(position, songList.size());
                            Toast.makeText(getContext(), songEntity.getName() + " has been removed!", LENGTH_SHORT).show();
                        });
                    });
                    song.start();
                    return true;
                }

            }
            return false;
        });
    }

    private class AccessDatabaseAndGetPlaylist extends AsyncTask<Void, PlaylistWithSongs, PlaylistWithSongs> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressBar.setVisibility(View.VISIBLE);
        }

        @Override
        protected PlaylistWithSongs doInBackground(Void... voids) {
            db = AppDatabase.getInstance(getContext().getApplicationContext());
            playlistDao = db.playlistDao();
            songDao = db.songDao();
            playlistWithSongs = playlistDao.findPlaylistAndSongsById(playlistId);
            return playlistWithSongs;
        }

        @Override
        protected void onPostExecute(PlaylistWithSongs playlistWithSongs) {
            super.onPostExecute(playlistWithSongs);
            progressBar.setVisibility(View.GONE);
            songList = playlistWithSongs.songs;
            tvPlaylistName.setText(playlistWithSongs.playlist.getPlaylistName());

            Handler handler = new Handler();
            Thread thread = new Thread(() -> {
                handler.post(() -> {
                    adapter = new LibraryViewAdapter(getContext(), songList, R.id.playlist_to_player);
                    adapter.setOnSongClickListener(PlaylistFragment.this::callPlayingService);
                    adapter.setOnSongOptionsClickListener(PlaylistFragment.this::showPopup);
                    playlistView.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false));
                    playlistView.setAdapter(adapter);
                });
            });
            thread.start();
        }
    }
}