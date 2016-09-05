package com.meronmks.zimitta.core;

import android.app.AlertDialog;
import android.app.NotificationManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
import android.util.Log;
import android.widget.*;
import com.meronmks.zimitta.Adapter.DirectMessageAdapterClass;
import com.meronmks.zimitta.Adapter.TweetAdapter;
import com.meronmks.zimitta.R;
import com.meronmks.zimitta.Variable.CoreVariable;
import com.meronmks.zimitta.Variable.RateLimitVariable;
import com.meronmks.zimitta.menu.List_Menu;
import twitter4j.*;
import twitter4j.conf.Configuration;
import twitter4j.conf.ConfigurationBuilder;

import java.io.File;
import java.util.List;
import java.util.Map;

/**
 * Created by meronmks on 2015/02/08.
 */
public class TwitterActionClass {

    protected Context activity;
    protected Twitter mTwitter;
    protected TweetAdapter mAdapter;
    protected DirectMessageAdapterClass mDirectMessageAdapterClass;
    protected ListView listView;
    protected List_Menu menu;
    protected long[] ListIDs;
    protected Spinner spinner;
    protected MyUserStreamAdapter mMyUserStreamAdapter;
    protected ListPositionVariable listPosition;
    protected SharedPreferences sharedPreferences;
    protected AsyncTwitter asyncTwitter;

    /**
     * twitterに関する処理の詰め合わせ
     * ついでにDB関連も
     * @param context 必須
     * @param madapter 不要ならnull可
     * @param lv ListView不要ならnull可
     * @param invokerName 呼びだし元の名称（TL、Mention UserTimeLineList）
     * @param spinnerArgument スピナー不要ならnull可
     */
    public TwitterActionClass(Context context, TweetAdapter madapter, ListView lv , String invokerName, Spinner spinnerArgument){
        //初期化
        activity = context;
        mAdapter = madapter;
        listView = lv;
        menu = new List_Menu();
        spinner = spinnerArgument;
        listPosition = new ListPositionVariable();
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(activity);
        //アカウント情報を読み込む
        SharedPreferences accountIDCount = activity.getSharedPreferences(activity.getString(R.string.SelectAccount), 0);
        SharedPreferences spOauth = activity.getSharedPreferences(activity.getString(R.string.PREF_NAME) + accountIDCount.getLong(activity.getString(R.string.SelectAccountNum), 0), Context.MODE_PRIVATE);

        //一般設定
        ConfigurationBuilder builder = new ConfigurationBuilder();
        {
            // Twitter4Jに対してOAuth情報を設定
            // アプリ固有の情報
            builder.setOAuthConsumerKey(OAuthVariable.twitterConsumerKey);
            builder.setOAuthConsumerSecret(OAuthVariable.twitterConsumerSecret);
            // アプリ＋ユーザー固有の情報
            builder.setOAuthAccessToken(spOauth.getString(activity.getString(R.string.TOKEN), null));
            builder.setOAuthAccessTokenSecret(spOauth.getString(activity.getString(R.string.TOKEN_SECRET), null));
            //HTTPタイムアウト設定(ミリ秒)
            builder.setHttpConnectionTimeout(10000);
            builder.setJSONStoreEnabled(true);
            //ストリーミング時にリプも表示するかどうか？
            if(sharedPreferences.getBoolean("Streeming_FF_Mention", false)) {
                builder.setUserStreamRepliesAllEnabled(true);
            }else{
                builder.setUserStreamRepliesAllEnabled(false);
            }
        }
        Configuration conf = builder.build();
        //TwitterFactoryをインスタンス化する
        TwitterFactory twitterFactory = new TwitterFactory(conf);
        //Twitterをインスタンス化する
        mTwitter = twitterFactory.getInstance();

        //非同期メソッド用のインスタンス
        AsyncTwitterFactory factory = new AsyncTwitterFactory(conf);
        asyncTwitter = factory.getInstance();
        createTwitterListener();

        if(invokerName.equals("TL") && CoreVariable.twitterStream == null) {
            TwitterStreamFactory twitterStreamFactory = new TwitterStreamFactory(conf);
            //TwitterStream をインスタンス化する
            CoreVariable.twitterStream = twitterStreamFactory.getInstance();
            mMyUserStreamAdapter = new MyUserStreamAdapter();
            //TwitterStream に UserStreamListener を実装したインスタンスを設定する
            CoreVariable.twitterStream.addListener(mMyUserStreamAdapter);
        }
        if(listView != null) {
            listViewClickAction(invokerName);
        }
    }

    /**
     * DM用のインスタンス作成メソッド
     * @param context
     * @param madapter
     * @param lv
     * @param invokerName
     * @param spinnerArgument
     */
    public TwitterActionClass(Context context, DirectMessageAdapterClass madapter, ListView lv , String invokerName, Spinner spinnerArgument){
        //初期化
        activity = context;
        mDirectMessageAdapterClass = madapter;
        listView = lv;
        menu = new List_Menu();
        spinner = spinnerArgument;
        listPosition = new ListPositionVariable();
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(activity);
        //アカウント情報を読み込む
        SharedPreferences accountIDCount = activity.getSharedPreferences(activity.getString(R.string.SelectAccount), 0);
        SharedPreferences spOauth = activity.getSharedPreferences(activity.getString(R.string.PREF_NAME) + accountIDCount.getLong(activity.getString(R.string.SelectAccountNum), 0), Context.MODE_PRIVATE);

        //一般設定
        ConfigurationBuilder builder = new ConfigurationBuilder();
        {
            // Twitter4Jに対してOAuth情報を設定
            // アプリ固有の情報
            builder.setOAuthConsumerKey(OAuthVariable.twitterConsumerKey);
            builder.setOAuthConsumerSecret(OAuthVariable.twitterConsumerSecret);
            // アプリ＋ユーザー固有の情報
            builder.setOAuthAccessToken(spOauth.getString(activity.getString(R.string.TOKEN), null));
            builder.setOAuthAccessTokenSecret(spOauth.getString(activity.getString(R.string.TOKEN_SECRET), null));
            //HTTPタイムアウト設定(ミリ秒)
            builder.setHttpConnectionTimeout(10000);
            builder.setJSONStoreEnabled(true);
        }
        Configuration conf = builder.build();
        //TwitterFactoryをインスタンス化する
        TwitterFactory twitterFactory = new TwitterFactory(conf);
        //Twitterをインスタンス化する
        mTwitter = twitterFactory.getInstance();

        //非同期メソッド用のインスタンス
        AsyncTwitterFactory factory = new AsyncTwitterFactory(conf);
        asyncTwitter = factory.getInstance();
        createTwitterListener();

        if(listView != null) {
            listViewClickAction(invokerName);
        }
    }

