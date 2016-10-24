package com.meronmks.zimitta.Activity;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.Toolbar;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;

import com.meronmks.zimitta.Adapter.AccountListAdapter;
import com.meronmks.zimitta.Core.BaseActivity;
import com.meronmks.zimitta.Core.MainActivity;
import com.meronmks.zimitta.Datas.UserInfo;
import com.meronmks.zimitta.Datas.UserSetting;
import com.meronmks.zimitta.OAuth.OauthUtils;
import com.meronmks.zimitta.OAuth.TwitterOAuthActivity;
import com.meronmks.zimitta.R;

import twitter4j.Status;
import twitter4j.Twitter;
import twitter4j.User;

/**
 * Created by meron on 2016/10/16.
 */

public class AccountChangeActivity extends BaseActivity{

    private Toolbar toolbar;
    private AccountListAdapter adapter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.account_list);
        toolbar = (Toolbar)findViewById(R.id.ToolBar);
        toolbar.setTitle("アカウント選択");
        setSupportActionBar(toolbar);

        adapter = new AccountListAdapter(this);

        SharedPreferences preferences = getSharedPreferences(getString(R.string.Account), 0);
        long accountNum = preferences.getLong(getString(R.string.AccountNum), 0);

        for (long i = 0; i < accountNum; i++) {
            UserInfo userInfo = UserInfo.getInstance(this, i);
            adapter.add(userInfo);
        }

        ListView listView = (ListView)findViewById(R.id.listView);
        listView.setOnItemClickListener((adapterView, view, i, l) -> {
            SharedPreferences.Editor e = preferences.edit();
            e.putLong(getString(R.string.ActiveAccount), i);
            e.commit();
            Intent intetMain = new Intent(AccountChangeActivity.this, MainActivity.class);
            startActivity(intetMain);

            Intent intent = new Intent();
            intent.putExtra("accountChange", "accountChange");
            setResult(Activity.RESULT_OK, intent);
            finish();
        });
        listView.setAdapter(adapter);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.acount_add, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.acount_Menu:
                Intent intent = new Intent(this, TwitterOAuthActivity.class);
                startActivity(intent);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
