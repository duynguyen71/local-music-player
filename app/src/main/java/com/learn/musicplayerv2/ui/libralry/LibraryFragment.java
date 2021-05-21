package com.learn.musicplayerv2.ui.libralry;

import android.app.AlertDialog;
import android.os.Build;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.PopupMenu;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.learn.musicplayerv2.AppCache;
import com.learn.musicplayerv2.MainActivity;
import com.learn.musicplayerv2.R;
import com.learn.musicplayerv2.dao.PlaylistDao;
import com.learn.musicplayerv2.dao.SongDao;
import com.learn.musicplayerv2.db.AppDatabase;
import com.learn.musicplayerv2.entity.PlaylistEntity;
import com.learn.musicplayerv2.model.Song;
import com.learn.musicplayerv2.service.PlaySongService;
import com.learn.musicplayerv2.ui.dialog.PlaylistEditDialog;
import com.learn.musicplayerv2.utils.AppUtil;

import java.util.ArrayList;
import java.util.List;

public class LibraryFragment extends Fragment implements LibraryViewAdapter.OnSongClickListener, LibraryViewAdapter.OnSongOptionsClickListener {

    static final String TAG = "TEST";
    RecyclerView songListView;
    LibraryViewAdapter adapter;
    FloatingActionButton btnNewPlaylist;
    ProgressBar progressBar;
    AppDatabase db;
    EditText etFilter;

    PlaylistDao playlistDao;
    List<PlaylistEntity> playlists;
    String[] playlistName;
    boolean[] checkedItems;
    SongDao songDao;
    PlaylistEditDialog playlistEditDialog;
    final int actionChangeToPlayer = R.id.action_navigation_libraryFragment_to_playerFragment;
    List<Integer> checkedPlaylistIds;
    PlaySongService service;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        Thread thread1 = new Thread(() -> {
        db = AppDatabase.getInstance(getActivity().getApplicationContext());
        playlistDao = db.playlistDao();
        songDao = db.songDao();

//        });
//        thread1.start();
    }

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_library, container, false);
        initViews(root);
        //find playlist in db in another thread
        getAllPlaylists();
        //observer playlist change
        registerObserver();
        return root;
    }

    void getAllPlaylists() {
        AppCache.LIBRARY_SONGS = AppUtil.findAllLibrarySong(getContext());
        songListView.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false));
        adapter = new LibraryViewAdapter(getContext(), AppCache.LIBRARY_SONGS, actionChangeToPlayer);
        songListView.setAdapter(adapter);
        adapter.setOnSongClickListener(this);
        adapter.setOnSongOptionsClickListener(this);
    }

    void registerObserver() {
        if (playlistDao != null) {
            playlistDao.findAllPlaylist().observe(getViewLifecycleOwner(), playlistEntities -> {
                if (playlistEntities != null) {
                    playlists = playlistEntities;
                    playlistName = new String[playlistEntities.size()];
                    checkedPlaylistIds = new ArrayList<>();
                    for (int i = 0; i < playlistEntities.size(); i++) {
                        playlistName[i] = playlistEntities.get(i).playlistName.trim();
                    }
                    checkedItems = new boolean[playlistEntities.size()];
                }
            });
        }
    }


    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public void callPlayingService(int position) {
        AppCache.PLAYING_PLAYLIST = AppCache.LIBRARY_SONGS;
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
                        .navigate(actionChangeToPlayer, null);
            }
        } else {
            Bundle bundle = new Bundle();
            bundle.putInt("songPosition", position);
            Navigation.findNavController((MainActivity) getContext(), R.id.nav_host_fragment)
                    .navigate(actionChangeToPlayer, bundle);
        }

    }

    //TODO : change
    @Override
    public void showPopup(View view, int position) {
        PopupMenu popupMenu = new PopupMenu(getContext(), view);
        popupMenu.inflate(R.menu.menu_song_option);
        popupMenu.show();
        popupMenu.setOnMenuItemClickListener(item -> {
            switch (item.getItemId()) {
                case R.id.itemAddToPlaylist: {
                    AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(getContext());
                    dialogBuilder.setIcon(R.drawable.ic_baseline_add_24);
                    dialogBuilder.setTitle("Select playlist to add");

                    dialogBuilder.setNegativeButton("Cancel", (dialog, which) -> {
                        dialog.cancel();
                        checkedPlaylistIds.clear();
                        checkedItems = new boolean[playlists.size()];

                    });
                    dialogBuilder.setMultiChoiceItems(playlistName, checkedItems, (dialog, which, isChecked) -> {
                        if (isChecked) {
                            checkedPlaylistIds.add(playlists.get(which).getPlaylistId());
                        }
                    });

                    //save to db when user click ok
                    dialogBuilder.setPositiveButton("Ok", (dialog13, which) -> {
                        Thread thread = new Thread(
                                () -> {
                                    Song songClicked = AppUtil.findSongByPosition(AppCache.LIBRARY_SONGS, position);
                                    for (int playlistId :
                                            checkedPlaylistIds) {
                                        List<Song> songs = songDao.findByPlaylistId(playlistId);
                                        songClicked.setPlaylistId(playlistId);
                                        if (!songs.contains(songClicked)) {
                                            songDao.insert(songClicked);
                                            Log.e(TAG, "showPopup contains: ");
                                        }
                                    }
                                    checkedPlaylistIds.clear();
                                }
                        );
                        thread.start();
                        checkedItems = new boolean[playlists.size()];
                    });
                    dialogBuilder.show();
                    return true;
                }
            }
            return false;
        });
    }

    void initViews(View root) {
        progressBar = root.findViewById(R.id.progressBar);
        btnNewPlaylist = root.findViewById(R.id.btnNewPlaylist);
        songListView = root.findViewById(R.id.rc_songs_view);
        etFilter = root.findViewById(R.id.etFilter);
        etFilter.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

                String str = s.toString();
                if (adapter != null) {
                    adapter.getFilter().filter(str);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });


        progressBar.setVisibility(View.GONE);
        btnNewPlaylist.setOnClickListener(v -> {
            FragmentManager supportFragmentManager = getActivity().getSupportFragmentManager();
            playlistEditDialog = new PlaylistEditDialog();
            playlistEditDialog.show(supportFragmentManager, "fragment_new_playlist");
        });

    }

}