    /**
     * メインアダプタでのインスタンス作成用
     * @param context
     */
    public TwitterActionClass(Context context){
        //初期化
        activity = context;
        menu = new List_Menu();
        listPosition = new ListPositionVariable();
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(activity);
        //アカウント情報を読み込む
        SharedPreferences accountIDCount = activity.getSharedPreferences(activity.getString(R.string.SelectAccount), 0);
        SharedPreferences spOauth = activity.getSharedPreferences(activity.getString(R.string.PREF_NAME) + accountIDCount.getLong(activity.getString(R.string.SelectAccountNum), 0), Context.MODE_PRIVATE);

        //一般設定
        ConfigurationBuilder builder = new ConfigurationBuilder();
        {
            // Twitter4Jに対してOAuth情報を設定
            // アプリ固有の情報
            builder.setOAuthConsumerKey(OAuthVariable.twitterConsumerKey);
            builder.setOAuthConsumerSecret(OAuthVariable.twitterConsumerSecret);
            // アプリ＋ユーザー固有の情報
            builder.setOAuthAccessToken(spOauth.getString(activity.getString(R.string.TOKEN), null));
            builder.setOAuthAccessTokenSecret(spOauth.getString(activity.getString(R.string.TOKEN_SECRET), null));
            //HTTPタイムアウト設定(ミリ秒)
            builder.setHttpConnectionTimeout(10000);
            builder.setJSONStoreEnabled(true);
            //ストリーミング時にリプも表示するかどうか？
            if(sharedPreferences.getBoolean("Streeming_FF_Mention", false)) {
                builder.setUserStreamRepliesAllEnabled(true);
            }else{
                builder.setUserStreamRepliesAllEnabled(false);
            }
        }
        Configuration conf = builder.build();
        //TwitterFactoryをインスタンス化する
        TwitterFactory twitterFactory = new TwitterFactory(conf);
        //Twitterをインスタンス化する
        mTwitter = twitterFactory.getInstance();

        //非同期メソッド用のインスタンス
        AsyncTwitterFactory factory = new AsyncTwitterFactory(conf);
        asyncTwitter = factory.getInstance();
        createTwitterListener();
    }

    /**
     * 自身のIDとか取得
     */
    public void getMyID() {
        AsyncTask<Void, Void, User> task = new AsyncTask<Void, Void, User>() {
            @Override
            protected User doInBackground(Void... params) {
                try {
                    return mTwitter.verifyCredentials();//Userオブジェクトを作成
                } catch (TwitterException e){
                    e.printStackTrace();
                } catch(Exception e)
                {
                    e.printStackTrace();
                }
                return null;
            }

            @Override
            protected void onPostExecute(User result) {
                if(result != null){
                    CoreVariable.userID = result.getId();
                    CoreVariable.userName = result.getScreenName();
                }
            }
        };
        task.execute();
    }

    /**
     * 公式からミュートリストを受け取る
     */
    public void getMyMuteList() {
        AsyncTask<Void, Void, IDs> task = new AsyncTask<Void, Void, IDs>() {
            @Override
            protected IDs doInBackground(Void... params) {
                try {
                    return mTwitter.getMutesIDs(-1);
                } catch (TwitterException e){
                    e.printStackTrace();
                } catch(Exception e)
                {
                    e.printStackTrace();
                }
                return null;
            }

            @Override
            protected void onPostExecute(IDs result) {
                if(result != null){
                    CoreVariable.muteList = null;
                    CoreVariable.muteList = new long[result.getIDs().length];
                    CoreVariable.muteList = result.getIDs();
                }else {
                    CoreVariable.muteList = null;
                    CoreVariable.muteList = new long[1];
                    CoreVariable.muteList[0] = 0;
                }
            }
        };
        task.execute();
    }

    /**
     * ミュート追加
     */
    public void createMute(final long userID){
        AsyncTask<Void, Void, User> task = new AsyncTask<Void, Void, User>() {
            @Override
            protected User doInBackground(Void... params) {
                try {
                    return mTwitter.createMute(userID);
                } catch (TwitterException e){
                    e.printStackTrace();
                } catch(Exception e)
                {
                    e.printStackTrace();
                }
                return null;
            }

            @Override
            protected void onPostExecute(User result) {
                if(result != null){
                    CoreActivity.showToast("ミュートしました");
                    getMyMuteList();
                }else{
                    CoreActivity.showToast("ミュートに失敗しました");
                }
            }
        };
        task.execute();
    }

