package com.meronmks.zimitta.Activity;

import android.content.Intent;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.MediaController;
import android.widget.ProgressBar;
import android.widget.VideoView;

import com.meronmks.zimitta.R;

/**
 * Created by meron on 2016/09/19.
 */
public class PlayVideoActivity extends AppCompatActivity {

    VideoView mVideoView;
    ProgressBar mProgressBar;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_play_video);

        Intent intent = getIntent();
        String videoURL = intent.getStringExtra("Video");

        mProgressBar = (ProgressBar) findViewById(R.id.VideoLoadingProgress);

        mVideoView = (VideoView) findViewById(R.id.VideoView);
        mVideoView.setMediaController(new MediaController(this));
        mVideoView.setVideoURI(Uri.parse(videoURL));

        mVideoView.setOnPreparedListener(mediaPlayer -> {
            mediaPlayer.start();
            mediaPlayer.setOnVideoSizeChangedListener((mediaPlayer1, i, i1) -> {
                mProgressBar.setVisibility(View.GONE);
                mediaPlayer.start();
            });
        });
    }
}
