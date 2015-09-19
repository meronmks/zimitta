package com.meronmks.zimitta.Fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import com.meronmks.zimitta.R;
import com.meronmks.zimitta.Variable.CoreVariable;
import com.meronmks.zimitta.core.CoreActivity;
import com.meronmks.zimitta.core.TwitterActionClass;

import java.util.Timer;

/**
 * Created by p-user on 2015/06/08.
 */
public class SearchTweetFramgent extends Fragment {

    private Context Activity;
    private TwitterActionClass mtAction;
    private Timer timer;
    private ImageButton searchButton;
    private EditText searchText;
    private ListView lv;

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        Activity = getActivity();

        lv.setAdapter(CoreVariable.searchTweet);

        mtAction = new TwitterActionClass(Activity, CoreVariable.searchTweet,lv,"searchTab",null);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View v = inflater.inflate(R.layout.searchtweetfragment, container, false);

        lv = (ListView)v.findViewById(R.id.searchListView);

        //テキストエリアの取得
        searchText = (EditText)v.findViewById(R.id.searchEditText);

        //ボタンの取得
        searchButton = (ImageButton)v.findViewById(R.id.listTlReloadButton);

        searchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(searchText.getText().length() == 0) return;
                CoreVariable.searchTweet.clear();
                CoreActivity.progresRun();
                mtAction.searchToTweet(searchText.getText().toString(), null);
            }
        });

        return v;
    }

    @Override
    public void onResume() {
        super.onResume();
        //表示の定期更新
        timer = new Timer();
        InvalidateFragmentClass invalidat = new InvalidateFragmentClass();
        invalidat.invalidate(timer,CoreVariable.searchTweet, "searchTab", false);
    }

    @Override
    public void onPause() {
        super.onPause();
        timer.cancel();
    }
}
