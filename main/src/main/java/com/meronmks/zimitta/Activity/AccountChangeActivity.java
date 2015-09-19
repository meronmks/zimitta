package com.meronmks.zimitta.Activity;

import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.*;
import android.widget.AdapterView;
import android.widget.ListView;
import com.meronmks.zimitta.Adapter.AccountListAdapter;
import com.meronmks.zimitta.R;
import com.meronmks.zimitta.Variable.CoreVariable;
import com.meronmks.zimitta.core.CoreActivity;
import com.meronmks.zimitta.core.TwitterUtils;
import twitter4j.Twitter;
import twitter4j.User;
import android.content.Intent;

/**
 * Created by meronmks on 2015/03/25.
 */
public class AccountChangeActivity extends ActionBarActivity {
    private SharedPreferences accountIDCount;
    private AccountListAdapter adapter;
    private Twitter mTwitter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.listview_base);

        getSupportActionBar().setDisplayShowHomeEnabled(false);

        adapter = new AccountListAdapter(this);
        accountIDCount = getSharedPreferences("accountidcount", 0);
        long ID = accountIDCount.getLong("ID_Num", 0);

        for (long i = 0; i < ID; i++) {
            getUserItem(i);
        }

        ListView listView = (ListView)findViewById(R.id.listView_base);
        listView.setAdapter(adapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                Editor e = accountIDCount.edit();
                e.putLong("ID_Num_Now", position);
                e.commit();
                CoreVariable.TLmAdapter = null;
                Intent intent = new Intent(AccountChangeActivity.this, CoreActivity.class);
                startActivity(intent);
                finish();
            }

        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.acount_add, menu);
        return true;
    }

    @Override public boolean onKeyDown(int keyCode, KeyEvent event) {
        if(keyCode== KeyEvent.KEYCODE_BACK){
            CoreVariable.TLmAdapter = null;
            Intent intent = new Intent(AccountChangeActivity.this, CoreActivity.class);
            startActivity(intent);
            finish();
            return true;
        }
        return false;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.acount_Menu:
                Intent intent = new Intent(this, TwitterOAuthActivity.class);
                intent.putExtra("Flag", true);
                startActivity(intent);
                finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void getUserItem(final long i) {
        AsyncTask<Void, Void, twitter4j.User> task = new AsyncTask<Void, Void, User>() {

            @Override
            protected twitter4j.User doInBackground(Void... params) {
                try {
                    mTwitter = TwitterUtils.getTwitterInstance(AccountChangeActivity.this, i);
                    return mTwitter.verifyCredentials();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                return null;
            }

            @Override
            protected void onPostExecute(twitter4j.User result) {
                if (result != null) {
                    adapter.add(result);
                }
            }
        };
        task.execute();
    }
}
