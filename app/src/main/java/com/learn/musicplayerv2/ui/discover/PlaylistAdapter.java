package com.learn.musicplayerv2.ui.discover;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.learn.musicplayerv2.R;
import com.learn.musicplayerv2.db.AppDatabase;
import com.learn.musicplayerv2.entity.PlaylistEntity;

import org.jetbrains.annotations.NotNull;

import java.util.List;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PlaylistAdapter extends RecyclerView.Adapter<PlaylistAdapter.MyHolder> {


    private List<PlaylistEntity> playlists;
    private Context context;

    private OnPlaylistItemClickListener onPlaylistItemClickListener;
    private OnPlaylistOptionsClickListener onPlaylistOptionsClickListener;

    public PlaylistAdapter(List<PlaylistEntity> playlists, Context context) {
        this.playlists = playlists;
        this.context = context;
    }

    @Override
    public PlaylistAdapter.MyHolder onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
        View inflate = LayoutInflater.from(context).inflate(R.layout.playlist_item, parent, false);
        MyHolder myHolder = new MyHolder(inflate);
        return myHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull @NotNull PlaylistAdapter.MyHolder holder, int playlistPosition) {
        PlaylistEntity playlist = playlists.get(playlistPosition);
        holder.tvPlaylistName.setText(playlist.getPlaylistName());
//       int count =  db.playlistDao().countSongs(playlist.getPlaylistId());
//        holder.tvSongCount.setText(count!=0?count+" songs":"0 song");
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
        private TextView tvPlaylistName;
        private TextView tvPlaylistOption;
        private TextView tvSongCount;

        public MyHolder(@NonNull @NotNull View itemView) {
            super(itemView);
            tvPlaylistName = itemView.findViewById(R.id.tvPlaylistName);
            tvPlaylistOption = itemView.findViewById(R.id.tv_playlistOptions);
            tvSongCount = itemView.findViewById(R.id.tv_playlistSongCount);
        }

    }

    public interface OnPlaylistItemClickListener {
        void onPlaylistItemClick(View view, int position);
    }

    public interface OnPlaylistOptionsClickListener {
        void onPlaylistOptionsClick(View view, int position);
    }
}
