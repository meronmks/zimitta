package com.meronmks.zimitta.Datas;

import android.content.Context;

import com.meronmks.zimitta.Adapter.TweetAdapter;

import twitter4j.conf.Configuration;

/**
 * Created by meron on 2016/09/15.
 */
public class Variable {
    public static long userID;
    public static long[] muteList;
    public static String userName;
    public static TweetAdapter TLAdapter;
    public static Configuration conf;

    public static boolean iniVariable(Context context){
        Destroy();
        try{
            TLAdapter = new TweetAdapter(context);
        }catch (Exception e){
            return false;
        }
        return true;
    }

    public static void Destroy(){
        userID = 0;
        muteList = null;
        userName = "";
        TLAdapter = null;
        conf = null;
    }
}
