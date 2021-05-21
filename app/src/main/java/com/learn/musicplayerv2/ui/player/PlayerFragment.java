package com.learn.musicplayerv2.ui.player;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.ColorFilter;
import android.media.MediaPlayer;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import com.bumptech.glide.Glide;
import com.learn.musicplayerv2.AppViewModel;
import com.learn.musicplayerv2.R;
import com.learn.musicplayerv2.service.PlaySongService;
import com.learn.musicplayerv2.ui.dialog.SongDetailsDialog;
import com.learn.musicplayerv2.utils.AppUtil;

import org.jetbrains.annotations.NotNull;

import lombok.NoArgsConstructor;

@NoArgsConstructor
public class PlayerFragment extends Fragment implements View.OnClickListener {

    public static final String SONG_POSITION = "songPosition";
    private Integer songPosition;
    private ImageButton btnNext, btnPlay, btnPrev;
    private Handler handler;
    private AppViewModel appViewModel;
    private TextView name, artist, tvStartTme, tvDuration;
    private ImageView image;
    private SeekBar seekBar;
    PlaySongService playSongService;
    private TextView tvViewSongDetails;

    @Override
    public void onAttach(@NonNull @NotNull Context context) {
        super.onAttach(context);
//        OnBackPressedCallback callback = new OnBackPressedCallback(true) {
//            @Override
//            public void handleOnBackPressed() {
//                Toast.makeText(context,"ON Back pressed",Toast.LENGTH_SHORT).show();
////                showAreYouSureDialog();
//                Navigation.findNavController(getActivity(),R.id.nav_host_fragment)
//                        .navigateUp();
//            }
//        };
//
//        requireActivity().getOnBackPressedDispatcher().addCallback(this,callback);
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            songPosition = getArguments().getInt(SONG_POSITION);
        }
        appViewModel = AppViewModel.getInstance();
        playSongService = PlaySongService.getInstance();
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_player, container, false);
        initViews(root);
        if (songPosition != null) {
            if (playSongService.getPlayer() == null || playSongService.getPlayingPosition() != songPosition) {
                playSongService.play(songPosition);
            }
        }
        registerLiveDataListener();

        return root;
    }

    void handleBtnPauseEvent() {
        boolean pause = playSongService.pause();
        if (pause) {
            btnPlay.setImageResource(R.drawable.ic_play_arrow_24);
        } else {
            btnPlay.setImageResource(R.drawable.ic_pause_24);
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    void handleBtnNextEvent() {
        playSongService.next();
//        appViewModel.setPlayingSong(playSongService.getPlayingSong());
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    void handleBtnPrevEvent() {
        playSongService.prev();
    }

    Runnable upDateSeekBar = new Runnable() {
        @Override
        public void run() {
            if (appViewModel.getMPLiveDate().getValue() != null) {
                MediaPlayer player = appViewModel.getMPLiveDate().getValue();
                if (player.isPlaying()) {
                    btnPlay.setImageResource(R.drawable.ic_pause_24);
                } else {
                    btnPlay.setImageResource(R.drawable.ic_play_arrow_24);
                }
                seekBar.setProgress(player.getCurrentPosition());
                seekBar.setMax(player.getDuration());

                tvStartTme.setText(formatTime(player.getCurrentPosition()));
                tvDuration.setText(formatTime(player.getDuration()));

                seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                    @Override
                    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                        if (fromUser) {
                            appViewModel.getMediaPlayer().seekTo(progress);
                        }
                    }

                    @Override
                    public void onStartTrackingTouch(SeekBar seekBar) {

                    }

                    @Override
                    public void onStopTrackingTouch(SeekBar seekBar) {

                    }
                });
                handler.postDelayed(upDateSeekBar, 1000);
            }
        }
    };

    void registerLiveDataListener() {
        appViewModel.getCurrentSong().observe(getViewLifecycleOwner(), song -> {
            if (song != null) {
                btnPlay.setImageResource(R.drawable.ic_pause_24);
                name.setText(song.getName());
                artist.setText(song.getArtist());
                Animation animation = AnimationUtils.loadAnimation(getContext(), R.anim.tranform_360_infinite);
                animation.setDuration(appViewModel.getMediaPlayer().getDuration());

                Bitmap img = AppUtil.toBitmap(getContext(), song.getAlbumId());
                if (img != null) {
                    Glide.with(getContext()).load(img).into(image);
                }
                else {
                    Glide.with(getContext()).load(R.drawable.ic_hihih_solid).into(image);
                }

                image.startAnimation(animation);


            }
        });
        //TODO: is playing boolean live data
        appViewModel.getMPLiveDate().observe(getViewLifecycleOwner(), mediaPlayer -> {
            if (mediaPlayer != null) {
                btnPlay.setImageResource(R.drawable.ic_pause_24);

                if (!mediaPlayer.isPlaying()) {
                    btnPlay.setImageResource(R.drawable.ic_play_arrow_24);
                }
                handler = new Handler();
                handler.postDelayed(upDateSeekBar, 0);
            }
        });
    }

    String formatTime(int i) {
        int minutes = (int) (i % (1000 * 60 * 60)) / (1000 * 60);
        int seconds = (int) ((i % (1000 * 60 * 60)) % (1000 * 60) / 1000);
        return String.format("%d:%02d", minutes, seconds);
    }


    @RequiresApi(api = Build.VERSION_CODES.O)
    void initViews(@NotNull View root) {
        name = root.findViewById(R.id.pName);
        artist = root.findViewById(R.id.pArtist);
        image = root.findViewById(R.id.pImage);
        seekBar = root.findViewById(R.id.seekBar);
        btnNext = root.findViewById(R.id.btnNext);
        btnPlay = root.findViewById(R.id.btnPlay);
        btnPrev = root.findViewById(R.id.btnPrev);
        tvStartTme = root.findViewById(R.id.tvStartTime);
        tvDuration = root.findViewById(R.id.tvDurationTime);
        tvViewSongDetails = root.findViewById(R.id.tv_songDetails);

        tvViewSongDetails.setOnClickListener(this);
        btnPlay.setOnClickListener(this);
        btnNext.setOnClickListener(this);
        btnPrev.setOnClickListener(this);
//        btnPlay.setOnClickListener(v -> {
//            handleBtnPauseEvent();
//
//        });
//        btnNext.setOnClickListener(v -> {
//            handleBtnNextEvent();
//        });
//        btnPrev.setOnClickListener(v -> {
//            handleBtnPrevEvent();
//        });
    }


    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tv_songDetails: {
                Toast.makeText(getContext(), "Click", Toast.LENGTH_SHORT).show();
                FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                SongDetailsDialog songDetailsDialog = SongDetailsDialog.newInstance("TiTLE");
                songDetailsDialog.show(fragmentManager, "dialog_song_details");
                break;
            }
            case R.id.btnNext: {
                handleBtnNextEvent();
                break;
            }
            case R.id.btnPrev: {
                handleBtnPrevEvent();
                break;
            }
            case R.id.btnPlay: {
                handleBtnPauseEvent();
                break;
            }

        }
    }
}