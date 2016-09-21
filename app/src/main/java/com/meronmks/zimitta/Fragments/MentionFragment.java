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
import android.widget.AbsListView;
import android.widget.ListView;

import com.meronmks.zimitta.Core.BaseFragment;
import com.meronmks.zimitta.Datas.Variable;
import com.meronmks.zimitta.R;
import com.meronmks.zimitta.TwitterUtil.StreamReceiver;
import com.meronmks.zimitta.TwitterUtil.TwitterAction;

import java.util.Timer;
import java.util.TimerTask;

import twitter4j.Paging;
import twitter4j.ResponseList;
import twitter4j.Status;
import twitter4j.TwitterAdapter;
import twitter4j.TwitterException;
import twitter4j.TwitterListener;
import twitter4j.TwitterMethod;
import twitter4j.UserMentionEntity;

/**
 * Created by meron on 2016/09/14.
 */
public class MentionFragment extends BaseFragment implements SwipeRefreshLayout.OnRefreshListener {

    private ListView mListView;
    private SwipeRefreshLayout mSwipeRefreshLayout;

    private TwitterAction mAction;
    private boolean isStatusAdd;
    private StreamReceiver mStreamReceiver;

    private Timer limitTimer;
    private boolean isLimited;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mAction = new TwitterAction(getContext(), listener);
        isLimited = false;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.core_fragment, container, false);

        mListView = (ListView) v.findViewById(R.id.list);
        setItemClickListener();
        setLongItemClickListener();
        mListView.setAdapter(Variable.MentionsAdapter);
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
        if(!Variable.MentionsAdapter.isEmpty())return;
        mSwipeRefreshLayout.setRefreshing(true);
        isStatusAdd = false;
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(getContext());
        Paging p = new Paging();
        p.count(Integer.parseInt(sp.getString("Load_Tweet", "20")));
        mAction.getMentions(p);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        mSwipeRefreshLayout.removeAllViews();   //残像バグ対策
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mStreamReceiver.unregister();
    }

    private void setItemClickListener(){
        mListView.setOnItemClickListener((adapterView, view, i, l) -> {
            showToast("Click!");
        });
    }

    private void setLongItemClickListener(){
        mListView.setOnItemLongClickListener((adapterView, view, i, l) -> {
            showToast("LongClick!");
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
                if (totalItemCount != 0 && !mSwipeRefreshLayout.isRefreshing() && !isLimited && totalItemCount == firstVisibleItem + visibleItemCount) {
                    mSwipeRefreshLayout.setRefreshing(true);
                    isStatusAdd = true;
                    SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(getContext());
                    Paging p = new Paging();
                    p.setMaxId(Variable.MentionsAdapter.getItem(Variable.MentionsAdapter.getCount()-1).getId());
                    p.count(Integer.parseInt(sp.getString("Load_Tweet", "20")));
                    mAction.getMentions(p);
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
            if (!Variable.MentionsAdapter.isEmpty()) {
                pos = mListView.getFirstVisiblePosition();
                top = mListView.getChildAt(0).getTop();
            }

            if(status.getUser().getId() != Variable.userID && !status.isRetweet()){
                for (UserMentionEntity entity : status.getUserMentionEntities()) {
                    if(!entity.getScreenName().equals(Variable.userName))continue;
                    Variable.MentionsAdapter.statusAdd(Variable.MentionsAdapter, status);
                    mListView.setSelectionFromTop(pos + 1, top);
                    if (pos == 0 && top == 0) {
                        mListView.smoothScrollToPositionFromTop(0, 0);
                    }
                }
            }
        }));
    }
    
    @Override
    public void onRefresh() {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(getContext());
        isStatusAdd = false;
        Paging p = new Paging();
        p.count(Integer.parseInt(sp.getString("Load_Tweet", "20")));
        mAction.getMentions(p);
    }

    /**
     * Listener定義
     */
    private TwitterListener listener = new TwitterAdapter() {
        @Override
        public void gotMentions(ResponseList<Status> statuses) {
            getActivity().runOnUiThread(() -> {
                int pos = 0;
                int top = 0;
                int tmpCount = Variable.MentionsAdapter.getCount();
                if(!Variable.MentionsAdapter.isEmpty()) {
                    pos = mListView.getFirstVisiblePosition();
                    top = mListView.getChildAt(0).getTop();
                }
                Variable.MentionsAdapter.statusAddAll(Variable.MentionsAdapter, statuses);
                if(tmpCount != 0 && !isStatusAdd){
                    mListView.setSelectionFromTop(pos + (Variable.MentionsAdapter.getCount() - tmpCount), top);
                }
                mSwipeRefreshLayout.setRefreshing(false);
            });
        }

        @Override
        public void onException(TwitterException te, TwitterMethod method) {
            getActivity().runOnUiThread(() -> {
                switch (method){
                    case MENTIONS_TIMELINE:
                        showToast("リプライの取得に失敗しました。");
                        isLimited = true;
                        limitTimer = new Timer();
                        limitTimer.schedule(new LimitTimer(), te.getRateLimitStatus().getSecondsUntilReset()*1000);
                        break;
                }
                mSwipeRefreshLayout.setRefreshing(false);

            });
        }
    };

    private class LimitTimer extends TimerTask {
        @Override
        public void run() {
            isLimited = false;
            limitTimer.cancel();
        }
    }
}
