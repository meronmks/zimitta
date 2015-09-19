package com.meronmks.zimitta.Variable;

import android.content.Context;
import com.meronmks.zimitta.Adapter.DMAdapter;
import com.meronmks.zimitta.Adapter.TweetAdapter;
import twitter4j.ResponseList;
import twitter4j.Status;
import twitter4j.TwitterStream;
import twitter4j.UserList;

import java.util.ArrayList;

/**
 * Created by p-user on 2015/06/01.
 * アプリ内で共有するフィールド定義と初期化等
 */
public class CoreVariable {
    public static long userID;
    public static long[] muteList;
    public static String userName;
    public static ResponseList<UserList> userLists;
    public static TwitterStream twitterStream;
    public static boolean runStream;
    public static TweetAdapter TLmAdapter;
    public static TweetAdapter mentionTLmAdapter;
    public static TweetAdapter listTLmAdapter;
    public static DMAdapter DMAdapter;
    public static ArrayList<Long> deleteTweet;
    public static ArrayList<Status> stockTweet;
    public static TweetAdapter searchTweet;
    public static final int REQUEST_PICK_CONTENT = 0;
    public static final int REQUEST_KITKAT_PICK_CONTENT = 1;
    public static int nowNetworkStatus;

    public static void initializationVariable(Context context){
        Destroy();
        TLmAdapter = new TweetAdapter(context);
        listTLmAdapter = new TweetAdapter(context);
        mentionTLmAdapter = new TweetAdapter(context);
        searchTweet = new TweetAdapter(context);
        DMAdapter = new DMAdapter(context);
        deleteTweet = new ArrayList<Long>();
        stockTweet = new ArrayList<Status>();
    }

    public static void Destroy(){
        userID = 0;
        userName = "";
        userLists = null;
        muteList = null;
        twitterStream = null;
        runStream = false;
        searchTweet = null;
        nowNetworkStatus = -1;
    }
}
