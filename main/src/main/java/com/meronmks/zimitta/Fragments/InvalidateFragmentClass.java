package com.meronmks.zimitta.Fragments;

import com.meronmks.zimitta.Adapter.DirectMessageAdapterClass;
import com.meronmks.zimitta.Adapter.TweetAdapter;
import com.meronmks.zimitta.Variable.CoreVariable;
import com.meronmks.zimitta.core.UiHandler;

import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by meronmks on 2015/04/13.
 */
public class InvalidateFragmentClass {
    /**
     * viewからadapterの定期更新
     * @param timer
     * @param className
     */
    public void invalidate(Timer timer, final TweetAdapter mAdapter, final String className, final boolean streemingStok){

        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {

            if(mAdapter == null || mAdapter.getCount() == 0)return;

            //TL以外でHeaderが無いか
            if(mAdapter.getItem(0) != null && !className.equals("TL")){
                new UiHandler() {
                    public void run() {
                        mAdapter.insert(null, 0);
                        mAdapter.notifyDataSetChanged();
                    }
                }.post();
            }
            //TLでストリーミングOFFでHeaderが無い
            else if(className.equals("TL") && !CoreVariable.runStream && mAdapter.getItem(0) != null){
                new UiHandler() {
                    public void run() {
                        mAdapter.insert(null,0);
                        mAdapter.notifyDataSetChanged();
                    }
                }.post();
            }
            //TLでストリーミングONでHeaderがあってストック機能がOFFなら
            else if(className.equals("TL") && CoreVariable.runStream && mAdapter.getItem(0) == null && !streemingStok){
                new UiHandler() {
                    public void run() {
                        mAdapter.remove(mAdapter.getItem(0));
                        mAdapter.notifyDataSetChanged();
                    }
                }.post();
            }
            //TLでストリーミングONでHeaderがなくてストック機能がONなら
            else if(className.equals("TL") && CoreVariable.runStream && mAdapter.getItem(0) != null && streemingStok){
                new UiHandler() {
                    public void run() {
                        mAdapter.insert(null, 0);
                        mAdapter.notifyDataSetChanged();
                    }
                }.post();
            }
            //Headerの追加
            if(mAdapter.getItem(mAdapter.getCount() - 1) != null){
                new UiHandler() {
                    public void run() {
                        mAdapter.add(null);
                        mAdapter.notifyDataSetChanged();
                    }
                }.post();
            }
            }
        }, 0, 1000);
    }

    /**
     * DM用の定期表示更新
     * @param timer
     * @param mAdapter
     */
    public void invalidate(Timer timer, final DirectMessageAdapterClass mAdapter){
        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                if (mAdapter == null || mAdapter.getCount() == 0) return;

                if (mAdapter.getItem(0) != null) {
                    new UiHandler() {
                        public void run() {
                            mAdapter.insert(null, 0);
                            mAdapter.notifyDataSetChanged();
                        }
                    }.post();
                }

                if (mAdapter.getItem(mAdapter.getCount() - 1) != null) {
                    new UiHandler() {
                        public void run() {
                            mAdapter.add(null);
                            mAdapter.notifyDataSetChanged();
                        }
                    }.post();
                }
            }
        }, 0, 1000);
    }
}
