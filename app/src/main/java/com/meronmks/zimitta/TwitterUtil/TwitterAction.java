package com.meronmks.zimitta.TwitterUtil;

import android.content.Context;
import android.content.SharedPreferences;

import com.meronmks.zimitta.Datas.Variable;
import com.meronmks.zimitta.OAuth.OAuthVariable;
import com.meronmks.zimitta.R;

import twitter4j.AsyncTwitter;
import twitter4j.AsyncTwitterFactory;
import twitter4j.Paging;
import twitter4j.TwitterListener;
import twitter4j.conf.Configuration;
import twitter4j.conf.ConfigurationBuilder;

/**
 * Created by meron on 2016/09/14.
 */
public class TwitterAction {

    private TwitterListener mTwitterListener;
    private AsyncTwitter mTwitter;
    private ConfigurationBuilder builder;


    public TwitterAction(Context context, TwitterListener twitterListener){
        mTwitterListener = twitterListener;

        if(Variable.conf == null){
            makeConfigurationBuilder(context);
        }
        //TwitterFactoryをインスタンス化する
        AsyncTwitterFactory twitterFactory = new AsyncTwitterFactory(Variable.conf);
        //Twitterをインスタンス化する
        mTwitter = twitterFactory.getInstance();
        mTwitter.addListener(twitterListener);
    }

    /**
     * ConfigurationBuilderをつくる奴～～～
     * @param context
     */
    private void makeConfigurationBuilder(Context context){
        builder = new ConfigurationBuilder();
        SharedPreferences accountIDCount = context.getSharedPreferences(context.getString(R.string.Account), 0);
        SharedPreferences spOauth = context.getSharedPreferences(context.getString(R.string.PREF_NAME) + accountIDCount.getLong(context.getString(R.string.ActiveAccount), 0), Context.MODE_PRIVATE);
        // Twitter4Jに対してOAuth情報を設定
        // アプリ固有の情報
        builder.setOAuthConsumerKey(OAuthVariable.TWITTER_KEY);
        builder.setOAuthConsumerSecret(OAuthVariable.TWITTER_SECRET);
        // アプリ＋ユーザー固有の情報
        builder.setOAuthAccessToken(spOauth.getString(context.getString(R.string.TOKEN), null));
        builder.setOAuthAccessTokenSecret(spOauth.getString(context.getString(R.string.TOKEN_SECRET), null));
        //HTTPタイムアウト設定(ミリ秒)
        builder.setHttpConnectionTimeout(10000);
        builder.setJSONStoreEnabled(true);

        Variable.conf = builder.build();
    }

    public void getHomeTimeline(Paging p){
        mTwitter.getHomeTimeline(p);
    }
}
