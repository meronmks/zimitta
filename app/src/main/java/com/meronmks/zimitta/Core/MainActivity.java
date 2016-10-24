package com.meronmks.zimitta.Core;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.view.ViewPager;

import com.crashlytics.android.Crashlytics;
import com.crashlytics.android.answers.Answers;
import com.jakewharton.rxbinding.view.RxView;
import com.meronmks.zimitta.Activity.AccountChangeActivity;
import com.meronmks.zimitta.Activity.MakeTweetActivity;
import com.meronmks.zimitta.Adapter.MainPagerAdapter;
import com.meronmks.zimitta.Datas.ErrorLogs;
import com.meronmks.zimitta.Datas.UserInfo;
import com.meronmks.zimitta.Datas.Variable;
import com.meronmks.zimitta.Menus.MainMenu;
import com.meronmks.zimitta.OAuth.OAuthVariable;
import com.meronmks.zimitta.OAuth.OauthUtils;
import com.meronmks.zimitta.OAuth.TwitterOAuthActivity;
import com.meronmks.zimitta.R;
import com.meronmks.zimitta.TwitterUtil.TwitterAction;
import com.twitter.sdk.android.Twitter;
import com.twitter.sdk.android.core.TwitterAuthConfig;

import org.json.JSONArray;

import java.util.ArrayList;
import java.util.List;

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

        ErrorLogs.loadLog(this);

        //TODO 5.0.0正規バージョンまでにはCrashlytics及びAnswersの機能をON,OFF可能へ
        Fabric.Builder builder = new Fabric.Builder(this)
                .kits(new Twitter(authConfig), new Crashlytics(), new Answers());
        Fabric.with(builder.build());
        setContentView(R.layout.activity_main);

        preferences = getSharedPreferences(getString(R.string.Account), 0);

        if(NetStatusUtil.isOnline(this) && !OauthUtils.hasAccessToken(this, preferences.getLong(getString(R.string.ActiveAccount), 0))){
            Intent intent = new Intent(this, TwitterOAuthActivity.class);
            intent.putExtra("Flag", false);
            startActivity(intent);
            finish();
        }else if(NetStatusUtil.isOnline(this)){
            //Fragment準備
            android.support.v4.app.FragmentManager fragmentManager = getSupportFragmentManager();
            MainPagerAdapter pagerAdapter = new MainPagerAdapter(fragmentManager,this);
            ViewPager viewPager = (ViewPager) findViewById(R.id.pager);
            viewPager.setAdapter(pagerAdapter);
            mAction = new TwitterAction(this, listener);
            mAction.getMutesIDs();
            mAction.getVerifyCredentials();
        }

        RxView.clicks(findViewById(R.id.tweet))
                .subscribe(x -> {
                    Intent intent = new Intent(this, MakeTweetActivity.class);
                    intent.putExtra("mention", false);
                    startActivity(intent);
                });

        RxView.clicks(findViewById(R.id.Menu_button))
                .subscribe(x -> showMenu());
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Variable.userInfo.saveInstance(this, getSharedPreferences(getString(R.string.Account), 0).getLong(getString(R.string.ActiveAccount), 0));
        if(Variable.twitterStream != null) {
            Variable.twitterStream.shutdown();
        }
        ErrorLogs.saveLog(this);
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
                Variable.userInfo.userID = user.getId();
                Variable.userInfo.userName = user.getName();
                Variable.userInfo.userScreenName = user.getScreenName();
                Variable.userInfo.userProfileImageURLHttps = user.getProfileImageURLHttps();
                Variable.userInfo.saveInstance(getApplicationContext(), getSharedPreferences(getString(R.string.Account), 0).getLong(getString(R.string.ActiveAccount), 0));
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
                        ErrorLogs.putErrorLog("ユーザの情報取得に失敗しました", te.getMessage());
                        break;
                    case MUTE_LIST_IDS:
                        showToast("ミュート一覧の取得に失敗しました");
                        ErrorLogs.putErrorLog("ミュート一覧の取得に失敗しました", te.getMessage());
                        break;
                }
            });
        }
    };

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(requestCode != 104) return;

        if(data != null && data.getStringExtra("accountChange").equals("accountChange")){
            finish();
        }
    }
}
