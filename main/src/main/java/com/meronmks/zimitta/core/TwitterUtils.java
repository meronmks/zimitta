package com.meronmks.zimitta.core;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import com.meronmks.zimitta.R;

import twitter4j.Twitter;
import twitter4j.TwitterFactory;
import twitter4j.auth.AccessToken;

public class TwitterUtils {

    /**
     * Twitterインスタンスを取得します。アクセストークンが保存されていれば自動的にセットします。
     *
     * @param context
     * @return
     */
    public static Twitter getTwitterInstance(Context context,long ID) {

        TwitterFactory factory = new TwitterFactory();
        Twitter twitter = factory.getInstance();
        twitter.setOAuthConsumer(OAuthVariable.twitterConsumerKey, OAuthVariable.twitterConsumerSecret);

        if (hasAccessToken(context,ID)) {
            twitter.setOAuthAccessToken(loadAccessToken(context,ID));
        }
        return twitter;
    }

    public static Twitter addTwitterInstance(Context context) {

        TwitterFactory factory = new TwitterFactory();
        Twitter twitter = factory.getInstance();
        twitter.setOAuthConsumer(OAuthVariable.twitterConsumerKey, OAuthVariable.twitterConsumerSecret);

        return twitter;
    }

    /**
     * アクセストークンをプリファレンスに保存します。
     *
     * @param context
     * @param accessToken
     */
    public static void storeAccessToken(Context context, AccessToken accessToken,long ID) {
        SharedPreferences preferences = context.getSharedPreferences(context.getString(R.string.PREF_NAME) + ID, Context.MODE_PRIVATE);
        Editor editor = preferences.edit();
        editor.putString(context.getString(R.string.TOKEN), accessToken.getToken());
        editor.putString(context.getString(R.string.TOKEN_SECRET), accessToken.getTokenSecret());
        editor.commit();
    }

    /**
     * アクセストークンをプリファレンスから読み込みます。
     *
     * @param context
     * @return
     */
    public static AccessToken loadAccessToken(Context context,long ID) {
        SharedPreferences preferences = context.getSharedPreferences(context.getString(R.string.PREF_NAME) + ID, Context.MODE_PRIVATE);
        String token = preferences.getString(context.getString(R.string.TOKEN), null);
        String tokenSecret = preferences.getString(context.getString(R.string.TOKEN_SECRET), null);
        if (token != null && tokenSecret != null) {
            return new AccessToken(token, tokenSecret);
        } else {
            return null;
        }
    }

    /**
     * アクセストークンが存在する場合はtrueを返します。
     *
     * @return
     */
    public static boolean hasAccessToken(Context context,long ID) {
        return loadAccessToken(context,ID) != null;
    }
}