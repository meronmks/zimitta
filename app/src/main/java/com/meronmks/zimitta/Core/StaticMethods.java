package com.meronmks.zimitta.Core;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.text.format.DateFormat;
import android.widget.TextView;

import java.util.Calendar;
import java.util.Date;

/**
 * Created by p-user on 2016/10/21.
 * 基底クラスが別々でも同じメソッドを使いたい場合はここへ
 */

public class StaticMethods {
    /**
     * 時間を変換するやつ
     */
    public static void replacrTimeAt(Date TimeStatusNow, Date CreatedAt, TextView timeView){
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
            timeView.setText(DateFormat.format("yyyy/MM/dd kk:mm:ss", CreatedAt));
        }
    }

    /**
     * ループテキストの排除メソッド
     */
    public static String replaceLoopText(String tweetText, Context context){
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
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
}