    /**
     * タイムライン非同期処理
     */
    public void getTimeLine(final Long ID) {
        AsyncTask<Void, Void, ResponseList<twitter4j.Status>> task = new AsyncTask<Void, Void, ResponseList<twitter4j.Status>>() {
            @Override
            protected ResponseList<twitter4j.Status> doInBackground(Void... params) {
                try {
                    getListViewPosition();
                    Paging paging = new Paging();
                    paging.count(Integer.parseInt(sharedPreferences.getString("Load_Tweet", "20")));
                    if(ID != null) {
                        paging.setMaxId(ID);
                    }
                    return mTwitter.getHomeTimeline(paging);
                } catch (TwitterException e) {
                    e.printStackTrace();
                    return null;
                }
            }

            @Override
            protected void onPostExecute(ResponseList<twitter4j.Status> result) {
                CoreActivity.progresStop();
                if(result != null){
                    setItemtoAdapter(result,ID);
                    setRateLimitStaatus(CoreVariable.HomeTimeline, result.getRateLimitStatus());
                }
            }
        };
        task.execute();
    }

    /**
     * Mention取得メソッド
     */
    public void getMention(final Long ID) {
        AsyncTask<Void, Void, ResponseList<twitter4j.Status>> task = new AsyncTask<Void, Void, ResponseList<Status>>() {
            @Override
            protected ResponseList<twitter4j.Status> doInBackground(Void... params) {
                try {
                    getListViewPosition();
                    Paging paging = new Paging();
                    if(ID != null) {
                        paging.setMaxId(ID);
                    }
                    paging.count(Integer.parseInt(sharedPreferences.getString("Load_Tweet", "20")));
                    return mTwitter.getMentionsTimeline(paging);
                } catch (TwitterException e) {
                    e.printStackTrace();
                    return null;
                }
            }

            @Override
            protected void onPostExecute(ResponseList<twitter4j.Status> result) {
                CoreActivity.progresStop();
                if(result != null){
                    setItemtoAdapter(result,ID);
                    setRateLimitStaatus(CoreVariable.MentionsTimelineLimit, result.getRateLimitStatus());
                }
            }
        };
        task.execute();
    }

    /**
     * リストTLの更新メソッド
     * @param ID
     */
    public void getListTimeLine(final Long ID, final int ListID) {

        AsyncTask<Void, Void, ResponseList<twitter4j.Status>> task = new AsyncTask<Void, Void, ResponseList<twitter4j.Status>>() {
            long timeLineID = ListIDs[ListID];
            @Override
            protected ResponseList<twitter4j.Status> doInBackground(Void... params) {
                try {
                    getListViewPosition();
                    Paging paging = new Paging();
                    if(ID != null) {
                        paging.maxId(ID);
                    }
                    paging.count(Integer.parseInt(sharedPreferences.getString("Load_Tweet", "20")));
                    return mTwitter.getUserListStatuses(timeLineID,paging);
                } catch (TwitterException e) {
                    e.printStackTrace();
                    return null;
                }
            }

            @Override
            protected void onPostExecute(ResponseList<twitter4j.Status> result) {
                CoreActivity.progresStop();
                if(result != null){
                    setItemtoAdapter(result,ID);
                    setRateLimitStaatus(CoreVariable.UserListStatusesLimit, result.getRateLimitStatus());
                }
            }
        };
        task.execute();
    }

    /**
     * ユーザーTLの表示
     */
    public  void getUserTimeLine(final Long userID, final Long tweetID){
        AsyncTask<Void, Void, List<twitter4j.Status>> task = new AsyncTask<Void, Void, List<twitter4j.Status>>() {
            @Override
            protected List<twitter4j.Status> doInBackground(Void... params) {
                try {
                    getListViewPosition();
                    Paging p = new Paging();
                    p.count(Integer.parseInt(sharedPreferences.getString("Load_Tweet", "20")));
                    if(tweetID != null){
                        p.maxId(tweetID);
                    }
                    return mTwitter.getUserTimeline(userID,p);
                } catch (TwitterException e) {
                    e.printStackTrace();
                    return null;
                }
            }

            @Override
            protected void onPostExecute(List<twitter4j.Status> result) {
                CoreActivity.progresStop();
                if(result != null){
                    setItemtoAdapter(result,tweetID);
                }
            }
        };
        task.execute();
    }

    /**
     * DirectMessageの取得メソッド
     * */
    public void getDirectMessage(final Long ID){
        AsyncTask<Void, Void, ResponseList<DirectMessage>> task = new AsyncTask<Void, Void, ResponseList<twitter4j.DirectMessage>>() {
            @Override
            protected ResponseList<DirectMessage> doInBackground(Void... params) {
                try {
                    getListViewPosition();
                    Paging p = new Paging();
                    p.count(Integer.parseInt(sharedPreferences.getString("Load_Tweet", "20")));
                    if(ID != null) {
                        p.setMaxId(ID);
                    }
                    return mTwitter.getDirectMessages(p);
                } catch (TwitterException e) {
                    e.printStackTrace();
                    return null;
                }
            }
            @Override
            protected void onPostExecute(ResponseList<twitter4j.DirectMessage> result) {
                CoreActivity.progresStop();
                if(result != null){
                    setMessegetoAdapter(result, ID);
                    setRateLimitStaatus(CoreVariable.DirectMessageLimit, result.getRateLimitStatus());
                }
            }
        };
        task.execute();
    }

