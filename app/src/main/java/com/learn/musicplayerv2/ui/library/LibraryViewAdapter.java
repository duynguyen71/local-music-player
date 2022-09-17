package com.learn.musicplayerv2.ui.library;

import android.content.Context;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.learn.musicplayerv2.AppCache;
import com.learn.musicplayerv2.R;
import com.learn.musicplayerv2.model.Song;
import com.learn.musicplayerv2.utils.AppUtil;

import java.util.ArrayList;
import java.util.List;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class LibraryViewAdapter extends RecyclerView.Adapter<LibraryViewAdapter.SongViewHolder> implements Filterable {

    private Context context;
    private List<Song> songList;
    private int action;

    private List<Song> filteredSongList = new ArrayList<>();

    private OnSongOptionsClickListener onSongOptionsClickListener;

    private OnSongClickListener onSongClickListener;

    public LibraryViewAdapter(Context context, List<Song> songList, int action) {
        this.context = context;
        this.songList = songList;
        this.action = action;
    }

    @NonNull
    @Override
    public SongViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View root = LayoutInflater.from(context).inflate(R.layout.song_info, parent, false);
        return new SongViewHolder(root);
    }

    @Override
    public void onBindViewHolder(@NonNull SongViewHolder holder, int position) {
        Song item = songList.get(position);
        holder.tvSongName.setText(item.getName());
        holder.tvSongArtist.setText(item.getArtist());
        Bitmap bitmap = AppUtil.toBitmap(context, item.getAlbumId());
        if (bitmap != null) {
            Glide.with(context).load(bitmap).into(holder.ivSongImage);
        }
        holder.infoContent.setOnClickListener(v -> {
            if (onSongClickListener != null) {
                onSongClickListener.callPlayingService(position);
                return;
            }
        });

        holder.tvOptions.setOnClickListener(v -> {
            if (onSongOptionsClickListener != null) {
                onSongOptionsClickListener.showPopup(v, position);
            }
        });

    }


    @Override
    public int getItemCount() {
        return this.songList.size();
    }


    @Override
    public Filter getFilter() {
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence constraint) {
                String string = constraint.toString();
                if (string.isEmpty()) {
                    filteredSongList = AppCache.LIBRARY_SONGS;
                } else {
                    for (Song song :
                            songList) {
//                        int i = Collections.binarySearch(songList, song, (o1, o2) -> {
//                            if (o1.getName().toLowerCase() == o2.getName().toLowerCase()) {
//                                return 0;
//                            }
//                            return -1;
//                        });
//                        if(i == 0){
//                            filteredSongList.add(song);
//                        }

                        if (song.getName().toLowerCase().contains(string.toLowerCase())) {
                            filteredSongList.add(song);
                        }
                    }
                }
                FilterResults filterResults = new FilterResults();
                filterResults.values = filteredSongList;
                return filterResults;
            }

            @Override
            protected void publishResults(CharSequence constraint, FilterResults results) {

                List<Song> rs = (List<Song>) results.values;
                if (rs != null && rs.size() > 0) {
                    songList = rs;
                    notifyDataSetChanged();
                }

            }
        };
    }


    public class SongViewHolder extends RecyclerView.ViewHolder {
        TextView tvSongName, tvSongArtist, tvOptions;
        ImageView ivSongImage;
        View infoContent;

        public SongViewHolder(@NonNull View itemView) {
            super(itemView);
            tvSongArtist = itemView.findViewById(R.id.songArtist);
            tvSongName = itemView.findViewById(R.id.songName);
            ivSongImage = itemView.findViewById(R.id.songImg);
            infoContent = itemView.findViewById(R.id.songInfoContent);
            tvOptions = itemView.findViewById(R.id.tvOptions);

        }

    }

    public interface OnSongClickListener {
        void callPlayingService(int position);
    }

    public interface OnSongOptionsClickListener {
        void showPopup(View view, int position);
    }
}
