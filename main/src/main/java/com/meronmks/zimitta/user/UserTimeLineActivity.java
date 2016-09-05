package com.meronmks.zimitta.user;

import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.AppCompatActivity;
import android.widget.ListView;
import android.content.Intent;
import com.meronmks.zimitta.Adapter.TweetAdapter;
import com.meronmks.zimitta.R;
import com.meronmks.zimitta.core.TwitterActionClass;

/**
 * Created by meronmks on 2015/04/20.
 */
public class UserTimeLineActivity extends AppCompatActivity {

    private long UserID;
    private String ScreenName;
    private TwitterActionClass mtAction;
    private TweetAdapter mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent Intent = getIntent();
        UserID = Intent.getLongExtra("UserID_TL", BIND_ABOVE_CLIENT);
        ScreenName = Intent.getStringExtra(getString(R.string.ScreanNames));
        setTitle(ScreenName + " TL");
        setContentView(R.layout.listview_base);
        mAdapter = new TweetAdapter(this);
        ListView listView = (ListView)findViewById(R.id.listViewBase);
        listView.setAdapter(mAdapter);
        mtAction = new TwitterActionClass(this,mAdapter,listView,"UserTL",null);
        mtAction.getUserTimeLine(UserID, null);
    }
}
