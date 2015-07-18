package com.meronmks.zimitta.user;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.AlertDialog;
import android.app.ListActivity;
import android.content.DialogInterface;
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
import com.meronmks.zimitta.Activity.TwitterOAuthActivity;
import com.meronmks.zimitta.Adapter.FollowAdapter;
import com.meronmks.zimitta.core.MainActivity;
import com.meronmks.zimitta.core.TwitterUtils;
import twitter4j.PagableResponseList;
import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.User;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by p-user on 2015/05/25.
 */
public class UserFollowActivity extends ListActivity {

    private FollowAdapter mAdapter;
    private Twitter mTwitter;
    private long UserID = 0;
    private long Next_cursor = 0;
    private String ScreenName;
    private SharedPreferences sp;
    private boolean NewReloadFulg = true;
    private List<User> StatusIDs = new ArrayList<User>();
    private  SharedPreferences accountIDCount;

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
            mAdapter = new FollowAdapter(this);
            setListAdapter(mAdapter);
            ListView lv = getListView();
            final boolean LongTap = sp.getBoolean("Tap_Setting", true);

            lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {

                @Override
                public void onItemClick(AdapterView<?> parent, View view,int position, long id) {
                    if(LongTap == false){
                        List_Menu(position);
                    }
                }
            });

            lv.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
                @Override
                public boolean onItemLongClick(AdapterView<?> parent, View view,int position, long id) {
                    if(LongTap == true){
                        List_Menu(position);
                    }
                    return true;
                }
            });
            mTwitter = TwitterUtils.getTwitterInstance(this,accountIDCount.getLong("ID_Num_Now", 0));
            MainActivity.showProcessDialog();
            Intent Intent = getIntent();
            UserID = Intent.getLongExtra("UserID_TL", BIND_ABOVE_CLIENT);
            ScreenName = Intent.getStringExtra("ScreenName");
            if(UserID != 0){
                setTitle(ScreenName + "フォロー一覧");
                getFriendsList();
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        PaintDrawable paintDrawable = new PaintDrawable(Color.argb(255, 0, 0, 0));
        getWindow().setBackgroundDrawable(paintDrawable);
    }

    //フォロー一覧取得
    private void getFriendsList() {
        AsyncTask<Void, Void, PagableResponseList<User>> task = new AsyncTask<Void, Void, PagableResponseList<User>>() {
            String exception;
            long cursor = -1L;
            @Override
            protected PagableResponseList<User> doInBackground(Void... params) {
                try {
                    if(!NewReloadFulg){
                        cursor = Next_cursor;
                    }
                    return mTwitter.getFriendsList(UserID, cursor);
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
                    MainActivity.dismissProcessDialog();
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
            dialogItem = new String[]{"ユーザー詳細"};
        }else{
            dialogItem = new String[]{"ユーザー詳細","さらに読み込む"};
        }
        AlertDialog.Builder dialogMenu = new AlertDialog.Builder(this);
        dialogMenu.setItems(dialogItem, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                switch(which)
                {
                    case 0:
                        Intent My_Prof_Intent = new Intent(UserFollowActivity.this, Prof_Activity.class);
                        My_Prof_Intent.putExtra("UserID", StatusIDs.get(position).getId());
                        startActivity(My_Prof_Intent);
                        break;
                    case 1:
                        NewReloadFulg = false;
                        MainActivity.showProcessDialog();
                        getFriendsList();
                        break;
                }
            }
        }).create().show();
    }

    private void showDialog(String text){
        AlertDialog.Builder alertDialog=new AlertDialog.Builder(this);
        alertDialog.setTitle("Error!");
        alertDialog.setMessage(text);

        alertDialog.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {

            }
        });
        alertDialog.show();
    }
}