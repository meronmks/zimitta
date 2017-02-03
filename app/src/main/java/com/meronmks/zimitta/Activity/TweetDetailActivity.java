package com.meronmks.zimitta.Activity;

import android.os.Bundle;
import android.support.annotation.Nullable;

import com.meronmks.zimitta.Core.BaseActivity;
import com.meronmks.zimitta.Datas.ParcelStatus;
import com.meronmks.zimitta.R;

import twitter4j.Status;

/**
 * Created by meron on 2016/12/29.
 */

public class TweetDetailActivity extends BaseActivity {
    private Status status;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tweet_detail);
        ParcelStatus ps = getIntent().getParcelableExtra("status");
        status = ps.status;
        settingItemVIew(status);
    }
}
