package com.meronmks.zimitta.Adapter;

/**
 * Created by meron on 2016/09/14.
 */
import android.content.Context;
import android.widget.ArrayAdapter;

import java.util.HashSet;

import twitter4j.ResponseList;
import twitter4j.Status;


public class BaseAdapter<T> extends ArrayAdapter<T> {
    public BaseAdapter(Context context, int resources) {
        super(context, resources);
    }

    /**
     * 重複がなくなるように入れる
     * @param adapter なんか気持ち悪いけどこうするしかなさそう？
     * @param statuses 取得したツイート
     */
    public void statusAddAll(TweetAdapter adapter, ResponseList<Status> statuses){
        adapter.addAll(statuses);
        //ID判断で重複消し
        HashSet<Long> hs = new HashSet<>();
        for (int i = 0; i < adapter.getCount();) {
            if (!hs.contains(adapter.getItem(i).getId())) {
                hs.add(adapter.getItem(i).getId());
                ++i;
            } else {
                adapter.remove(adapter.getItem(i));
            }
        }
        //時間でソート
        adapter.sort((t2, t1) -> t1.getCreatedAt().compareTo(t2.getCreatedAt()));
    }

    /**
     * 重複がなくなるように入れる
     * @param adapter なんか気持ち悪いけどこうするしかなさそう？
     * @param status 取得したツイート
     */
    public void statusAdd(TweetAdapter adapter, Status status){
        adapter.add(status);
        //ID判断で重複消し
        HashSet<Long> hs = new HashSet<>();
        for (int i = 0; i < adapter.getCount();) {
            if (!hs.contains(adapter.getItem(i).getId())) {
                hs.add(adapter.getItem(i).getId());
                ++i;
            } else {
                adapter.remove(adapter.getItem(i));
            }
        }
        //時間でソート
        adapter.sort((t2, t1) -> t1.getCreatedAt().compareTo(t2.getCreatedAt()));
    }
}
