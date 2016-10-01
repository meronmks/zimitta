package com.meronmks.zimitta.Datas;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.text.TextUtils;

import com.google.gson.Gson;

/**
 * Created by meron on 2016/10/02.
 */

public class UserInfo {

    public String token;
    public String tokenSecret;
    public long userID;
    public String userName;
    private static final String USER_INFO_KEY = "USER_INFO";

    /**
     * 保存情報取得
     * @param context
     * @return
     */
    public static UserInfo getInstance(Context context, long ID) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        Gson gson = new Gson();
        String userSettingString = prefs.getString(USER_INFO_KEY + ID, "");

        UserInfo instance;
        if(!TextUtils.isEmpty(userSettingString)) {
            instance = gson.fromJson(userSettingString, UserInfo.class);
        }else {
            instance = getDefaultInstance();
        }
        return instance;
    }

    /**
     * デフォ値
     * @return
     */
    public static UserInfo getDefaultInstance() {
        UserInfo instance = new UserInfo();
        instance.token = "";
        instance.tokenSecret = "";
        instance.userID = 0;
        instance.userName = "";
        return instance;
    }

    /**
     * 設定保存
     * @param context
     */
    public void saveInstance(Context context, long ID) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        Gson gson = new Gson();
        prefs.edit().putString(USER_INFO_KEY + ID, gson.toJson(this)).apply();
    }
}
