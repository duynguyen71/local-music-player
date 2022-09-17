package com.learn.musicplayerv2;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;


public class SplashActivity extends AppCompatActivity {

    static final int CODE = BuildConfig.VERSION_CODE;
    static final String NAME = BuildConfig.VERSION_NAME;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        Handler handler = new Handler();
        Thread thread = new Thread(() -> {
            TextView vName = findViewById(R.id.tvVersonName);
            TextView vCode = findViewById(R.id.tvVersionCode);
            handler.post(() -> {
                vName.setText(NAME);
                vCode.setText("version" + CODE);
            });
        });
        thread.start();
        new Handler().postDelayed(() -> {
            startActivity(new Intent(SplashActivity.this, MainActivity.class));
            thread.interrupt();
            finish();
        }, 1000);
    }
}
