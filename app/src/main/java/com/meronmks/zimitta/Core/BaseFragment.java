package com.meronmks.zimitta.Core;

import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.widget.ListView;
import android.widget.Toast;

import com.meronmks.zimitta.Datas.UserSetting;
import com.meronmks.zimitta.Menus.ItemMenu;
import com.meronmks.zimitta.TwitterUtil.StreamReceiver;
import com.meronmks.zimitta.TwitterUtil.TwitterAction;

import java.util.Timer;
import java.util.TimerTask;

import twitter4j.Status;

/**
 * Created by meron on 2016/09/20.
 */
public class BaseFragment extends Fragment {

    protected ListView mListView;
    protected SwipeRefreshLayout mSwipeRefreshLayout;

    protected TwitterAction mAction;
    protected boolean isStatusAdd;
    protected StreamReceiver mStreamReceiver;

    protected Timer limitTimer;
    protected boolean isLimited;

    protected void showToast(String text){
        if(text == null || text.length() == 0) return;
        getActivity().runOnUiThread(() -> {
            Toast.makeText(getContext(), text, Toast.LENGTH_SHORT).show();
        });

    }

    /**
     * メニューの表示
     */
    private void showMenu(Status status){
        ItemMenu itemMenu = new ItemMenu(getActivity());
        itemMenu.show(status);
    }

    protected void setStatusItemClickListener(){
        mListView.setOnItemClickListener((adapterView, view, i, l) -> {
            if(UserSetting.LongItemClickMenu(getContext())) return;
            showMenu((Status) adapterView.getItemAtPosition(i));
        });
    }

    protected void setLongStatusItemClickListener(){
        mListView.setOnItemLongClickListener((adapterView, view, i, l) -> {
            if(!UserSetting.LongItemClickMenu(getContext())) return true;
            showMenu((Status) adapterView.getItemAtPosition(i));
            return true;
        });
    }

    public class LimitTimer extends TimerTask {
        @Override
        public void run() {
            isLimited = false;
            limitTimer.cancel();
        }
    }
}
