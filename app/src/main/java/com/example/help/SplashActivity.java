package com.example.help;

import android.content.Intent;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.widget.VideoView;

import androidx.appcompat.app.AppCompatActivity;

public class SplashActivity extends AppCompatActivity {



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        VideoView videoView = findViewById(R.id.videoView1);

        String videoPath = "android.resource://" + getPackageName() + "/" + R.raw.axhub;

        Uri uri = Uri.parse(videoPath);

        videoView.setVideoURI(uri);

        videoView.start();

        videoView.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {

                startActivity(new Intent(SplashActivity.this, MainActivity.class));
                finish();
            }
        });
    }
}

//make raw file and add video in it