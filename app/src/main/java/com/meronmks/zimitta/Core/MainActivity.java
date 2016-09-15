package com.meronmks.zimitta.Core;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Toast;

import com.meronmks.zimitta.Adapter.MainPagerAdapter;
import com.meronmks.zimitta.Datas.Variable;
import com.meronmks.zimitta.OAuth.OAuthVariable;
import com.meronmks.zimitta.OAuth.OauthUtils;
import com.meronmks.zimitta.OAuth.TwitterOAuthActivity;
import com.meronmks.zimitta.R;
import com.twitter.sdk.android.Twitter;
import com.twitter.sdk.android.core.TwitterAuthConfig;

import io.fabric.sdk.android.Fabric;

public class MainActivity extends AppCompatActivity {

    private SharedPreferences preferences;
    private static Context mContext;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mContext = this;
        Variable.iniVariable(mContext);
        TwitterAuthConfig authConfig = new TwitterAuthConfig(OAuthVariable.TWITTER_KEY, OAuthVariable.TWITTER_SECRET);
        Fabric.with(this, new Twitter(authConfig));
        setContentView(R.layout.activity_main);

        preferences = getSharedPreferences(getString(R.string.Account), 0);

        if(!OauthUtils.hasAccessToken(this, preferences.getLong(getString(R.string.ActiveAccount), 0))){
            Intent intent = new Intent(this, TwitterOAuthActivity.class);
            intent.putExtra("Flag", false);
            startActivity(intent);
            finish();
        }else{
            //Fragment準備
            android.support.v4.app.FragmentManager fragmentManager = getSupportFragmentManager();
            MainPagerAdapter pagerAdapter = new MainPagerAdapter(fragmentManager,this);
            ViewPager viewPager = (ViewPager) findViewById(R.id.pager);
            viewPager.setAdapter(pagerAdapter);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Variable.Destroy();
    }

    /**
     * トースト表示処理
     */
    public static void showToast(String text){
        if(mContext == null || text == null || text.length() == 0) return;
        Toast.makeText(mContext, text, Toast.LENGTH_SHORT).show();
    }
}
