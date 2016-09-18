package com.meronmks.zimitta.Adapter;

/**
 * Created by meron on 2016/09/14.
 */
import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.meronmks.zimitta.Datas.Variable;

import java.text.DateFormat;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.HashSet;

import rx.subjects.PublishSubject;
import twitter4j.ResponseList;
import twitter4j.Status;


public class BaseAdapter<T> extends ArrayAdapter<T> {

    static class ViewHolder {
        RelativeLayout listItemBase;
        TextView Name;
        ImageView UserIcon;
        ImageView RTUserIcon;
        TextView ScreenName;
        TextView TweetText;
        TextView Time;
        TextView Via;
        TextView RTCount;
        TextView FavCount;

        TextView RTUserName;

        ImageView TweetDeletedStatus;
        ImageView LockedStatus;
        View TweetStatus;

        LinearLayout PreviewImage;

        //引用ツイート関連
        LinearLayout QuoteTweetView;
        TextView QuoteName;
        TextView QuoteScreenName;
        TextView QuoteText;
        TextView QuoteAtTime;
        LinearLayout QuotePreviewImage;
    }

    public BaseAdapter(Context context, int resources) {
        super(context, resources);
    }

    /**
     * ループテキストの排除メソッド
     */
    protected String replaceLoopText(String tweetText){
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(getContext());
        if(sp.getBoolean("zipTweet", false)) {
            //ループテキストを圧縮準備
            boolean loopTextFound = false;
            tweetText = tweetText.replaceAll("\r\n", "\n");
            //一行ずつ取り出し
            String[] loopStrings = tweetText.split("\n");
            //ループしてるか判定し、していたら文字を消す
            //ただし最初に一致しているのを発見した場合「Following text looped」に置き換える
            for (int i = 1; i < loopStrings.length; i++) {
                if (loopStrings[0].equals(loopStrings[i]) && !loopTextFound) {
                    loopStrings[i] = "Following text looped";
                    loopTextFound = true;
                } else {
                    loopStrings[i] = loopStrings[i].replace(loopStrings[0], "");
                }
            }
            //処理が終わったテキストを合成
            StringBuffer buf = new StringBuffer();
            for (int i = 0; i < loopStrings.length; i++) {
                if (i + 1 != loopStrings.length && !loopStrings[i].equals("")) {
                    buf.append(loopStrings[i] + "\n");
                } else {
                    buf.append(loopStrings[i]);
                }
            }
            tweetText = buf.toString();
        }
        return tweetText;
    }

    /**
     * 時間を変換するやつ
     */
    protected void replacrTimeAt(Date TimeStatusNow, Date CreatedAt, TextView timeView){
        Calendar cal1 = Calendar.getInstance();
        Calendar cal2 = Calendar.getInstance();
        cal1.setTime(CreatedAt);
        cal2.setTime(TimeStatusNow);
        long date1 = cal1.getTimeInMillis();
        long date2 = cal2.getTimeInMillis();
        long time = (date2 - date1) / 1000;
        if(time < 5){
            timeView.setText("now");
        }
        if (5 <= time && time <= 59) {
            timeView.setText(time + "s前");
        }
        time = time / 60;
        if ((time <= 59) && (time >= 1)) {
            timeView.setText(time + "m前");
        }
        time = time / 60;
        if ((time <= 23) && (time >= 1)) {
            timeView.setText(time + "h前");
        }
        time = time / 24;
        if (time != 0) {
            timeView.setText(DateFormat.getDateTimeInstance().format(CreatedAt));
        }
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
