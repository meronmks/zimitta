package com.meronmks.zimitta.Fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import com.meronmks.zimitta.Adapter.TweetAdapter;
import com.meronmks.zimitta.R;
import com.meronmks.zimitta.TwitterUtil.TwitterAction;

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

    private TweetAdapter mAdapter;
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
        mAdapter = new TweetAdapter(getContext());
        mListView.setAdapter(mAdapter);

        setItemClickListener();
        setLongItemClickListener();

        mSwipeRefreshLayout = (SwipeRefreshLayout) v.findViewById(R.id.refresh);
        mSwipeRefreshLayout.setOnRefreshListener(this);
        mSwipeRefreshLayout.setColorSchemeResources(android.R.color.holo_red_light, android.R.color.holo_green_light, android.R.color.holo_blue_light, android.R.color.holo_orange_light);

        return v;
    }

    @Override
    public void onRefresh() {
        mAction.getHomeTimeline();
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
                mAdapter.addAll(statuses);
                mSwipeRefreshLayout.setRefreshing(false);
            });
        }

        @Override
        public void onException(TwitterException te, TwitterMethod method) {
            Log.e("TwitterException", te.getMessage());
        }
    };
}
