package com.meronmks.zimitta.Activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.graphics.Color;
import android.graphics.drawable.PaintDrawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import com.meronmks.zimitta.AppCooperation.WebTwitterLoginActivity;
import com.meronmks.zimitta.R;
import com.meronmks.zimitta.core.CoreActivity;
import com.meronmks.zimitta.core.TwitterUtils;
import com.twitter.sdk.android.core.Callback;
import com.twitter.sdk.android.core.Result;
import com.twitter.sdk.android.core.TwitterSession;
import com.twitter.sdk.android.core.identity.TwitterLoginButton;

import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.User;
import twitter4j.auth.AccessToken;
import twitter4j.auth.RequestToken;

public class TwitterOAuthActivity extends AppCompatActivity {

    private TwitterLoginButton twitterLoginButton;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_twitter_oauth);

        twitterLoginButton = (TwitterLoginButton)findViewById(R.id.login_button);
        twitterLoginButton.setCallback(new Callback<TwitterSession>() {
            @Override
            public void success(Result<TwitterSession> result) {
                CoreActivity.showToast("認証成功！");
                setOauthResult(result.data);
            }

            @Override
            public void failure(com.twitter.sdk.android.core.TwitterException exception) {
                CoreActivity.showToast("認証失敗・・");
            }
        });
    }

    private void setOauthResult(TwitterSession twitterSession){
        SharedPreferences selectAccount = getSharedPreferences(getString(R.string.SelectAccount), 0);
        long accountNum = selectAccount.getLong(getString(R.string.AccountNum), 0);
        TwitterUtils.storeAccessToken(this, twitterSession, accountNum);

        SharedPreferences.Editor e = selectAccount.edit();
        e.putLong(getString(R.string.AccountNum), accountNum + 1);	//追加
        e.putLong(getString(R.string.SelectAccountNum), accountNum);
        e.commit();
        SharedPreferences screanNames = getSharedPreferences(getString(R.string.ScreanNames), 0);
        SharedPreferences.Editor e1 = screanNames.edit();
        StringBuilder sb = new StringBuilder();
        sb.append(getString(R.string.ScreanNames));
        sb.append(accountNum);
        String str = new String(sb);
        e1.putString(str, twitterSession.getUserName());
        e1.commit();
        Intent intent = new Intent(TwitterOAuthActivity.this, CoreActivity.class);
        startActivity(intent);
        finish();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        twitterLoginButton.onActivityResult(requestCode, resultCode, data);
    }

}