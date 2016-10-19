package com.meronmks.zimitta.OAuth;

import android.content.Context;
import android.content.SharedPreferences;

import com.meronmks.zimitta.Datas.UserInfo;
import com.meronmks.zimitta.Datas.Variable;
import com.meronmks.zimitta.R;
import com.twitter.sdk.android.core.TwitterSession;

import twitter4j.Twitter;
import twitter4j.TwitterFactory;
import twitter4j.auth.AccessToken;

/**
 * Created by meron on 2016/09/13.
 */
public class OauthUtils {
    /**
     * Twitterインスタンスを取得します。アクセストークンが保存されていれば自動的にセットします。
     *
     * @param context
     * @return
     */
    public static Twitter getTwitterInstance(Context context, long ID) {

        TwitterFactory factory = new TwitterFactory();
        Twitter twitter = factory.getInstance();
        twitter.setOAuthConsumer(OAuthVariable.TWITTER_KEY, OAuthVariable.TWITTER_SECRET);

        if (hasAccessToken(context,ID)) {
            twitter.setOAuthAccessToken(loadAccessToken(context,ID));
        }
        return twitter;
    }

    /**
     * アクセストークンをプリファレンスに保存します。
     *
     * @param context
     * @param twitterSession
     */
    public static void storeAccessToken(Context context, TwitterSession twitterSession, long ID) {
        Variable.userInfo = UserInfo.getInstance(context, ID);
        Variable.userInfo.token = twitterSession.getAuthToken().token;
        Variable.userInfo.tokenSecret = twitterSession.getAuthToken().secret;
        Variable.userInfo.userID = twitterSession.getUserId();
        Variable.userInfo.userName = twitterSession.getUserName();
        Variable.userInfo.saveInstance(context, ID);
    }

    /**
     * アクセストークンをプリファレンスから読み込みます。
     *
     * @param context
     * @return
     */
    public static AccessToken loadAccessToken(Context context,long ID) {
        Variable.userInfo = UserInfo.getInstance(context, ID);
        if (!Variable.userInfo.token.equals("") && !Variable.userInfo.tokenSecret.equals("")) {
            return new AccessToken(Variable.userInfo.token, Variable.userInfo.tokenSecret);
        } else {
            return null;
        }
    }

    /**
     * アクセストークンが存在する場合はtrueを返します。
     *
     * @return
     */
    public static boolean hasAccessToken(Context context, long ID) {
        return loadAccessToken(context,ID) != null;
    }
}
