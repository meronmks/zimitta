package com.meronmks.zimitta.OAuth;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import com.meronmks.zimitta.Core.MainActivity;
import com.meronmks.zimitta.R;
import com.twitter.sdk.android.core.Callback;
import com.twitter.sdk.android.core.Result;
import com.twitter.sdk.android.core.TwitterSession;
import com.twitter.sdk.android.core.identity.TwitterLoginButton;

/**
 * Created by meron on 2016/09/14.
 */
public class TwitterOAuthActivity extends AppCompatActivity {

    private TwitterLoginButton twitterLoginButton;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_twitter_oauth);

        Toolbar toolbar = (Toolbar) findViewById(R.id.ToolBar);
        toolbar.setTitle("Twitterログイン");

        twitterLoginButton = (TwitterLoginButton)findViewById(R.id.login_button);
        twitterLoginButton.setCallback(new Callback<TwitterSession>() {
            @Override
            public void success(Result<TwitterSession> result) {
                setOauthResult(result.data);
            }

            @Override
            public void failure(com.twitter.sdk.android.core.TwitterException exception) {

            }
        });
    }

    private void setOauthResult(TwitterSession twitterSession){
        SharedPreferences preferences = getSharedPreferences(getString(R.string.Account), 0);
        long accountNum = preferences.getLong(getString(R.string.AccountNum), 0);
        OauthUtils.storeAccessToken(this, twitterSession, accountNum);

        SharedPreferences.Editor e = preferences.edit();
        e.putLong(getString(R.string.AccountNum), accountNum + 1);	//追加
        e.putLong(getString(R.string.ActiveAccount), accountNum);
        e.commit();
        SharedPreferences screanNameList = getSharedPreferences(getString(R.string.ScreanNameList), 0);
        SharedPreferences.Editor e1 = screanNameList.edit();
        StringBuilder sb = new StringBuilder();
        sb.append(getString(R.string.ScreanNameList));
        sb.append(accountNum);
        String str = new String(sb);
        e1.putString(str, twitterSession.getUserName());
        e1.commit();
        Intent intent = new Intent(TwitterOAuthActivity.this, MainActivity.class);
        startActivity(intent);
        finish();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        twitterLoginButton.onActivityResult(requestCode, resultCode, data);
    }

}