    /**
     * 指定テキストをTwitterから検索する
     * @param searchText
     */
    public void searchToTweet(final String searchText, final Long ID){
        AsyncTask<Void, Void, twitter4j.QueryResult> task = new AsyncTask<Void, Void, twitter4j.QueryResult>() {
            @Override
            protected twitter4j.QueryResult doInBackground(Void... params) {
                try {
                    getListViewPosition();
                    Query query = new Query();
                    query.count(Integer.parseInt(sharedPreferences.getString("Load_Tweet", "20")));
                    query.setQuery(searchText);
                    if(ID != null) {
                        query.setMaxId(ID);
                    }
                    return mTwitter.search(query);
                } catch (TwitterException e) {
                    e.printStackTrace();
                    return null;
                }
            }

            @Override
            protected void onPostExecute(twitter4j.QueryResult result) {
                CoreActivity.progresStop();
                if(result != null){
                    setItemtoAdapter(result.getTweets(),ID);
                    setRateLimitStaatus(CoreVariable.SearchLimit, result.getRateLimitStatus());
                }
            }
        };
        task.execute();
    }

    /**
     * 受信したツイートをアダプタに条件が合えばセットする
     **/
    protected void setItemtoAdapter(final List<twitter4j.Status> status, final Long ID){
        int count;
        boolean firstLoad = false;
        getListViewPosition();
        if(mAdapter.getCount() == 0){
            count = 0;
            firstLoad = true;
        }else if(ID != null){
            count = mAdapter.getCount() - 1;
        }else{
            count = 1;
        }
        for (final twitter4j.Status tweet : status) {
            if(!isTweetMute(tweet)){
                if(mAdapter.getCount() != 0 && mAdapter.getPosition(tweet) > 0) continue;
                final int finalCount = count;
                final boolean finalFirstLoad = firstLoad;
                new UiHandler(){
                    public void run(){
                        mAdapter.insert(tweet, finalCount);
                        mAdapter.notifyDataSetChanged();
                        if(ID == null) {
                            setListViewPosition(finalFirstLoad ? 0 : finalCount);
                        }
                    }
                }.post();
                count++;
            }
        }
        CoreActivity.progresStop();
    }
    protected void setItemtoAdapter(final twitter4j.Status tweet, final long MaxId){
        if(!isTweetMute(tweet)){
            getListViewPosition();
            if(MaxId > 0){
                new UiHandler(){
                    public void run(){
                        mAdapter.insert(tweet,0);
                        mAdapter.notifyDataSetChanged();
                        if(listPosition.position != 0){
                            setListViewPosition(1);
                        }
                    }
                }.post();
            }else{
                new UiHandler(){
                    public void run(){
                        mAdapter.add(tweet);
                        mAdapter.notifyDataSetChanged();
                    }
                }.post();
            }
        }

        CoreActivity.progresStop();
    }

    /**
     * 受信したDMをアダプタに代入する奴
     * @param messages
     * @param MaxId
     */
    protected void setMessegetoAdapter(final List<twitter4j.DirectMessage> messages, final Long MaxId){
                int count;
                if(mDirectMessageAdapterClass.getCount() == 0){
                    count = 0;
                }else if(MaxId != null){
                    count = mDirectMessageAdapterClass.getCount() - 1;
                }else{
                    count = 1;
                }
                for (final twitter4j.DirectMessage message : messages) {
                    if(mDirectMessageAdapterClass.getCount() != 0 && mDirectMessageAdapterClass.getPosition(message) > 0 ) continue;
                    final int finalCount = count;
                    new UiHandler() {
                        public void run() {
                            mDirectMessageAdapterClass.insert(message, finalCount);
                            mDirectMessageAdapterClass.notifyDataSetChanged();
                        }
                    }.post();
                    count++;
                }

                CoreActivity.progresStop();
    }

    /**
     * ツイートがミュート対象か
     * @param tweet
     * @return
     */
    protected boolean isTweetMute(twitter4j.Status tweet){
        if(CoreVariable.muteList == null) return false;

        for(long ID : CoreVariable.muteList)
        {
            if((tweet.getUser().getId() == ID) && (sharedPreferences.getBoolean("mute_flag", false)))
            {
                return true;
            }
        }
        return false;
    }

    /**
     * ツイート投稿メソッド
     */
    public void sendTweet(final String sendText, final String path[]) {
        AsyncTask<Void, Void, Boolean> task = new AsyncTask<Void, Void, Boolean>() {
            String tmp = null;
            @Override
            protected Boolean doInBackground(Void... params) {
                try {
                    if(path[0] == null && path[1] == null && path[2] == null && path[3] == null)
                    {
                        mTwitter.updateStatus(sendText);
                        return true;
                    }
                    StatusUpdate update = new StatusUpdate(sendText);
                    int mediacount = 0;
                    for(int i = 0; i < path.length; i++){
                        if(path[i] != null){
                            mediacount++;
                        }
                    }
                    long media[] = new long[mediacount];
                    mediacount = 0;
                    for(int i=0;i< path.length; i++){
                        if(path[i] != null){
                            media[mediacount] = mTwitter.uploadMedia(new File(path[i])).getMediaId();
                            mediacount++;
                        }
                    }
                    update.setMediaIds(media);
                    mTwitter.updateStatus(update);
                    return true;
                } catch (TwitterException e) {
                    e.printStackTrace();
                    tmp = e.getMessage();
                    return false;
                }
            }

            @Override
            protected void onPostExecute(Boolean result) {
                if (result) {
                    CoreActivity.showToast("ツイートが完了しました！");
                } else {
                    CoreActivity.showToast("投稿に失敗しました・・・\n" + tmp);
                }
            }
        };
        task.execute();
    }

