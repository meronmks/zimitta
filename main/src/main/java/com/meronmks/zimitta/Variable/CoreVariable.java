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
 */
public class CoreVariable {
    public static long Userid;
    public static long[] mutelist;
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

    public static void iniVariable(Context context){
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
        Userid = 0;
        userName = "";
        userLists = null;
        mutelist = null;
        twitterStream = null;
        runStream = false;
        searchTweet = null;
    }
}
