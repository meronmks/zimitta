package com.meronmks.zimitta.TwitterUtil;

import android.content.Context;
import android.content.SharedPreferences;

import com.meronmks.zimitta.Datas.UserSetting;
import com.meronmks.zimitta.Datas.Variable;
import com.meronmks.zimitta.OAuth.OAuthVariable;
import com.meronmks.zimitta.OAuth.OauthUtils;
import com.meronmks.zimitta.R;

import java.io.File;

import twitter4j.AsyncTwitter;
import twitter4j.AsyncTwitterFactory;
import twitter4j.Paging;
import twitter4j.StatusUpdate;
import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.TwitterFactory;
import twitter4j.TwitterListener;
import twitter4j.TwitterStreamFactory;
import twitter4j.UploadedMedia;
import twitter4j.UserList;
import twitter4j.conf.ConfigurationBuilder;

/**
 * Twitterとの通信詰め合わせ。結果はリスナー経由
 * Created by meron on 2016/09/14.
 */
public class TwitterAction {

    private AsyncTwitter asyncTwitter;
    private Twitter twitter;
    private ConfigurationBuilder builder;


    public TwitterAction(Context context, TwitterListener twitterListener){

        if(Variable.conf == null){
            makeConfigurationBuilder(context);
        }
        //AsyncTwitterFactoryをインスタンス化する
        AsyncTwitterFactory asyncTwitterFactory = new AsyncTwitterFactory(Variable.conf);
        //Twitterをインスタンス化する
        asyncTwitter = asyncTwitterFactory.getInstance();
        asyncTwitter.addListener(twitterListener);

        TwitterFactory twitterFactory = new TwitterFactory(Variable.conf);
        twitter = twitterFactory.getInstance();

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
        SharedPreferences preferences = context.getSharedPreferences(context.getString(R.string.Account), 0);
        OauthUtils.hasAccessToken(context, preferences.getLong(context.getString(R.string.ActiveAccount), 0));
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
        asyncTwitter.updateStatus(statusUpdate);
    }

    /**
     * タイムラインの取得
     * @param p 取得時のオプション
     */
    public void getHomeTimeline(Paging p){
        asyncTwitter.getHomeTimeline(p);
    }

    /**
     * Mentionの取得
     * @param p 取得時のオプション
     */
    public void getMentions(Paging p){
        asyncTwitter.getMentions(p);
    }

    /**
     * ログインしているユーザの情報取得
     */
    public void getVerifyCredentials(){
        asyncTwitter.verifyCredentials();
    }

    /**
     * ミュートしているユーザのID取得
     */
    public void getMutesIDs(){
        asyncTwitter.getMutesIDs(-1);
    }

    /**
     * 指定IDのツイートをリツイートする
     * @param ID ツイートのID
     */
    public void retweetStatus(long ID){
        asyncTwitter.retweetStatus(ID);
    }

    /**
     * 指定IDのツイートをお気に入りする
     * @param ID ツイートのID
     */
    public void createFavorite(long ID){
        asyncTwitter.createFavorite(ID);
    }

    /**
     * 指定IDのツイートのお気に入りを解除する
     * @param ID ツイートのID
     */
    public void destroyFavorite(long ID){
        asyncTwitter.destroyFavorite(ID);
    }

    /**
     * 指定IDのツイートを削除する
     * @param ID ツイートのID
     */
    public void destroyStatus(long ID){
        asyncTwitter.destroyStatus(ID);
    }

    /**
     * アカウントで作られたor保存しているListの呼び出し
     * @param userID ユーザの固有ID
     */
    public void getUserLists(long userID){
        asyncTwitter.getUserLists(userID);
    }

    /**
     * ListのTLを取得
     * @param listID 取得したListのID
     * @param paging 各種設定等
     */
    public void getUserListStatuses(long listID, Paging paging){
        asyncTwitter.getUserListStatuses(listID, paging);
    }

    /**
     * リストに登録されているメンバーの取得（２０人ずつ）
     * @param listID 取得したいListのID
     * @param cursor ページ数的な奴
     */
    public void getUserListMembers(long listID, long cursor){
        asyncTwitter.getUserListMembers(listID, cursor);
    }

    /**
     * 複数の画像を添付する
     * @param file
     * @throws TwitterException
     */
    public UploadedMedia uploadMedia(File file) throws TwitterException {
        return twitter.uploadMedia(file);
    }
}