    /**
     * リプライ投稿メソッド
     */
    public void sendMention(final String sendText, final String path[], final long mentionID) {

        long men = mentionID;
        AsyncTask<Void, Void, Boolean> task = new AsyncTask<Void, Void, Boolean>() {
            String tmp = null;
            @Override
            protected Boolean doInBackground(Void... params) {
                try {
                    StatusUpdate update = new StatusUpdate(sendText);
                    if(path[0] == null && path[1] == null && path[2] == null && path[3] == null) {
                        update.setInReplyToStatusId(mentionID);
                        mTwitter.updateStatus(update);
                        return true;
                    }
                    int mediacount = 0;
                    for(int i = 0; i < path.length; i++){
                        if(path[i] != null){
                            mediacount++;
                        }
                    }
                    long media[] = new long[mediacount];
                    mediacount = 0;
                    for(int i=0;i< path.length; i++){
                        if(path[i] != null){
                            media[mediacount] = mTwitter.uploadMedia(new File(path[i])).getMediaId();
                            mediacount++;
                        }
                    }
                    update.setMediaIds(media);
                    update.setInReplyToStatusId(mentionID);
                    mTwitter.updateStatus(update);
                    return true;
                } catch (TwitterException e) {
                    e.printStackTrace();
                    tmp = e.getMessage();
                    return false;
                }
            }

            @Override
            protected void onPostExecute(Boolean result) {
                if (result) {
                    CoreActivity.showToast("リプライが完了しました！");
                } else {
                    CoreActivity.showToast("投稿に失敗しました・・・");
                }
            }
        };
        task.execute();
    }

    /**
     * DM投稿メソッド
     */
    public void sendDirectMessage(final String sendText, final long mentionID) {
        AsyncTask<Void, Void, Boolean> task = new AsyncTask<Void, Void, Boolean>() {
            String tmp = null;
            @Override
            protected Boolean doInBackground(Void... params) {
                try {
                    mTwitter.sendDirectMessage(mentionID, sendText);
                    return true;
                } catch (TwitterException e) {
                    e.printStackTrace();
                    tmp = e.getMessage();
                    return false;
                }
            }

            @Override
            protected void onPostExecute(Boolean result) {
                if (result) {
                    CoreActivity.showToast("DM送信完了しました！");
                } else {
                    CoreActivity.showToast("送信に失敗しました・・・");
                }
            }
        };
        task.execute();
    }

    /**
     * ListViewクリック時の動作設定メソッド
     */
    protected void listViewClickAction(final String invoker) {
        final SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(activity);
        /**
         * 通常押し
         */
        if(!invoker.equals("DM")) {
            if(mAdapter == null)return;
            if (invoker.equals("TL") && CoreVariable.isTLMenu)return;
            if (invoker.equals("Mention") && CoreVariable.isMentionMenu)return;
            if (invoker.equals("UserTimeLineList") && CoreVariable.isListMenu)return;
            if (invoker.equals("searchTab") && CoreVariable.isSerchMenu)return;
            mAdapter.clickObservable
                    .subscribe(position -> {
                        if (position != 0 && mAdapter.getItem(position) == null) {
                            //フッターがクリックされた
                            CoreActivity.progresRun();
                            if (invoker.equals("TL")) {
                                getTimeLine(mAdapter.getItem(position - 1).getId());
                            } else if (invoker.equals("Mention")) {
                                getMention(mAdapter.getItem(position - 1).getId());
                            } else if (invoker.equals("UserTimeLineList")) {
                                getListTimeLine(mAdapter.getItem(position - 1).getId(), spinner.getSelectedItemPosition());
                            }
                        } else if (position == 0 && mAdapter.getItem(position) == null) {
                            //ヘッダーがクリックされた
                            CoreActivity.progresRun();
                            if (invoker.equals("TL")) {
                                if(sp.getBoolean("Streeming_stok", false) == false) {
                                    CoreActivity.progresRun();
                                    getTimeLine(null);
                                }else{
                                    for(Status tweet : CoreVariable.stockTweet){
                                        mAdapter.insert(tweet,1);
                                    }
                                }
                            } else if (invoker.equals("Mention")) {
                                getMention(null);
                            } else if (invoker.equals("UserTimeLineList")) {
                                getListTimeLine(null, spinner.getSelectedItemPosition());
                            }
                        }
                        if(sp.getBoolean("Tap_Setting", true)) return;
                        menu.Tweet_Menu(activity, mAdapter.getItem(position));

                    });
        }else{
            if(mDirectMessageAdapterClass == null)return;
            if (CoreVariable.isDmMenu)return;
            mDirectMessageAdapterClass.clickObservable
                    .subscribe(position -> {
                        if (position != 0 && mDirectMessageAdapterClass.getItem(position) == null) {
                            //フッターがクリックされた
                            CoreActivity.progresRun();
                            getDirectMessage(mDirectMessageAdapterClass.getItem(position - 1).getId());
                        } else if (position == 0 && mDirectMessageAdapterClass.getItem(position) == null) {
                            //ヘッダーがクリックされた
                            CoreActivity.progresRun();
                            getDirectMessage(null);
                        }
                        if(sp.getBoolean("Tap_Setting", true)) return;
                        menu.DM_Menu(activity, mDirectMessageAdapterClass.getItem(position));
                    });
        }

        /**
         * 長押し
         */
        if(!invoker.equals("DM")) {
            if(mAdapter == null)return;
            if (invoker.equals("TL") && CoreVariable.isTLMenu)return;
            if (invoker.equals("Mention") && CoreVariable.isMentionMenu)return;
            if (invoker.equals("UserTimeLineList") && CoreVariable.isListMenu)return;
            if (invoker.equals("searchTab") && CoreVariable.isSerchMenu)return;
            mAdapter.longClickObservable
                    .filter(position -> (mAdapter.getItem(position) != null && sp.getBoolean("Tap_Setting", true)))
                    .subscribe(position -> {
                        menu.Tweet_Menu(activity, mAdapter.getItem(position));
                    });
        }else{
            if(mDirectMessageAdapterClass == null)return;
            if (CoreVariable.isDmMenu)return;
            mDirectMessageAdapterClass.longClickObservable
                    .filter(position -> (mDirectMessageAdapterClass.getItem(position) != null && sp.getBoolean("Tap_Setting", true)))
                    .subscribe(position -> {
                        menu.DM_Menu(activity, mDirectMessageAdapterClass.getItem(position));
                    });
        }
    }

