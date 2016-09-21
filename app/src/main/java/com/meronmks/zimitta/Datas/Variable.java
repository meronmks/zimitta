package com.meronmks.zimitta.Datas;

import android.content.Context;

import com.meronmks.zimitta.Adapter.TweetAdapter;

import twitter4j.TwitterStream;
import twitter4j.conf.Configuration;

/**
 * Created by meron on 2016/09/15.
 */
public class Variable {
    public static long userID;
    public static long[] muteList;
    public static String userName;
    public static TweetAdapter TLAdapter;
    public static TweetAdapter MentionsAdapter;
    public static Configuration conf;
    public static TwitterStream twitterStream;
    public static final String ACTION_INVOKED = "com.meronmks.zimitta.ACTION_INVOKED";
    public static final String STREAM_PARCELABLE = "STREAM_PARCELABLE";
    public static final String STREAM_BUNDLE = "STREAM_BUNDLE";
    public static RateLimits rateLimits;


    public static boolean iniVariable(Context context){
        Destroy();
        try{
            TLAdapter = new TweetAdapter(context);
            MentionsAdapter = new TweetAdapter(context);
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
        twitterStream = null;
        rateLimits = null;
    }
}
