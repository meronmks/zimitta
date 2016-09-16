package com.meronmks.zimitta.Fragments;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.ListView;

import com.meronmks.zimitta.Core.MainActivity;
import com.meronmks.zimitta.Datas.ParcelStatus;
import com.meronmks.zimitta.Datas.Variable;
import com.meronmks.zimitta.R;
import com.meronmks.zimitta.TwitterUtil.StreamReceiver;
import com.meronmks.zimitta.TwitterUtil.TwitterAction;

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
    private boolean isStatusAdd;
    private StreamReceiver mStreamReceiver;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mAction = new TwitterAction(getContext(), listener);
        Variable.twitterStream.user();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.core_fragment, container, false);

        mListView = (ListView) v.findViewById(R.id.list);
        mListView.setAdapter(Variable.TLAdapter);

        setItemClickListener();
        setLongItemClickListener();
        setScrollListener();
        setReceiver();

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
        isStatusAdd = false;
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(getContext());
        Paging p = new Paging();
        p.count(Integer.parseInt(sp.getString("Load_Tweet", "20")));
        mAction.getHomeTimeline(p);
    }

    @Override
    public void onRefresh() {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(getContext());
        isStatusAdd = false;
        Paging p = new Paging();
        p.count(Integer.parseInt(sp.getString("Load_Tweet", "20")));
        mAction.getHomeTimeline(p);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        mStreamReceiver.unregister();
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

    private void setScrollListener(){
        mListView.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView absListView, int i) {

            }

            @Override
            public void onScroll(AbsListView absListView, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                if (totalItemCount != 0 && !mSwipeRefreshLayout.isRefreshing() && totalItemCount == firstVisibleItem + visibleItemCount) {
                    mSwipeRefreshLayout.setRefreshing(true);
                    isStatusAdd = true;
                    SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(getContext());
                    Paging p = new Paging();
                    p.setMaxId(Variable.TLAdapter.getItem(Variable.TLAdapter.getCount()-1).getId());
                    p.count(Integer.parseInt(sp.getString("Load_Tweet", "20")));
                    mAction.getHomeTimeline(p);
                }
            }
        });
    }

    /**
     * レシーバの受け取り口作成
     */
    private void setReceiver(){
        mStreamReceiver = StreamReceiver.register(getContext(), status -> getActivity().runOnUiThread(() -> {
            int pos = 0;
            int top = 0;
            int tmpCount = Variable.TLAdapter.getCount();
            if(!Variable.TLAdapter.isEmpty()) {
                pos = mListView.getFirstVisiblePosition();
                top = mListView.getChildAt(0).getTop();
            }
            Variable.TLAdapter.statusAdd(Variable.TLAdapter, status);
            if(tmpCount != 0 && !isStatusAdd){
                mListView.setSelectionFromTop(pos + (Variable.TLAdapter.getCount() - tmpCount), top);
            }
            if (pos == 0 && top == 0) {
                mListView.smoothScrollToPositionFromTop(0, 0);
            }
        }));
    }

    /**
     * Listener定義
     */
    TwitterListener listener = new TwitterAdapter() {
        @Override
        public void gotHomeTimeline(ResponseList<Status> statuses) {
            getActivity().runOnUiThread(() -> {
                int pos = 0;
                int top = 0;
                int tmpCount = Variable.TLAdapter.getCount();
                if(!Variable.TLAdapter.isEmpty()) {
                    pos = mListView.getFirstVisiblePosition();
                    top = mListView.getChildAt(0).getTop();
                }
                Variable.TLAdapter.statusAddAll(Variable.TLAdapter, statuses);
                if(tmpCount != 0 && !isStatusAdd){
                    mListView.setSelectionFromTop(pos + (Variable.TLAdapter.getCount() - tmpCount), top);
                }
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