    /**
     * RTをするメソッド
     * @param ID
     */
    public void RTPost(final long ID){
        AsyncTask<Void, Void,  Boolean> task = new AsyncTask<Void, Void, Boolean>() {
            //処理をここに書く
            @Override
            protected Boolean doInBackground(Void... params) {
                try{
                    mTwitter.retweetStatus(ID);
                    return true;

                } catch (TwitterException e){
                    e.printStackTrace();
                } catch(Exception e)
                {
                    e.printStackTrace();
                }
                return false;
            }
            //処理が終わった後の処理
            @Override
            protected void onPostExecute(Boolean result) {
                if (result != false) {
                    CoreActivity.showToast("リツイートしました。");
                } else {
                    CoreActivity.showToast("リツイートに失敗しました。。。");
                }
            }
        };
        task.execute();
    }

    /**
     * お気に入り登録するメソッド
     * @param ID
     */
    public void FaPost(final long ID)	{
        AsyncTask<Void, Void,  Boolean> task = new AsyncTask<Void, Void, Boolean>() {
            //処理をここに書く
            @Override
            protected Boolean doInBackground(Void... params) {
                try{
                    mTwitter.createFavorite(ID);
                    return true;
                } catch (TwitterException e){
                    e.printStackTrace();
                } catch(Exception e)
                {
                    e.printStackTrace();
                }
                return false;
            }
            //処理が終わった後の処理
            @Override
            protected void onPostExecute(Boolean result) {
                if (result != false) {
                    CoreActivity.showToast("ふぁぼしました。");
                } else {
                    CoreActivity.showToast("ふぁぼ失敗・・・");
                }
            }
        };
        task.execute();
    }

    /**
     * フォローするメソッド
     * @param userID 相手のID
     */
    public void userFollow(final long userID) {
        final AsyncTask<Void, Void, User> task = new AsyncTask<Void, Void, User>() {

            @Override
            protected User doInBackground(Void... params) {
                try {
                    return mTwitter.createFriendship(userID);
                } catch (TwitterException e) {
                    e.printStackTrace();
                    return null;
                }
            }

            @Override
            protected void onPostExecute(User resurt) {
                if (resurt != null) {
                    CoreActivity.showToast("フォロー完了");
                } else {
                    CoreActivity.showToast("フォロー失敗");
                }
            }
        };
        task.execute();
    }

    /**
     * リムーブするメソッド
     * @param userID 相手のID
     */
    public void userUnduFollow(final long userID){
        final AsyncTask<Void, Void, User> task = new AsyncTask<Void, Void, User>() {

            @Override
            protected User doInBackground(Void... params) {
                try {
                    return mTwitter.destroyFriendship(userID);
                } catch (TwitterException e) {
                    e.printStackTrace();
                    return null;
                }
            }

            @Override
            protected void onPostExecute(User resurt) {
                if(resurt != null){
                    CoreActivity.showToast("リムーブ完了");
                }else{
                    CoreActivity.showToast("リムーブ失敗");
                }
            }
        };
        task.execute();
    }

    /**
     * List一覧を取得するメソッド
     */
    public void getUserList(final ArrayAdapter adapter) {
        AsyncTask<Void, Void, ResponseList<UserList>> task = new AsyncTask<Void, Void, ResponseList<UserList>>() {

            @Override
            protected ResponseList<UserList> doInBackground(Void... params) {
                try {
                    if(CoreVariable.userLists == null) {
                        SharedPreferences ScreanNames = activity.getSharedPreferences("ScreanNames", 0);
                        SharedPreferences accountIDCount = activity.getSharedPreferences(activity.getString(R.string.SelectAccount), 0);
                        long id = accountIDCount.getLong(activity.getString(R.string.SelectAccountNum), 0);
                        StringBuilder sb = new StringBuilder();
                        sb.append("ScreanName");
                        sb.append(id);
                        String str = new String(sb);
                        String name = ScreanNames.getString(str, "");
                        return mTwitter.getUserLists(name);
                    }else{
                        return CoreVariable.userLists;
                    }
                } catch (TwitterException e) {
                    e.printStackTrace();
                }
                return null;
            }

            @Override
            protected void onPostExecute(ResponseList<UserList> result) {
                if (result != null) {
                    CoreVariable.userLists = result;
                    ListIDs = new long[result.size()];
                    int i = 0;
                    for (UserList List : result) {
                        adapter.add(List.getName());
                        ListIDs[i] = List.getId();
                        i++;
                    }
                    adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    spinner.setAdapter(adapter);
                }
            }
        };
        task.execute();
    }

    /**
     * limit一覧をダイアログに出す
     */
    public void debugMode(){

        final Twitter mTwitter;
        SharedPreferences accountIDCount = activity.getSharedPreferences(activity.getString(R.string.SelectAccount), 0);
        mTwitter = TwitterUtils.getTwitterInstance(activity, accountIDCount.getLong(activity.getString(R.string.SelectAccountNum), 0));
        //非同期
        AsyncTask<Void, Void, Map<String, RateLimitStatus>> task = new AsyncTask<Void, Void, Map<String, RateLimitStatus>>() {

            @Override
            protected Map<String, RateLimitStatus> doInBackground(Void... params) {
                try {
                    return mTwitter.getRateLimitStatus();
                } catch (TwitterException e) {
                    // TODO 自動生成された catch ブロック
                    e.printStackTrace();
                };
                return null;
            }

            @Override
            protected void onPostExecute(Map<String, RateLimitStatus> result) {
                if(result != null)
                {
                    StringBuilder sb = new StringBuilder();
                    for (String endpoint : result.keySet()) {
                        RateLimitStatus rateStatus = result.get(endpoint);
                        sb.append(endpoint + "\n");
                        sb.append("API:" + rateStatus.getRemaining() + "\n");
                        sb.append("ResetSecond:" + rateStatus.getSecondsUntilReset() + "\n");
                        sb.append("Time:" + rateStatus.getResetTimeInSeconds() + "\n\n");
                    }
                    String str = new String(sb);
                    showDialog("LimitAll", str);
                }
            }

        };
        task.execute();
    }

