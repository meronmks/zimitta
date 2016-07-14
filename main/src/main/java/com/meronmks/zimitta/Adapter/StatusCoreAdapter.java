package com.meronmks.zimitta.Adapter;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;
import android.util.StringBuilderPrinter;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextClock;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.meronmks.zimitta.Activity.ImageActivity;
import com.meronmks.zimitta.R;
import com.meronmks.zimitta.core.CoreActivity;

import org.w3c.dom.Text;

import java.text.DateFormat;
import java.util.Calendar;
import java.util.Date;

import rx.subjects.PublishSubject;
import twitter4j.ExtendedMediaEntity;
import twitter4j.MediaEntity;

/**
 * Created by p-user on 2016/07/14.
 */
public class StatusCoreAdapter<T> extends ArrayAdapter<T> {

    private Context context;
    public PublishSubject<Integer> clickObservable = PublishSubject.create();
    public PublishSubject<Integer> longClickObservable = PublishSubject.create();

    public StatusCoreAdapter(Context context, int resources) {
        super(context, resources);
        this.context = context;
    }

    @Override
    public View getView(int position, View view, ViewGroup parent) {
        view.setOnLongClickListener(v -> {
            longClickObservable.onNext(position);
            return true;
        });
        view.setOnClickListener(v -> clickObservable.onNext(position));
        return view;
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
        if (time <= 59) {
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
}
