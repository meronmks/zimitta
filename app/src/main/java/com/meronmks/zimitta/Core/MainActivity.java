package com.meronmks.zimitta.Core;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.meronmks.zimitta.Datas.OAuthVariable;
import com.meronmks.zimitta.R;
import com.twitter.sdk.android.Twitter;
import com.twitter.sdk.android.core.TwitterAuthConfig;

import io.fabric.sdk.android.Fabric;

public class MainActivity extends AppCompatActivity {

    private SharedPreferences preferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        TwitterAuthConfig authConfig = new TwitterAuthConfig(OAuthVariable.TWITTER_KEY, OAuthVariable.TWITTER_SECRET);
        Fabric.with(this, new Twitter(authConfig));
        setContentView(R.layout.activity_main);

        preferences = getSharedPreferences(getString(R.string.Account), 0);

        if(!OauthUtils.hasAccessToken(this, preferences.getLong(getString(R.string.ActiveAccount), 0))){

        }
    }
}
