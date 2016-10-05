package com.meronmks.zimitta.TwitterUtil;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.meronmks.zimitta.Datas.UserSetting;
import com.meronmks.zimitta.Datas.Variable;
import com.meronmks.zimitta.OAuth.OAuthVariable;
import com.meronmks.zimitta.R;

import twitter4j.AsyncTwitter;
import twitter4j.AsyncTwitterFactory;
import twitter4j.Paging;
import twitter4j.StatusUpdate;
import twitter4j.TwitterListener;
import twitter4j.TwitterStreamFactory;
import twitter4j.conf.ConfigurationBuilder;

/**
 * Twitterとの通信詰め合わせ。結果はリスナー経由
 * Created by meron on 2016/09/14.
 */
public class TwitterAction {

    private AsyncTwitter mTwitter;
    private ConfigurationBuilder builder;


    public TwitterAction(Context context, TwitterListener twitterListener){

        if(Variable.conf == null){
            makeConfigurationBuilder(context);
        }
        //AsyncTwitterFactoryをインスタンス化する
        AsyncTwitterFactory twitterFactory = new AsyncTwitterFactory(Variable.conf);
        //Twitterをインスタンス化する
        mTwitter = twitterFactory.getInstance();
        mTwitter.addListener(twitterListener);

        if(Variable.twitterStream == null) {
            TwitterStreamFactory factory = new TwitterStreamFactory(Variable.conf);
            Variable.twitterStream = factory.getInstance();
            StreamAdapter streamAdapter = new StreamAdapter(context);
            Variable.twitterStream.addListener(streamAdapter);
        }
    }

    /**
     * ConfigurationBuilderをつくる奴～～～
     * @param context
     */
    private void makeConfigurationBuilder(Context context){
        builder = new ConfigurationBuilder();
        // Twitter4Jに対してOAuth情報を設定
        // アプリ固有の情報
        builder.setOAuthConsumerKey(OAuthVariable.TWITTER_KEY);
        builder.setOAuthConsumerSecret(OAuthVariable.TWITTER_SECRET);
        // アプリ＋ユーザー固有の情報
        builder.setOAuthAccessToken(Variable.userInfo.token);
        builder.setOAuthAccessTokenSecret(Variable.userInfo.tokenSecret);
        //HTTPタイムアウト設定(ミリ秒)
        builder.setHttpConnectionTimeout(10000);
        builder.setJSONStoreEnabled(true);
        //ストリーミング時にリプも表示するかどうか？
        if(UserSetting.StreamingFFMention(context)) {
            builder.setUserStreamRepliesAllEnabled(true);
        }else{
            builder.setUserStreamRepliesAllEnabled(false);
        }
        Variable.conf = builder.build();
    }

    /**
     * ツイート投稿
     * @param statusUpdate 投稿する内容
     */
    public void statusUpdate(StatusUpdate statusUpdate){
        mTwitter.updateStatus(statusUpdate);
    }

    /**
     * タイムラインの取得
     * @param p 取得時のオプション
     */
    public void getHomeTimeline(Paging p){
        mTwitter.getHomeTimeline(p);
    }

    /**
     * Mentionの取得
     * @param p 取得時のオプション
     */
    public void getMentions(Paging p){
        mTwitter.getMentions(p);
    }

    /**
     * ログインしているユーザの情報取得
     */
    public void getVerifyCredentials(){
        mTwitter.verifyCredentials();
    }

    /**
     * ミュートしているユーザのID取得
     */
    public void getMutesIDs(){
        mTwitter.getMutesIDs(-1);
    }

    /**
     * 指定IDのツイートをリツイートする
     * @param ID ツイートのID
     */
    public void retweetStatus(long ID){
        mTwitter.retweetStatus(ID);
    }

    /**
     * 指定IDのツイートをお気に入りする
     * @param ID ツイートのID
     */
    public void createFavorite(long ID){
        mTwitter.createFavorite(ID);
    }

    /**
     * 指定IDのツイートのお気に入りを解除する
     * @param ID ツイートのID
     */
    public void destroyFavorite(long ID){
        mTwitter.destroyFavorite(ID);
    }

    /**
     * 指定IDのツイートを削除する
     * @param ID ツイートのID
     */
    public void destroyStatus(long ID){
        mTwitter.destroyStatus(ID);
    }
}
