package com.learn.musicplayerv2.ui.discover;

import android.app.AlertDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.PopupMenu;
import android.widget.ProgressBar;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.LiveData;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.learn.musicplayerv2.MainActivity;
import com.learn.musicplayerv2.R;
import com.learn.musicplayerv2.dao.PlaylistDao;
import com.learn.musicplayerv2.db.AppDatabase;
import com.learn.musicplayerv2.entity.PlaylistEntity;
import com.learn.musicplayerv2.ui.dialog.PlaylistEditDialog;
import com.learn.musicplayerv2.ui.playlist.PlaylistFragment;

import java.util.List;

public class DiscoverFragment extends Fragment implements View.OnClickListener, PlaylistAdapter.OnPlaylistItemClickListener, PlaylistAdapter.OnPlaylistOptionsClickListener {

    private RecyclerView playlistsView;
    private List<PlaylistEntity> playlists;
    private PlaylistAdapter playlistAdapter;
    private PlaylistDao playlistDao;
    private FloatingActionButton fab;
    private PlaylistEditDialog dialog;
    private ProgressBar progressBar;


    @Override
    public void onCreate(@Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_discover, container, false);
        initViews(root);
        //add new playlist
        new GetAppDatabase().execute();
        return root;
    }

    private void initViews(View root) {
        playlistsView = root.findViewById(R.id.rcv_playlists);
        progressBar = root.findViewById(R.id.progressBar2);
        fab = root.findViewById(R.id.fab_addBtn);
        fab.setOnClickListener(this);
        progressBar.setVisibility(View.GONE);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.fab_addBtn: {
                FragmentManager supportFragmentManager = getActivity().getSupportFragmentManager();
                dialog = PlaylistEditDialog.newInstance("Add new playlist", PlaylistEditDialog.ACTION_NEW,null);
                dialog.show(supportFragmentManager, "fragment_new_playlist");
            }
        }
    }

    @Override
    public void onPlaylistItemClick(View view, int position) {
        PlaylistEntity playlistEntity = playlists.get(position);
        Bundle bundle = new Bundle();
        bundle.putInt(PlaylistFragment.PLAYLIST_ID, playlistEntity.getPlaylistId());
        Navigation.findNavController((MainActivity) getContext(), R.id.nav_host_fragment)
                .navigate(R.id.action_navigation_discover_to_playlistFragment, bundle);
    }

    @Override
    public void onPlaylistOptionsClick(View view, int position) {

        PlaylistEntity playlistEntity = playlists.get(position);
        PopupMenu popupMenu = new PopupMenu(getContext(), view.findViewById(R.id.tv_playlistOptions));
        popupMenu.inflate(R.menu.menu_playlist_option);
        popupMenu.show();

        popupMenu.setOnMenuItemClickListener(item -> {
            switch (item.getItemId()) {
                case R.id.item_removePlaylist: {
                    AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(getContext());
                    dialogBuilder.setTitle("Are you sure to delete " + playlistEntity.getPlaylistName());
                    dialogBuilder.setNegativeButton("Cancel", (dialog, which) -> {
                        dialog.cancel();
                    });
                    dialogBuilder.setPositiveButton("Delete", (dialog, which) -> {
                        Thread thread = new Thread(() -> {
                            playlistDao.deletePlaylist(playlistEntity);
                        });
                        thread.start();
                        dialog.dismiss();
                    });
                    dialogBuilder.show();
                    break;
                }
                case R.id.item_changeNamePlaylist: {
                    dialog = PlaylistEditDialog.newInstance("Update playlist name", PlaylistEditDialog.ACTION_UPDATE,playlists.get(position));
                    dialog.show(getActivity().getSupportFragmentManager(), "dialog_update_playlist_name");
                }
            }
            return true;
        });

        return;
    }


    public class GetAppDatabase extends AsyncTask<Void, Void, LiveData<List<PlaylistEntity>>> {
        AppDatabase db;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressBar.setVisibility(View.VISIBLE);
        }

        @Override
        protected LiveData<List<PlaylistEntity>> doInBackground(Void... voids) {
            db = AppDatabase.getInstance(getActivity().getApplicationContext());
            playlistDao = db.playlistDao();
            return null;
        }

        @Override
        protected void onPostExecute(LiveData<List<PlaylistEntity>> listLiveData) {
            super.onPostExecute(listLiveData);
            playlistDao.findAllPlaylist().observe(getViewLifecycleOwner(), playlistEntities -> {
                playlists = playlistEntities;
                playlistAdapter = new PlaylistAdapter(playlistEntities, getContext());
                playlistsView.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false));
                playlistsView.setAdapter(playlistAdapter);
                playlistAdapter.setPlaylistItemListener(DiscoverFragment.this::onPlaylistItemClick);
                playlistAdapter.setOnPlaylistOptionsClickListener(DiscoverFragment.this::onPlaylistOptionsClick);
            });
            progressBar.setVisibility(View.GONE);
        }
    }

}