    /**
     * UserStream受信時にこのクラスのメソッドが呼ばれる
     */
    protected class MyUserStreamAdapter extends UserStreamAdapter {
        /**
         * 新着ツイート受信時
         * @param status 受信したツイート内容等
         */
        @Override
        public void onStatus(final Status status) {
            super.onStatus(status);

            if(sharedPreferences.getBoolean("Streeming_stok", false) == false) {
                setItemtoAdapter(status, 1);
            }else{
                CoreVariable.stockTweet.add(status);
                if(sharedPreferences.getBoolean("Streeming_Nof_Tost", false)) {
                    CoreActivity.showToast("新しいツイートがあるみたい");
                }
            }
            //自分宛てのツイートか
            boolean RepNotifFlag = false;
            boolean ReoNotifNotFlag = false;
            for (UserMentionEntity UrlLink : status.getUserMentionEntities()) {
                if(UrlLink.getScreenName().equals(CoreVariable.userName))
                {
                    RepNotifFlag = true;
                    if(status.getUser().getScreenName().equals(CoreVariable.userName))
                    {
                        ReoNotifNotFlag = true;
                    }
                }
            }
            if(RepNotifFlag && !ReoNotifNotFlag && sharedPreferences.getBoolean("NotificationMen", false)){
                NotificationManager mNotificationManager = (NotificationManager) activity.getSystemService(Context.NOTIFICATION_SERVICE);
                mNotificationManager.cancelAll();
                CoreActivity.sendRepNotification("新しいリプライがあります。");
            }
            if(status.getRetweetedStatus().getUser().getId() == CoreVariable.userID && sharedPreferences.getBoolean("NotificationRT", false)){
                NotificationManager mNotificationManager = (NotificationManager) activity.getSystemService(Context.NOTIFICATION_SERVICE);
                mNotificationManager.cancelAll();
                CoreActivity.sendRepNotification("リツイートされました。");
            }
        }

        /**
         * フォロー通知メソッド
         * @param source
         * @param followedUser
         */
        @Override
        public void onFollow(User source, User followedUser) {
            super.onFollow(source, followedUser);
            if(sharedPreferences.getBoolean("NotificationFol", false)){
                NotificationManager mNotificationManager = (NotificationManager) activity.getSystemService(Context.NOTIFICATION_SERVICE);
                mNotificationManager.cancelAll();
                CoreActivity.sendRepNotification("@" + source.getScreenName() + " さんにフォローされました。");
            }
        }

        /**
         * DirectMessage受信メソッド
         * @param directMessage
         */
        @Override
        public void onDirectMessage(DirectMessage directMessage) {
            super.onDirectMessage(directMessage);
            if(sharedPreferences.getBoolean("NotificationDM", false)){
                NotificationManager mNotificationManager = (NotificationManager) activity.getSystemService(Context.NOTIFICATION_SERVICE);
                mNotificationManager.cancelAll();
                CoreActivity.sendRepNotification("@" + directMessage.getSenderScreenName() + " さんからのDMがあります。");
            }
        }

        /**
         * ツイートが削除された時
         * @param sdn
         */
        @Override
        public void onDeletionNotice(StatusDeletionNotice sdn) {
            CoreVariable.deleteTweet.add(sdn.getStatusId());
        }


    }

    /**
     * 指定されたツイートIDのツイートを取得する
     */
    public void getStatus(final long ID){
        AsyncTask<Void, Void,  Status> task = new AsyncTask<Void, Void, Status>() {
            //処理をここに書く
            @Override
            protected twitter4j.Status doInBackground(Void... params) {
                try{
                    return mTwitter.showStatus(ID);
                } catch (TwitterException e){
                    e.printStackTrace();
                } catch(Exception e)
                {
                    e.printStackTrace();
                }
                return null;
            }
            //処理が終わった後の処理
            @Override
            protected void onPostExecute(twitter4j.Status result) {
                if(result != null){
                    mAdapter.add(result);
                }
            }
        };
        task.execute();
    }

    /**
     * 会話取得非同期メソッド
     * @param ID
     */
    public void getConversation(final long ID){
        if(ID == -1) return;
        asyncTwitter.showStatus(ID);
    }

    /**
     * リストビューの位置を取得し返す
     */
    protected void getListViewPosition(){
        try {
            listPosition.position = listView.getFirstVisiblePosition();
            listPosition.y = listView.getChildAt(0).getTop();
        }catch (Exception e){
            listPosition.position = 0;
            listPosition.y = 0;
        }
    }

    /**
     * リストビューの位置をセットする
     * @param movePosition ずらしたい項目数
     */
    protected void setListViewPosition(final int movePosition){
        listView.setSelectionFromTop(listPosition.position + movePosition, listPosition.y);
    }

