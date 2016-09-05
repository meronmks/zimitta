package com.meronmks.zimitta.user;

import java.util.ArrayList;
import java.util.List;

import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.AlertDialog;
import com.meronmks.zimitta.Adapter.FollowAdapter;
import com.meronmks.zimitta.R;
import com.meronmks.zimitta.core.TwitterUtils;
import twitter4j.PagableResponseList;
import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.User;
import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

public class UserFollowersActivity extends AppCompatActivity {

    private FollowAdapter mAdapter;
    private Twitter mTwitter;
    private long UserID = 0;
    private long Next_cursor = 0;
    private String ScreenName;
    private SharedPreferences sp;
    private boolean NewReloadFulg = true;
    private List<User> StatusIDs = new ArrayList<User>();
    private  SharedPreferences accountIDCount;

    //定数
    public static final String cache = "mention_cache.txt";

    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    @SuppressLint("NewApi")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        sp = PreferenceManager.getDefaultSharedPreferences(this);
        accountIDCount = getSharedPreferences(getString(R.string.SelectAccount), 0);
        mAdapter = new FollowAdapter(this);
        setContentView(R.layout.listview_base);
        ListView listView = (ListView)findViewById(R.id.listViewBase);
        listView.setAdapter(mAdapter);
        final boolean LongTap = sp.getBoolean("Tap_Setting", true);

        mAdapter.clickObservable
                .subscribe(position -> {
                    if (LongTap == false) {
                        List_Menu(position);
                    }
                });

        mAdapter.longClickObservable
                .subscribe(position -> {
                    if(LongTap == true){
                        List_Menu(position);
                    }
                });
        mTwitter = TwitterUtils.getTwitterInstance(this,accountIDCount.getLong(getString(R.string.SelectAccountNum), 0));
        Intent Intent = getIntent();
        UserID = Intent.getLongExtra("UserID_TL", BIND_ABOVE_CLIENT);
        ScreenName = Intent.getStringExtra(getString(R.string.ScreanNames));
        if(UserID != 0){
            setTitle(ScreenName + "のフォロワー一覧");
            reloadUserTimeLine();
        }
    }

    //フォロワーの非同期取得
    private void reloadUserTimeLine() {
        AsyncTask<Void, Void, PagableResponseList<User>> task = new AsyncTask<Void, Void, PagableResponseList<User>>() {
            String exception;
            long cursor = -1L;
            @Override
            protected PagableResponseList<User> doInBackground(Void... params) {
                try {
                    if(!NewReloadFulg){
                        cursor = Next_cursor;
                    }
                    return mTwitter.getFollowersList(UserID, cursor);
                } catch (TwitterException e){
                    e.printStackTrace();
                    exception = e.getMessage();
                } catch(Exception e)
                {
                    e.printStackTrace();
                    exception = e.getMessage();
                }
                return null;
            }

            @Override
            protected void onPostExecute(PagableResponseList<User> result) {
                if (result != null) {
                    for (User status : result) {
                        StatusIDs.add(status);
                        mAdapter.add(status);
                    }
                    Next_cursor = result.getNextCursor();
                }else{
                    showDialog(exception);
                }
            }
        };
        task.execute();
    }

    private void List_Menu(final int position){
        String[] dialogItem;
        if(position != StatusIDs.size() - 1){
            dialogItem = new String[]{"ユーザー詳細"};	//メニューの項目作り
        }else{
            dialogItem = new String[]{"ユーザー詳細","さらに読み込む"};	//メニューの項目作り
        }
        AlertDialog.Builder dialogMenu = new AlertDialog.Builder(this);
        dialogMenu.setItems(dialogItem, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                switch(which)
                {
                    case 0://ユーザー詳細
                        Intent My_Prof_Intent = new Intent(UserFollowersActivity.this, Prof_Activity.class);
                        My_Prof_Intent.putExtra("UserID", StatusIDs.get(position).getId());
                        startActivity(My_Prof_Intent);
                        break;
                    case 1: //フォロワーの追加取得
                        NewReloadFulg = false;
                        reloadUserTimeLine();
                        break;
                }
            }
        }).create().show();
    }

    private void showDialog(String text){
        AlertDialog.Builder alertDialog=new AlertDialog.Builder(this);
        alertDialog.setTitle("Error!");      //タイトル設定
        alertDialog.setMessage(text);  //内容(メッセージ)設定

        // OK(肯定的な)ボタンの設定
        alertDialog.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                // OKボタン押下時の処理
            }
        });
        alertDialog.show();
    }
}