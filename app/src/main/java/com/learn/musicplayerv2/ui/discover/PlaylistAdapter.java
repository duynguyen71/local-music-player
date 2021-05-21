package com.learn.musicplayerv2.ui.discover;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.learn.musicplayerv2.R;
import com.learn.musicplayerv2.entity.PlaylistEntity;

import org.jetbrains.annotations.NotNull;

import java.util.List;

import lombok.Setter;

public class PlaylistAdapter extends RecyclerView.Adapter<PlaylistAdapter.MyHolder> {


    List<PlaylistEntity> playlists;
    Context context;

    @Setter
    OnPlaylistItemClickListener onPlaylistItemClickListener;
    @Setter
    OnPlaylistOptionsClickListener onPlaylistOptionsClickListener;

    public PlaylistAdapter(List<PlaylistEntity> playlists, Context context) {
        this.playlists = playlists;
        this.context = context;
    }

    @NonNull
    @NotNull
    @Override
    public PlaylistAdapter.MyHolder onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
        View inflate = LayoutInflater.from(context).inflate(R.layout.playlist_item, parent, false);
        MyHolder myHolder = new MyHolder(inflate);
        return myHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull @NotNull PlaylistAdapter.MyHolder holder, int playlistPosition) {
        PlaylistEntity item = playlists.get(playlistPosition);
        holder.tvPlaylistName.setText(item.getPlaylistName());
        holder.tvPlaylistName.setOnClickListener(v -> {
            if (onPlaylistItemClickListener != null) {
                onPlaylistItemClickListener.onPlaylistItemClick(v, playlistPosition);
            }
        });
        holder.tvPlaylistOption.setOnClickListener(v -> {
            if (onPlaylistOptionsClickListener != null) {
                onPlaylistOptionsClickListener.onPlaylistOptionsClick(v, playlistPosition);
            }
        });
    }

    @Override
    public int getItemCount() {
        return playlists.size();
    }

    public void setPlaylistItemListener(OnPlaylistItemClickListener clickListener) {
        this.onPlaylistItemClickListener = clickListener;
    }

    public class MyHolder extends RecyclerView.ViewHolder {
        TextView tvPlaylistName;
        TextView tvPlaylistOption;
        TextView tvSongCount;

        public MyHolder(@NonNull @NotNull View itemView) {
            super(itemView);
            tvPlaylistName = itemView.findViewById(R.id.tvPlaylistName);
            tvPlaylistOption = itemView.findViewById(R.id.tv_playlistOptions);
            tvSongCount = itemView.findViewById(R.id.tv_songCount);
        }

    }

    public interface OnPlaylistItemClickListener {
        void onPlaylistItemClick(View view, int position);
    }

    public interface OnPlaylistOptionsClickListener {
        void onPlaylistOptionsClick(View view, int position);
    }
}
