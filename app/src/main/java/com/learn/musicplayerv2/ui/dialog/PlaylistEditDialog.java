package com.learn.musicplayerv2.ui.dialog;

import android.app.Dialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.learn.musicplayerv2.R;
import com.learn.musicplayerv2.dao.PlaylistDao;
import com.learn.musicplayerv2.db.AppDatabase;
import com.learn.musicplayerv2.entity.PlaylistEntity;

public class PlaylistEditDialog extends DialogFragment {
    Button btnSave;
    EditText etPlaylistName;
    TextView tvTitle;

    private static String title;
    private static int action;
    private static PlaylistEntity playlistEntity;

    public static final int ACTION_NEW = 1;
    public static final int ACTION_UPDATE = 2;


    public static PlaylistEditDialog newInstance(String t, int a, PlaylistEntity playlist) {
        title = t;
        action = a;
        playlistEntity = playlist;
        PlaylistEditDialog fragment = new PlaylistEditDialog();

        return fragment;
    }


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.dialog_new_playlist, container);

        etPlaylistName = root.findViewById(R.id.etNewPlaylistName);
        if (action == ACTION_UPDATE) {
            etPlaylistName.append(playlistEntity.getPlaylistName());
        }
        tvTitle = root.findViewById(R.id.tvActionTtile);
        tvTitle.setText(title);
        return root;


    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        Dialog dialog = super.onCreateDialog(savedInstanceState);
        dialog.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        return dialog;
    }

    @Override
    public void onActivityCreated(@Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        getDialog().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        btnSave = view.findViewById(R.id.btnSavePlaylist);
        etPlaylistName = view.findViewById(R.id.etNewPlaylistName);
        btnSave.setOnClickListener(v -> {
            if (action == ACTION_NEW) {
                String playlistName = etPlaylistName.getText().toString().trim();
                if (playlistName.length() > 0) {
                    PlaylistEntity playlistEntity = new PlaylistEntity(playlistName);
                    savePlaylist(playlistEntity);
                    dismiss();
                    Toast.makeText(getContext(), playlistName + " has been added!", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(getContext(), "Please fill playlist name", Toast.LENGTH_SHORT).show();
                }
                return;
            } else if (action == ACTION_UPDATE) {
                String updateName = etPlaylistName.getText().toString().trim();
                if (updateName.length() > 0) {
                    playlistEntity.setPlaylistName(updateName);
                    savePlaylist(playlistEntity);
                    dismiss();
                    Toast.makeText(getContext(), updateName + "changed", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(getContext(), "Please fill playlist name", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    void savePlaylist(PlaylistEntity playlistEntity) {
        Runnable runnable = () -> {
            AppDatabase db =
                    AppDatabase.getInstance(getActivity().getApplicationContext());
            PlaylistDao playlistDao = db.playlistDao();
            playlistDao.insert(playlistEntity);
        };
        Thread thread = new Thread(runnable);
        thread.start();
    }

    //TODO: context menu
    //TODO: filter song list

}
