package com.meronmks.zimitta.Activity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.widget.MediaController;
import android.widget.VideoView;

import com.meronmks.zimitta.R;

/**
 * Created by meron on 2016/09/19.
 */
public class PlayVideoActivity extends AppCompatActivity {

    VideoView videoView;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_play_video);

        Intent intent = getIntent();
        String videoURL = intent.getStringExtra("Video");

        videoView = (VideoView) findViewById(R.id.VideoView);
        videoView.setMediaController(new MediaController(this));
        videoView.setVideoURI(Uri.parse(videoURL));
    }
}
