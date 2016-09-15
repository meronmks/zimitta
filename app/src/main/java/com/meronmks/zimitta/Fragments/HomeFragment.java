package com.meronmks.zimitta.Fragments;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.meronmks.zimitta.Adapter.TweetAdapter;
import com.meronmks.zimitta.Core.MainActivity;
import com.meronmks.zimitta.Datas.Variable;
import com.meronmks.zimitta.R;
import com.meronmks.zimitta.TwitterUtil.TwitterAction;

import java.util.Comparator;
import java.util.HashSet;

import twitter4j.Paging;
import twitter4j.ResponseList;
import twitter4j.Status;
import twitter4j.TwitterAdapter;
import twitter4j.TwitterException;
import twitter4j.TwitterListener;
import twitter4j.TwitterMethod;

/**
 * Created by meron on 2016/09/14.
 */
public class HomeFragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener {

    private ListView mListView;
    private SwipeRefreshLayout mSwipeRefreshLayout;

    private TwitterAction mAction;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mAction = new TwitterAction(getContext(), listener);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.core_fragment, container, false);

        mListView = (ListView) v.findViewById(R.id.list);
        mListView.setAdapter(Variable.TLAdapter);

        setItemClickListener();
        setLongItemClickListener();

        mSwipeRefreshLayout = (SwipeRefreshLayout) v.findViewById(R.id.refresh);
        mSwipeRefreshLayout.setOnRefreshListener(this);
        mSwipeRefreshLayout.setColorSchemeResources(R.color.Rad, R.color.Green, R.color.Blue, R.color.Orange);

        return v;
    }

    @Override
    public void onResume() {
        super.onResume();
        if(!Variable.TLAdapter.isEmpty())return;
        mSwipeRefreshLayout.setRefreshing(true);
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(getContext());
        Paging p = new Paging();
        p.count(Integer.parseInt(sp.getString("Load_Tweet", "20")));
        mAction.getHomeTimeline(p);
    }

    @Override
    public void onRefresh() {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(getContext());
        Paging p = new Paging();
        p.count(Integer.parseInt(sp.getString("Load_Tweet", "20")));
        mAction.getHomeTimeline(p);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        mSwipeRefreshLayout.removeAllViews();   //残像バグ対策
    }

    private void setItemClickListener(){
        mListView.setOnItemClickListener((adapterView, view, i, l) -> {

        });
    }

    private void setLongItemClickListener(){
        mListView.setOnItemLongClickListener((adapterView, view, i, l) -> {
            return true;
        });
    }

    TwitterListener listener = new TwitterAdapter() {
        @Override
        public void gotHomeTimeline(ResponseList<Status> statuses) {
            getActivity().runOnUiThread(() -> {
                Variable.TLAdapter.statusAddAll(Variable.TLAdapter, statuses);
                mSwipeRefreshLayout.setRefreshing(false);
            });
        }

        @Override
        public void onException(TwitterException te, TwitterMethod method) {
            getActivity().runOnUiThread(() -> {
                mSwipeRefreshLayout.setRefreshing(false);
                MainActivity.showToast(te.getMessage());
            });
        }
    };
}
