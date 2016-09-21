package com.meronmks.zimitta.Core;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Toast;

import com.crashlytics.android.Crashlytics;
import com.crashlytics.android.answers.Answers;
import com.jakewharton.rxbinding.view.RxView;
import com.meronmks.zimitta.Activity.MakeTweetActivity;
import com.meronmks.zimitta.Adapter.MainPagerAdapter;
import com.meronmks.zimitta.Datas.Variable;
import com.meronmks.zimitta.Menus.MainMenu;
import com.meronmks.zimitta.OAuth.OAuthVariable;
import com.meronmks.zimitta.OAuth.OauthUtils;
import com.meronmks.zimitta.OAuth.TwitterOAuthActivity;
import com.meronmks.zimitta.R;
import com.meronmks.zimitta.TwitterUtil.TwitterAction;
import com.twitter.sdk.android.Twitter;
import com.twitter.sdk.android.core.TwitterAuthConfig;

import io.fabric.sdk.android.Fabric;
import twitter4j.IDs;
import twitter4j.TwitterAdapter;
import twitter4j.TwitterException;
import twitter4j.TwitterListener;
import twitter4j.TwitterMethod;
import twitter4j.User;

public class MainActivity extends BaseActivity {

    private SharedPreferences preferences;
    private TwitterAction mAction;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Variable.iniVariable(this);
        TwitterAuthConfig authConfig = new TwitterAuthConfig(OAuthVariable.TWITTER_KEY, OAuthVariable.TWITTER_SECRET);
        Fabric.with(this, new Twitter(authConfig));
        Fabric.with(this, new Crashlytics());
        Fabric.with(this, new Answers());
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
            mAction = new TwitterAction(this, listener);
            mAction.getVerifyCredentials();
            mAction.getMutesIDs();
        }

        RxView.clicks(findViewById(R.id.tweet))
                .subscribe(x -> {
                    Intent intent = new Intent(this, MakeTweetActivity.class);
                    startActivity(intent);
                });

        RxView.clicks(findViewById(R.id.Menu_button))
                .subscribe(x -> showMenu());
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(Variable.twitterStream != null) {
            Variable.twitterStream.shutdown();
        }
        Variable.Destroy();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    /**
     * メニューの表示
     */
    private void showMenu(){
        MainMenu mainMenu = new MainMenu(this);
        mainMenu.show();
    }

    /**
     * Listener定義
     */
    private TwitterListener listener = new TwitterAdapter() {
        @Override
        public void verifiedCredentials(User user) {
            if(user != null){
                Variable.userID = user.getId();
                Variable.userName = user.getScreenName();
            }
        }

        @Override
        public void gotMuteIDs(IDs blockingUsersIDs) {
            if(blockingUsersIDs == null){
                Variable.muteList = null;
                Variable.muteList = new long[1];
                Variable.muteList[0] = 0;
            }else{
                Variable.muteList = null;
                Variable.muteList = new long[blockingUsersIDs.getIDs().length];
                Variable.muteList = blockingUsersIDs.getIDs();
            }
        }

        @Override
        public void onException(TwitterException te, TwitterMethod method) {
            runOnUiThread(() -> {
                switch (method){
                    case VERIFY_CREDENTIALS:
                        showToast("ユーザの情報取得に失敗しました");
                        break;
                    case MUTE_LIST_IDS:
                        showToast("ミュート一覧の取得に失敗しました");
                        break;
                }
            });
        }
    };
}
