package com.meronmks.zimitta.user;

import java.util.ArrayList;
import java.util.List;

import com.meronmks.zimitta.Activity.TwitterOAuthActivity;
import com.meronmks.zimitta.Adapter.TweetAdapter;
import com.meronmks.zimitta.core.MainActivity;
import com.meronmks.zimitta.core.TwitterUtils;
import com.meronmks.zimitta.menu.List_Menu;
import twitter4j.Paging;
import twitter4j.Status;
import twitter4j.Twitter;
import twitter4j.TwitterException;
import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.ListActivity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.PaintDrawable;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

public class UserFavActivity extends ListActivity {

    private TweetAdapter mAdapter;
    private Twitter mTwitter;
    private long OldStatus = 0;
    private long NewStatus = 0;
    private long UserID_Fav = 0;
    private String ScreenName;
    private int listposition = 0;
    private int listposition_y = 0;
    private SharedPreferences sp;
    private boolean NewReloadFulg = true;
    private List<Status> StatusIDs = new ArrayList<Status>();
    private ListView lv;
    //private Boolean TL_load_lock = true;
    private Status Tweet;
    private  SharedPreferences accountIDCount;

    //定数
    public static final String cache = "mention_cache.txt";

    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    @SuppressLint("NewApi")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        sp = PreferenceManager.getDefaultSharedPreferences(this);
        accountIDCount = getSharedPreferences("accountidcount", 0);
        if (!TwitterUtils.hasAccessToken(this, accountIDCount.getLong("ID_Num_Now", 0))) {
            Intent intent = new Intent(this, TwitterOAuthActivity.class);
            startActivity(intent);
            finish();
        }else {
            lv = getListView();
            mAdapter = new TweetAdapter(this);
            setListAdapter(mAdapter);
            mTwitter = TwitterUtils.getTwitterInstance(this,accountIDCount.getLong("ID_Num_Now", 0));
            Intent Intent = getIntent();
            UserID_Fav = Intent.getLongExtra("UserID_Fav", BIND_ABOVE_CLIENT);
            ScreenName = Intent.getStringExtra("ScreenName");
            setTitle(ScreenName + " Fav");
            reloadUserFavTimeLine();
        }
    }

    //Activity動作の一歩前
    @Override
    protected void onResume() {
        super.onResume();
        PaintDrawable paintDrawable = new PaintDrawable(Color.argb(255,0,0,0));
        getWindow().setBackgroundDrawable(paintDrawable);
        mAdapter.notifyDataSetChanged();

        final boolean LongTap = sp.getBoolean("Tap_Setting", true);

        //ListViewのクリックリスナー登録
        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            //通常押し
            @Override
            public void onItemClick(AdapterView<?> parent, View view,int position, long id) {
                if(LongTap == false){
                    try {
                        Tweet = mAdapter.getItem(position);
                        List_Menu list = new List_Menu();
                        list.Tweet_Menu(UserFavActivity.this, Tweet);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                }

                //フッターがクリックされた
                if(position != 0 && mAdapter.getItem(position) == null) {
                    NewReloadFulg = false;
                    StatusIDs.remove(position);
                    reloadUserFavTimeLine();
                }
                //ヘッダーがクリックされた
                if(position == 0 && mAdapter.getItem(position) == null) {
                    NewReloadFulg = true;
                    StatusIDs.remove(0);
                    reloadUserFavTimeLine();
                }
            }

        });

        lv.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            //長押し
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view,int position, long id) {
                if(LongTap == true){
                    try {
                        Tweet = mAdapter.getItem(position);
                        List_Menu list = new List_Menu();
                        list.Tweet_Menu(UserFavActivity.this, Tweet);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                return true;
            }
        });
    }

    //タイムラインの非同期取得
    private void reloadUserFavTimeLine() {
        AsyncTask<Void, Void, List<Status>> task = new AsyncTask<Void, Void, List<Status>>() {
            String exception;
            int position,y;
            @Override
            protected List<twitter4j.Status> doInBackground(Void... params) {
                int i = Integer.parseInt(sp.getString("Load_Tweet", "20"));
                if ((NewStatus == 0) && (NewReloadFulg)){	//ツイートの読み込み数が0で新たに読み込むとき
                    try {
                        Paging p = new Paging();
                        p.count(i);
                        return mTwitter.getFavorites(UserID_Fav,p);
                    } catch (TwitterException e){
                        e.printStackTrace();
                        exception = e.getMessage();
                    } catch(Exception e)
                    {
                        e.printStackTrace();
                        exception = e.getMessage();
                    }
                }else if((NewStatus != 0) && (NewReloadFulg)){	//ツイートの読み込み数が0以上で新たに更新するとき
                    try {
                        listposition = getListView().getFirstVisiblePosition();
                        listposition_y = getListView().getChildAt(0).getTop();
                        Paging p = new Paging();
                        p.setSinceId(NewStatus);
                        p.count(200);
                        return mTwitter.getFavorites(UserID_Fav,p);
                    } catch (TwitterException e){
                        e.printStackTrace();
                        exception = e.getMessage();
                    } catch(Exception e)
                    {
                        e.printStackTrace();
                        exception = e.getMessage();
                    }
                }else if(!NewReloadFulg){	//古いツイートを取得するとき
                    try {
                        Paging p = new Paging();
                        p.setMaxId(OldStatus);
                        p.count(i);
                        return mTwitter.getFavorites(UserID_Fav,p);
                    } catch (TwitterException e){
                        e.printStackTrace();
                        exception = e.getMessage();
                    } catch(Exception e)
                    {
                        e.printStackTrace();
                        exception = e.getMessage();
                    }
                }
                return null;
            }

            @Override
            protected void onPostExecute(List<twitter4j.Status> result) {
                if (result != null) {
                    position = lv.getFirstVisiblePosition();

                    try{
                        y = lv.getChildAt(0).getTop();
                    }catch(Exception e){
                        y = 0;
                    }
                    int count = 0;
                    if(NewReloadFulg)
                    {
                        for (twitter4j.Status status : result) {
                            if(count == 0){
                                StatusIDs.add(0,null);
                                count++;
                            }
                            StatusIDs.add(count ,status);
                            count++;
                        }
                        if(NewStatus != 0){
                            listposition = listposition + count;
                        }
                        getListView().setSelectionFromTop(listposition, listposition_y);
                    }else{
                        for (twitter4j.Status status : result) {
                            if(count == 0)
                            {
                                StatusIDs.remove(status);
                            }
                            StatusIDs.add(status);
                            count++;
                        }
                    }

                    if(count == 0){
                        StatusIDs.add(0,null);
                    }else {
                        if (OldStatus == 0 || !NewReloadFulg) {
                            StatusIDs.add(null);
                        }
                        mAdapter.clear();
                        for (twitter4j.Status tweet : StatusIDs) {
                            mAdapter.add(tweet);
                        }
                    }

                    if(NewStatus != 0 && NewReloadFulg){
                        lv.setSelectionFromTop(position + count + 1, y);	//ListViewの表示位置をずらす
                    }
                    NewStatus = StatusIDs.get(1).getId();
                    OldStatus = StatusIDs.get(mAdapter.getCount() - 2).getId();
                } else {
                    MainActivity.showToast("ツイートの取得に失敗しました。。。\r\n" + exception);
                }
            }
        };
        task.execute();
    }
}