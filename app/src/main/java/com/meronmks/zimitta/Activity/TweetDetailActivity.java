package com.meronmks.zimitta.Activity;

import android.os.Bundle;
import android.support.annotation.Nullable;

import com.meronmks.zimitta.Core.BaseActivity;
import com.meronmks.zimitta.R;

/**
 * Created by meron on 2016/12/29.
 */

public class TweetDetailActivity extends BaseActivity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tweet_detail);
    }
}
