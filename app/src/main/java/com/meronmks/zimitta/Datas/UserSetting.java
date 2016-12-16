package com.meronmks.zimitta.Datas;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

/**
 * Created by meron on 2016/10/02.
 * 設定の読み出しラッパ
 */

public class UserSetting {

    /**
     * ツイートの読み込み数
     * @param context
     * @return
     */
    public static int LoadTweetCount(Context context){
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
        return Integer.parseInt(sp.getString("LoadTweetCount", "20"));
    }

    /**
     * FF外のメンションを取得するか
     * @param context
     * @return
     */
    public static boolean StreamingFFMention(Context context){
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
        return sp.getBoolean("Streaming_FF_Mention", false);
    }

    /**
     * 長押しでメニューを表示するか
     * @param context
     * @return
     */
    public static boolean LongItemClickMenu(Context context){
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
        return sp.getBoolean("LongItemClickMenu", true);
    }

    /**
     * ツイートのカウント表示をするか
     * @param context
     * @return
     */
    public static boolean TextCountVisible(Context context){
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
        return sp.getBoolean("TextCountVisible", true);
    }

    /**
     * ツイート投稿時の確認ダイアログを出すか
     * @param context
     * @return
     */
    public static boolean ShowPostDialog(Context context){
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
        return sp.getBoolean("ShowPostDialog", true);
    }

    /**
     * お気に入り時の確認ダイアログを出すか
     * @param context
     * @return
     */
    public static boolean ShowFavDialog(Context context){
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
        return sp.getBoolean("ShowFavDialog", true);
    }

    /**
     * リツイート時の確認ダイアログを出すか
     * @param context
     * @return
     */
    public static boolean ShowRTDialog(Context context){
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
        return sp.getBoolean("ShowRTDialog", true);
    }

    /**
     * お気に入り＋リツイート時の確認ダイアログを出すか
     * @param context
     * @return
     */
    public static boolean ShowFavRTDialog(Context context){
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
        return sp.getBoolean("ShowFavRTDialog", true);
    }

    /**
     * ツイート削除時の確認ダイアログを出すか
     * @param context
     * @return
     */
    public static boolean ShowTweetDelDialog(Context context){
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
        return sp.getBoolean("ShowTweetDelDialog", true);
    }
}
