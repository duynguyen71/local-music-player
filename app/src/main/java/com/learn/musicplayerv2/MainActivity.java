package com.learn.musicplayerv2;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.learn.musicplayerv2.service.MyNotification;
import com.learn.musicplayerv2.service.PlaySongService;

import org.jetbrains.annotations.NotNull;

import static android.Manifest.permission.READ_EXTERNAL_STORAGE;
import static android.Manifest.permission.WRITE_EXTERNAL_STORAGE;
import static android.content.pm.PackageManager.PERMISSION_GRANTED;
import static android.os.Build.VERSION.SDK_INT;
import static android.os.Build.VERSION_CODES.M;
import static android.widget.Toast.LENGTH_SHORT;

public class MainActivity extends AppCompatActivity {

    TextView songName, songArtist;
    View playSongController;
    ImageButton playBtn, nextBtn, prevBtn;
    SeekBar seekBar;
    //    ProgressBar progressBar;
    AppViewModel appViewModel;
    NavController navController;
    Handler handler = new Handler();

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        registerReceiver(broadcastReceiver, new IntentFilter("TRACKS_TRACKS"));
        setContentView(R.layout.activity_main);
        getSupportActionBar().hide();
        checkVersion();
        initViews();
        appViewModel = AppViewModel.getInstance();
        if (appViewModel == null) {
            appViewModel = new ViewModelProvider(this).get(AppViewModel.class);
            AppViewModel.setInstance(appViewModel);
        }


        BottomNavigationView navView = getBottomNavigationView();

        playSongController.setVisibility(View.GONE);
        navController.addOnDestinationChangedListener((controller, destination, arguments) ->

        {
            if (destination.getId() == R.id.playerFragment2) {
                playSongController.setVisibility(View.GONE);
                navView.setVisibility(View.GONE);
            } else {
                navView.setVisibility(View.VISIBLE);
                if (PlaySongService.getInstance() != null && PlaySongService.getInstance().getPlayer() != null) {
                    playSongController.setVisibility(View.VISIBLE);
                }
            }
        });
        registerClickLisnterner();
        registerLiveDataListener();


    }

    @NotNull
    BottomNavigationView getBottomNavigationView() {
        BottomNavigationView navView = findViewById(R.id.nav_view);
        AppBarConfiguration appBarConfiguration = new AppBarConfiguration.Builder(
                R.id.navigation_home, R.id.navigation_discover, R.id.navigation_setting)
                .build();
        navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration);
        NavigationUI.setupWithNavController(navView, navController);
        return navView;
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    void initViews() {
        songName = findViewById(R.id.cName);
        songArtist = findViewById(R.id.cArtist);
        playSongController = findViewById(R.id.playController);

        playBtn = findViewById(R.id.cPause);
        nextBtn = findViewById(R.id.cNext);
        prevBtn = findViewById(R.id.cPrev);
        seekBar = findViewById(R.id.seekBar2);
//            progressBar = findViewById(R.id.progressBar);
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void registerClickLisnterner() {

        nextBtn.setOnClickListener(v -> PlaySongService.getInstance().next());
        prevBtn.setOnClickListener(v -> PlaySongService.getInstance().prev());
        playBtn.setOnClickListener(v -> {
            boolean pause = PlaySongService.getInstance().pause();
            if (pause) {
                playBtn.setImageResource(R.drawable.ic_play_arrow_24);
            } else {
                playBtn.setImageResource(R.drawable.ic_pause_24);
            }
        });

        songName.setOnClickListener(v -> {
            navController.navigate(R.id.playerFragment2, null);
        });

        songArtist.setOnClickListener(v -> {
            navController.navigate(R.id.playerFragment2, null);
        });
    }

    public Runnable runnable = new Runnable() {
        @Override
        public void run() {
            if (appViewModel.getMPLiveDate().getValue() != null) {
                if (appViewModel.getMediaPlayer().isPlaying()) {
                    playBtn.setImageResource(R.drawable.ic_pause_24);

                } else {
                    playBtn.setImageResource(R.drawable.ic_play_arrow_24);

                }
                seekBar.setMax(appViewModel.getMediaPlayer().getDuration());
                seekBar.setProgress(appViewModel.getMediaPlayer().getCurrentPosition());
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
                handler.postDelayed(runnable, 500);
            }
        }
    };

    void registerLiveDataListener() {
        appViewModel.getCurrentSong().observe(this, song -> {
            if (song != null) {
                songName.setText(song.getName());
                songArtist.setText(song.getArtist());
            }
        });
        appViewModel.getMPLiveDate().observe(this, mediaPlayer -> {
            if (mediaPlayer != null) {
                Log.e("TAG", "registerLiveDataListener: " + "media changed");
                handler.postDelayed(runnable, 0);
            }
        });
    }




    void checkVersion() {
        if (SDK_INT >= M) {
            if (checkSelfPermission(READ_EXTERNAL_STORAGE) != PERMISSION_GRANTED) {
                requestPermissions(new String[]{READ_EXTERNAL_STORAGE}, 1);
            }
            if (checkSelfPermission(WRITE_EXTERNAL_STORAGE) != PERMISSION_GRANTED) {
                requestPermissions(new String[]{WRITE_EXTERNAL_STORAGE}, 1);
            }
        }
    }

    BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @RequiresApi(api = Build.VERSION_CODES.O)
        @Override
        public void onReceive(Context context, Intent intent) {
            String actionName = String.valueOf(intent.getExtras().get("actionname"));
            PlaySongService service = PlaySongService.getInstance();
            switch (actionName) {
                case MyNotification.ACTION_PREV: {
                    service.prev();
                    break;
                }
                case MyNotification.ACTION_NEXT: {
                    service.next();
                    break;
                }
                case MyNotification.ACTION_PLAY: {
                    service.pause();
                    break;
                }

            }
        }
    };

    @Override
    protected void onResume() {
        Toast.makeText(this, "ON RESUME", LENGTH_SHORT).show();
        super.onResume();
    }

    boolean doubleBackToExitPressedOnce = false;

    @Override
    public void onBackPressed() {
//        super.onBackPressed();
        switch (Navigation.findNavController(this, R.id.nav_host_fragment).getCurrentDestination().getId()) {
//            case R.id.playerFragment2: {
//                Toast.makeText(this, "PLAYER FRAGMENT", LENGTH_SHORT).show();
//                break;
//            }
            case R.id.navigation_home: {
                if (doubleBackToExitPressedOnce) {
                    super.onBackPressed();
                    return;
                }
                this.doubleBackToExitPressedOnce = true;
                Toast.makeText(this, "Press back again to exit", LENGTH_SHORT).show();
                new Handler().postDelayed(() -> {
                    doubleBackToExitPressedOnce = false;
                }, 1800);

                break;
            }
            default: {
                super.onBackPressed();
            }
        }
    }
}