    /**
     * ダイアログを出す
     * @param text
     */
    protected void showDialog(String titleText, String text) {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(activity);
        alertDialog.setTitle(titleText);      //タイトル設定
        alertDialog.setMessage(text);  //内容(メッセージ)設定

        // OK(肯定的な)ボタンの設定
        alertDialog.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                // OKボタン押下時の処理
            }
        });
        alertDialog.show();
    }

    /**
     * Twitterインスタンスを返す
     * @return
     */
    public Twitter getTwitter(){
        return mTwitter;
    }

    /**
     * プロフィール取得
     * @param Userid
     * @return
     */
    public User show_Prof(long Userid){
        try {
            return mTwitter.showUser(Userid);
        } catch (TwitterException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * フォロー済みかチェック
     * @param user
     * @param b
     */
    public void chackFollow(final User user, final Button b){
        final long cursor = -1L;

        final AsyncTask<Void, Void, IDs> task = new AsyncTask<Void, Void, IDs>() {
            @Override
            protected IDs doInBackground(Void... params) {
                try {
                    return mTwitter.getFollowersIDs(cursor);
                } catch (TwitterException e) {
                    e.printStackTrace();
                    return null;
                }
            }

            @Override
            protected void onPostExecute(IDs ids) {
                if(ids != null){
                    Boolean machID = false;
                    Boolean myID = false;
                    for(long ID :ids.getIDs()){
                        if(ID == user.getId()){
                            machID = true;
                        }else if(CoreVariable.userID == user.getId()){
                            myID = true;
                        }
                    }
                    if(machID) {
                        b.setText("フォロー済み");
                    }else if(myID){
                        b.setText("あなただよ");
                    }else{
                        b.setText("フォロー");
                    }
                }
            }
        };
        task.execute();
    }

    /**
     * asyncTwitter用のリスナー作成と格納メソッド
     */
    public void createTwitterListener(){
        TwitterListener listener = new TwitterAdapter() {
            @Override
            public void gotShowStatus(final Status status) {
                super.gotShowStatus(status);
                if(status == null) return;
                new UiHandler() {
                    public void run() {
                        mAdapter.add(status);
                        mAdapter.notifyDataSetChanged();
                    }
                }.post();
                if(status.getInReplyToStatusId() == -1) return;
                asyncTwitter.showStatus(status.getInReplyToStatusId());
            }
        };

        asyncTwitter.addListener(listener);
    }

    /**
     * Streaming開始
     */
    public void startStreaming() {
        //CoreActivity.twitterStreamがnull（未定義）でなくMainActivity.runStreamがfalseなら
        if(CoreVariable.twitterStream != null && CoreVariable.runStream == false) {
            //TwitterStream#user() を呼び出し、ユーザーストリームを開始する
            CoreVariable.twitterStream.user();
            CoreVariable.runStream = true;  //ストリーミング実行中フラグを立てる
            Log.d("StreamingStatus", "StartStreaming");
        }
    }

    /**
     * Streaming停止
     */
    public void stopStreaming(){
        if(CoreVariable.twitterStream != null &&  CoreVariable.runStream == true) {
            CoreVariable.twitterStream.shutdown();
            CoreVariable.runStream = false;
            Log.d("StreamingStatus", "ShutdownStreaming");
        }
    }

    public void getRateLimitStatus(){
        final Twitter mTwitter;
        SharedPreferences accountIDCount = activity.getSharedPreferences(activity.getString(R.string.SelectAccount), 0);
        mTwitter = TwitterUtils.getTwitterInstance(activity, accountIDCount.getLong(activity.getString(R.string.SelectAccountNum), 0));
        //非同期
        AsyncTask<Void, Void, Map<String, RateLimitStatus>> task = new AsyncTask<Void, Void, Map<String, RateLimitStatus>>() {

            @Override
            protected Map<String, RateLimitStatus> doInBackground(Void... params) {
                try {
                    return mTwitter.getRateLimitStatus();
                } catch (TwitterException e) {
                    // TODO 自動生成された catch ブロック
                    e.printStackTrace();
                };
                return null;
            }

            @Override
            protected void onPostExecute(Map<String, RateLimitStatus> result) {
                if(result == null) return;
                for (String endpoint : result.keySet()) {
                    RateLimitStatus rateStatus = result.get(endpoint);
                    switch (endpoint){
                        case "/statuses/home_timeline":
                            setRateLimitStaatus(CoreVariable.HomeTimeline, rateStatus);
                            break;
                        case "/statuses/mentions_timeline":
                            setRateLimitStaatus(CoreVariable.MentionsTimelineLimit, rateStatus);
                            break;
                        case "/lists/statuses":
                            setRateLimitStaatus(CoreVariable.UserListStatusesLimit, rateStatus);
                            break;
                        case "/direct_messages":
                            setRateLimitStaatus(CoreVariable.DirectMessageLimit, rateStatus);
                            break;
                        case "/search/tweets":
                            setRateLimitStaatus(CoreVariable.SearchLimit, rateStatus);
                            break;
                        default:
                            break;
                    }
                }
            }

        };
        task.execute();
    }

    /**
     * Limit情報を代入する奴
     * @param rateLimitVariable
     * @param rateLimitStatus
     */
    protected void setRateLimitStaatus(RateLimitVariable rateLimitVariable, RateLimitStatus rateLimitStatus){
        //現在の最大回数
        rateLimitVariable.HourlyLimit = rateLimitStatus.getLimit();
        //残りアクセス可能回数
        rateLimitVariable.RemainingHits = rateLimitStatus.getRemaining();
        //リセットされる時間
        rateLimitVariable.ResetTimeInSeconds = rateLimitStatus.getResetTimeInSeconds();
        //リセットされる"までの"秒数
        rateLimitVariable.SecondsUntilReset = rateLimitStatus.getSecondsUntilReset();
    }

    /**
     * ListView戻り値用Class変数
     */
    protected class ListPositionVariable{
        int position;
        int y;
    }
}