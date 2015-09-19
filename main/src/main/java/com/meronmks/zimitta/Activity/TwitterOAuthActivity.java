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
import android.view.View;
import com.meronmks.zimitta.AppCooperation.WebTwitterLoginActivity;
import com.meronmks.zimitta.R;
import com.meronmks.zimitta.core.CoreActivity;
import com.meronmks.zimitta.core.TwitterUtils;
import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.User;
import twitter4j.auth.AccessToken;
import twitter4j.auth.RequestToken;

public class TwitterOAuthActivity extends ActionBarActivity {

    private String mCallbackURL;
    private Twitter mTwitter;
    private RequestToken mRequestToken;
    private SharedPreferences accountIDCount,ScreanNames;

	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_twitter_oauth);

        mCallbackURL = getString(R.string.twitter_callback_url);

        Intent intent = getIntent();
        Boolean Flag = intent.getBooleanExtra("Flag",true);

        if(Flag)
        {
            mTwitter = TwitterUtils.addTwitterInstance(this);
        }
        else{
            accountIDCount = getSharedPreferences("accountidcount", 0);
            mTwitter = TwitterUtils.getTwitterInstance(this,accountIDCount.getLong("ID_Num_Now", 0));
        }

        findViewById(R.id.actionStartOauth).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startAuthorize();
            }
        });

        findViewById(R.id.AppRenkeiButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent SettingsApplications = new Intent(TwitterOAuthActivity.this, WebTwitterLoginActivity.class);
                startActivity(SettingsApplications);
            }
        });
    }

    @Override
	protected void onResume() {
		super.onResume();
		PaintDrawable paintDrawable = new PaintDrawable(Color.argb(255, 0, 0, 0));
        getWindow().setBackgroundDrawable(paintDrawable);
	}

    /**
     * OAuth認証（厳密には認可）を開始します。
     *
     */
    private void startAuthorize() {
        AsyncTask<Void, Void, String> task = new AsyncTask<Void, Void, String>() {
            @Override
            protected String doInBackground(Void... params) {
                try {
                    mRequestToken = mTwitter.getOAuthRequestToken(mCallbackURL);
                    return mRequestToken.getAuthorizationURL();
                } catch (TwitterException e) {
                    e.printStackTrace();
                }
                return null;
            }

            @Override
            protected void onPostExecute(String url) {
                if (url != null) {
                    Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                    startActivity(intent);
                } else {
                    // 失敗。。。
                }
            }
        };
        task.execute();
    }

    @Override
    public void onNewIntent(Intent intent) {
        if (intent == null
                || intent.getData() == null
                || !intent.getData().toString().startsWith(mCallbackURL)) {
            return;
        }
        String verifier = intent.getData().getQueryParameter("oauth_verifier");

        AsyncTask<String, Void, AccessToken> task = new AsyncTask<String, Void, AccessToken>() {
            @Override
            protected AccessToken doInBackground(String... params) {
                try {
                    return mTwitter.getOAuthAccessToken(mRequestToken, params[0]);
                } catch (TwitterException e) {
                    e.printStackTrace();
                }catch (Exception e) {
					e.printStackTrace();
				}
                return null;
            }

            @Override
            protected void onPostExecute(AccessToken accessToken) {
                if (accessToken != null) {
                    // 認証成功！
                    CoreActivity.showToast("認証成功！");
                    successOAuth(accessToken);
                } else {
                    // 認証失敗。。。
                    CoreActivity.showToast("認証失敗。。。");
                }
            }
        };
        task.execute(verifier);
    }

    private void successOAuth(AccessToken accessToken) {
    	accountIDCount = getSharedPreferences("accountidcount", 0);
    	long ID = accountIDCount.getLong("ID_Num", 0);
    	getScreanname(accessToken,ID);
    }

    private void getScreanname(final AccessToken accessToken,final long ID)
    {
    	AsyncTask<Void, Void, String> task = new AsyncTask<Void, Void, String>() {

			@Override
			protected String doInBackground(Void... params) {
				try
				{
					TwitterUtils.storeAccessToken(TwitterOAuthActivity.this, accessToken, ID);
					User user = mTwitter.verifyCredentials();//Userオブジェクトを作成
					return user.getScreenName();
				 } catch (TwitterException e) {
	                    e.printStackTrace();
	                }catch (Exception e) {
						e.printStackTrace();
					}
				return null;
			}

			@Override
            protected void onPostExecute(String user) {
                if (user != null) {
                    // 取得成功！
                	Editor e = accountIDCount.edit();
                    e.putLong("ID_Num", ID + 1);	//追加
                    e.putLong("ID_Num_Now", ID);
                    e.commit();
                    ScreanNames = getSharedPreferences("ScreanNames", 0);
                    Editor e1 = ScreanNames.edit();
                    StringBuilder sb = new StringBuilder();
                    sb.append("ScreanName");
                    sb.append(ID);
                    String str = new String(sb);
                    e1.putString(str, user);
                    e1.commit();
                    Intent intent = new Intent(TwitterOAuthActivity.this, CoreActivity.class);
                    startActivity(intent);
                    finish();
                } else {
                    // 取得失敗。。。
                	Intent intent = new Intent(TwitterOAuthActivity.this, CoreActivity.class);
                    startActivity(intent);
                    finish();
                }
            }
    	};
        task.execute();
    }
}