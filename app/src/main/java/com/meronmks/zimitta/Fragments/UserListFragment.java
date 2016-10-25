package com.meronmks.zimitta.Fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.meronmks.zimitta.Core.BaseFragment;
import com.meronmks.zimitta.Datas.ErrorLogs;
import com.meronmks.zimitta.Datas.Variable;
import com.meronmks.zimitta.R;
import com.meronmks.zimitta.TwitterUtil.TwitterAction;

import java.util.Timer;

import twitter4j.ResponseList;
import twitter4j.Status;
import twitter4j.TwitterAdapter;
import twitter4j.TwitterException;
import twitter4j.TwitterListener;
import twitter4j.TwitterMethod;

/**
 * Created by meron on 2016/09/14.
 */
public class UserListFragment extends BaseFragment implements SwipeRefreshLayout.OnRefreshListener {

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mAction = new TwitterAction(getContext(), listener);
        isLimited = false;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.user_list_view, container, false);

        return v;
    }

    @Override
    public void onRefresh() {

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
                if(!Variable.MentionsAdapter.isEmpty() && mListView.getChildAt(0) != null) {
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
                        ErrorLogs.putErrorLog("リプライの取得に失敗しました", te.getMessage());
                        isLimited = true;
                        limitTimer = new Timer();
                        limitTimer.schedule(new LimitTimer(), te.getRateLimitStatus().getSecondsUntilReset()*1000);
                        break;
                }
                mSwipeRefreshLayout.setRefreshing(false);

            });
        }
    };
}
