package com.meronmks.zimitta.Fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.Spinner;

import com.meronmks.zimitta.Adapter.TweetAdapter;
import com.meronmks.zimitta.Core.BaseFragment;
import com.meronmks.zimitta.Datas.ErrorLogs;
import com.meronmks.zimitta.Datas.UserSetting;
import com.meronmks.zimitta.Datas.Variable;
import com.meronmks.zimitta.R;
import com.meronmks.zimitta.TwitterUtil.StreamReceiver;
import com.meronmks.zimitta.TwitterUtil.TwitterAction;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;

import twitter4j.PagableResponseList;
import twitter4j.Paging;
import twitter4j.ResponseList;
import twitter4j.Status;
import twitter4j.TwitterAdapter;
import twitter4j.TwitterException;
import twitter4j.TwitterListener;
import twitter4j.TwitterMethod;
import twitter4j.User;
import twitter4j.UserList;

/**
 * Created by meron on 2016/09/14.
 */
public class UserListFragment extends BaseFragment implements SwipeRefreshLayout.OnRefreshListener {

    private Spinner spinner;
    private ImageButton reloadButton;
    private List<Long> listMemberIDs;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mAction = new TwitterAction(getContext(), listener);
        isLimited = false;
        listMemberIDs = new ArrayList<>();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.user_list_view, container, false);

        spinner = (Spinner)v.findViewById(R.id.spinner1);
        reloadButton = (ImageButton)v.findViewById(R.id.listTlReloadButton);
        mListView = (ListView) v.findViewById(R.id.userListListView);
        setStatusItemClickListener();
        setLongStatusItemClickListener();
        mListView.setAdapter(Variable.UserListTLAdapter);
        setScrollListener();
        setReceiver();
        setButtonListener();
        setSpinner();
        mSwipeRefreshLayout = (SwipeRefreshLayout) v.findViewById(R.id.refresh);
        mSwipeRefreshLayout.setOnRefreshListener(this);
        mSwipeRefreshLayout.setColorSchemeResources(R.color.Rad, R.color.Green, R.color.Blue, R.color.Orange);
        return v;
    }

    @Override
    public void onRefresh() {
        UserList userList = getSelectUserList();
        if(spinner.getCount() == 0 || userList == null)return;
        Paging p = new Paging();
        p.count(UserSetting.LoadTweetCount(getContext()));
        mAction.getUserListStatuses(userList.getId(), p);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        mSwipeRefreshLayout.removeAllViews();   //残像バグ対策
    }

    private void setSpinner(){
        ArrayAdapter adapter = new ArrayAdapter(getContext(), android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        if(Variable.userLists == null)return;
        for(UserList userList : Variable.userLists){
            adapter.add(userList.getName());
        }
        spinner.setAdapter(adapter);
    }

    private void setButtonListener(){
        reloadButton.setOnClickListener(view -> {
            UserList userList = getSelectUserList();
            if(spinner.getCount() == 0 || userList == null)return;
            mSwipeRefreshLayout.setRefreshing(true);
            Variable.UserListTLAdapter.clear();
            listMemberIDs.clear();
            Paging p = new Paging();
            p.count(UserSetting.LoadTweetCount(getContext()));
            mAction.getUserListStatuses(userList.getId(), p);
            mAction.getUserListMembers(userList.getId(), -1L);
        });

        reloadButton.setOnLongClickListener(view -> {
            showToast(getString(R.string.reloadUserListsText));
            mAction.getUserLists(Variable.userInfo.userID);
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
                    UserList userList = getSelectUserList();
                    if(spinner.getCount() == 0 || userList == null) return;
                    Paging p = new Paging();
                    p.setMaxId(Variable.UserListTLAdapter.getItem(Variable.UserListTLAdapter.getCount()-1).getId());
                    p.count(UserSetting.LoadTweetCount(getContext()));
                    mAction.getUserListStatuses(userList.getId(), p);
                }
            }
        });
    }

    /**
     * 選択されたListを取得
     * @return Listの情報
     */
    private UserList getSelectUserList(){
        for(UserList userList : Variable.userLists){
            if(!spinner.getSelectedItem().equals(userList.getName()))continue;
            return userList;
        }
        return null;
    }

    /**
     * レシーバの受け取り口作成
     */
    private void setReceiver(){
        mStreamReceiver = StreamReceiver.register(getContext(), status -> getActivity().runOnUiThread(() -> {
            int pos = 0;
            int top = 0;
            if(Variable.UserListTLAdapter == null){
                Variable.UserListTLAdapter = new TweetAdapter(getContext());
            }
            if (!Variable.TLAdapter.isEmpty() && mListView.getChildAt(0) != null) {
                pos = mListView.getFirstVisiblePosition();
                top = mListView.getChildAt(0).getTop();
            }

            if(listMemberIDs.indexOf(status.getUser().getId()) == -1) return;

            Variable.UserListTLAdapter.statusAdd(Variable.UserListTLAdapter, status);
            mListView.setSelectionFromTop(pos + 1, top);
            if (pos == 0 && top == 0) {
                mListView.smoothScrollToPositionFromTop(0, 0);
            }
        }));
    }

    /**
     * Listener定義
     */
    private TwitterListener listener = new TwitterAdapter() {

        @Override
        public void gotUserListStatuses(ResponseList<Status> statuses) {
            getActivity().runOnUiThread(() -> {
                int pos = 0;
                int top = 0;
                int tmpCount = Variable.UserListTLAdapter.getCount();
                if(!Variable.UserListTLAdapter.isEmpty() && mListView.getChildAt(0) != null) {
                    pos = mListView.getFirstVisiblePosition();
                    top = mListView.getChildAt(0).getTop();
                }
                Variable.UserListTLAdapter.statusAddAll(Variable.UserListTLAdapter, statuses);
                if(tmpCount != 0 && !isStatusAdd){
                    mListView.setSelectionFromTop(pos + (Variable.UserListTLAdapter.getCount() - tmpCount), top);
                }
                mSwipeRefreshLayout.setRefreshing(false);
            });
        }

        @Override
        public void gotUserLists(ResponseList<UserList> userLists) {
            super.gotUserLists(userLists);
            Variable.userLists = userLists;
            setSpinner();
        }

        @Override
        public void gotUserListMembers(PagableResponseList<User> users) {
            super.gotUserListMembers(users);
            for(User user : users){
                listMemberIDs.add(user.getId());
            }
            if(users.hasNext()) mAction.getUserListMembers(getSelectUserList().getId(), users.getNextCursor());
        }

        @Override
        public void onException(TwitterException te, TwitterMethod method) {
            getActivity().runOnUiThread(() -> {
                switch (method){
                    case USER_LISTS:
                    case USER_LIST_MEMBERSHIPS:
                        showToast(getString(R.string.ListInfoErrorText));
                        ErrorLogs.putErrorLog(getString(R.string.ListInfoErrorText), te.getMessage());
                        break;
                    case USER_LIST_STATUSES:
                        showToast("ListTLの取得に失敗しました。");
                        ErrorLogs.putErrorLog("ListTLの取得に失敗しました", te.getMessage());
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
