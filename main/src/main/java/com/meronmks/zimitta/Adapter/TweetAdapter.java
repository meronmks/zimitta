package com.meronmks.zimitta.Adapter;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import com.bumptech.glide.Glide;
import com.meronmks.zimitta.R;
import com.meronmks.zimitta.Variable.CoreVariable;
import twitter4j.MediaEntity;
import twitter4j.Status;
import twitter4j.URLEntity;

import java.text.DateFormat;
import java.util.Calendar;
import java.util.Date;

public class TweetAdapter extends ArrayAdapter<Status> {
	private LayoutInflater mInflater;
	private String TweetText;
    private ViewHolder holder;
    private String hostClassName;

    public TweetAdapter(Context context) {
        super(context, android.R.layout.simple_list_item_1);
        mInflater = (LayoutInflater) context.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
        hostClassName = context.getClass().getName();
    }

    static class ViewHolder {
        ImageView icon,rticon;
        TextView name;
        TextView screenName;
        TextView text;
        TextView time;
        TextView rt_To;
        TextView rt;
        TextView fav;
        TextView via;
        ImageView tweetStatus;
        ImageView lockedImageView;
        ImageView deletedTweetImageView;
        RelativeLayout relativeLayout;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {

        final Status item = getItem(position);
        if(item != null) {
            Date TimeStatusNow = null;
            Date TimeStatusTweet = null;
            Calendar cal1 = Calendar.getInstance();
            Calendar cal2 = Calendar.getInstance();
            Long My_ID = CoreVariable.Userid;	//共有変数の呼び出し
            TimeStatusNow = new Date();
            if(convertView == null) {
                convertView = iniHolder();
                convertView.setTag(holder);
            }else{
                holder = (ViewHolder) convertView.getTag();
            }
            if(holder == null){
                convertView = iniHolder();
                convertView.setTag(holder);
            }
            holder.icon.setVisibility(View.VISIBLE);
            holder.name.setVisibility(View.VISIBLE);
            holder.screenName.setVisibility(View.VISIBLE);
            holder.text.setVisibility(View.VISIBLE);
            holder.time.setVisibility(View.VISIBLE);
            holder.rt_To.setVisibility(View.VISIBLE);
            holder.rt.setVisibility(View.VISIBLE);
            holder.fav.setVisibility(View.VISIBLE);
            holder.via.setVisibility(View.VISIBLE);
            holder.tweetStatus.setVisibility(View.INVISIBLE);
            holder.deletedTweetImageView.setVisibility(View.GONE);
            //ツイートが削除済みか
            if(CoreVariable.runStream) {
                if (CoreVariable.deleteTweet.indexOf(item.getId()) != -1) {
                    holder.deletedTweetImageView.setVisibility(View.VISIBLE);
                } else {
                    holder.deletedTweetImageView.setVisibility(View.GONE);
                }
            }else{
                holder.deletedTweetImageView.setVisibility(View.GONE);
            }
            //RTされたツイートかどうか
            if (item.getRetweetedStatus() == null) {
                //RTされてないツイートだったら
                //鍵垢じゃなかったら
                if(!item.getUser().isProtected()){
                    holder.lockedImageView.setVisibility(View.GONE);
                }else{
                    holder.lockedImageView.setVisibility(View.VISIBLE);
                }
                try{
                    Glide.with(getContext()).load(item.getUser().getProfileImageURLHttps()).into(holder.icon);
                }catch (Exception e){
                    e.getStackTrace();
                    holder.icon.setImageResource(R.drawable.clear);
                    Log.e("GlideException",e.getMessage());
                }

                holder.name.setText(item.getUser().getName().replaceAll("\n", ""));
                holder.screenName.setText("@" + item.getUser().getScreenName());
                TweetText = item.getText();
                //短縮URL置換
                MediaEntity[] ImgLink = null;
                if (item.getMediaEntities() != null) {
                    ImgLink = item.getMediaEntities();
                } else if (item.getExtendedMediaEntities() != null) {
                    ImgLink = item.getExtendedMediaEntities();
                }
                if (ImgLink.length != 0) {
                    for (int i = 0; i < ImgLink.length; i++) {
                        TweetText = TweetText.replaceAll(ImgLink[i].getURL(), ImgLink[i].getMediaURL());
                    }
                }

                URLEntity[] UrlLink = null;
                if (item.getURLEntities() != null) {
                    UrlLink = item.getURLEntities();
                } else if (item.getExtendedMediaEntities() != null) {
                    UrlLink = item.getURLEntities();
                }
                for (int i = 0; i < UrlLink.length; i++) {
                    TweetText = TweetText.replaceAll(UrlLink[i].getURL(), UrlLink[i].getExpandedURL());
                }
                replaceLoopText();
                //テキストを反映
                holder.text.setText(TweetText);
                TimeStatusTweet = item.getCreatedAt();
                cal1.setTime(TimeStatusTweet);
                cal2.setTime(TimeStatusNow);
                long date1 = cal1.getTimeInMillis();
                long date2 = cal2.getTimeInMillis();
                long time = (date2 - date1) / 1000;
                if (time <= 59) {
                    if (9 < time) {
                        holder.time.setText(time + "s前");
                    } else {
                        holder.time.setText("now");
                    }
                }
                time = time / 60;
                if ((time <= 59) && (time >= 1)) {
                    holder.time.setText(time + "m前");
                }
                time = time / 60;
                if ((time <= 23) && (time >= 1)) {
                    holder.time.setText(time + "h前");
                }
                time = time / 24;
                if (time != 0) {
                    holder.time.setText(DateFormat.getDateTimeInstance().format(item.getCreatedAt()));
                }
                holder.rt_To.setVisibility(View.GONE);    //RTした人の名前を非表示
                holder.rticon.setVisibility(View.GONE);
            } else {
                //RTされてるツイートだったら
                //鍵垢じゃなかったら
                if(!item.getRetweetedStatus().getUser().isProtected()){
                    holder.lockedImageView.setVisibility(View.GONE);
                }else{
                    holder.lockedImageView.setVisibility(View.VISIBLE);
                }
                try{
                    Glide.with(getContext()).load(item.getRetweetedStatus().getUser().getProfileImageURLHttps()).into(holder.icon);
                    Glide.with(getContext()).load(item.getUser().getBiggerProfileImageURLHttps()).into(holder.rticon);
                }catch (Exception e){
                    e.getStackTrace();
                    holder.icon.setImageResource(R.drawable.clear);
                    holder.rticon.setImageResource(R.drawable.clear);
                }

                holder.name.setText(item.getRetweetedStatus().getUser().getName().replaceAll("\n", ""));
                holder.screenName.setText("@" + item.getRetweetedStatus().getUser().getScreenName());
                //短縮URL置換
                MediaEntity[] ImgLink = null;
                if (item.getRetweetedStatus().getMediaEntities() != null) {
                    ImgLink = item.getRetweetedStatus().getMediaEntities();
                } else if (item.getRetweetedStatus().getExtendedMediaEntities() != null) {
                    ImgLink = item.getRetweetedStatus().getExtendedMediaEntities();
                }
                TweetText = item.getRetweetedStatus().getText();
                if (ImgLink.length != 0) {
                    for (int i = 0; i < ImgLink.length; i++) {
                        TweetText = TweetText.replaceAll(ImgLink[i].getURL(), ImgLink[i].getMediaURL());
                    }
                }

                URLEntity[] UrlLink = null;
                if (item.getRetweetedStatus().getURLEntities() != null) {
                    UrlLink = item.getRetweetedStatus().getURLEntities();
                } else if (item.getRetweetedStatus().getExtendedMediaEntities() != null) {
                    UrlLink = item.getRetweetedStatus().getURLEntities();
                }
                for (int i = 0; i < UrlLink.length; i++) {
                    TweetText = TweetText.replaceAll(UrlLink[i].getURL(), UrlLink[i].getExpandedURL());
                }
                replaceLoopText();
                //テキストを反映
                holder.text.setText(TweetText);
                TimeStatusTweet = item.getRetweetedStatus().getCreatedAt();
                cal1.setTime(TimeStatusTweet);
                cal2.setTime(TimeStatusNow);
                long date1 = cal1.getTimeInMillis();
                long date2 = cal2.getTimeInMillis();
                long time = (date2 - date1) / 1000;
                if (time <= 59) {
                    holder.time.setText(time + "s前");
                }
                time = time / 60;
                if ((time <= 59) && (time >= 1)) {
                    holder.time.setText(time + "m前");
                }
                time = time / 60;
                if ((time <= 23) && (time >= 1)) {
                    holder.time.setText(time + "h前");
                }
                time = time / 24;
                if (time != 0) {
                    holder.time.setText(DateFormat.getDateTimeInstance().format(item.getRetweetedStatus().getCreatedAt()));
                }
                holder.rt_To.setVisibility(View.VISIBLE);
                holder.rt_To.setText(item.getUser().getName() + " さんがリツイート");
                holder.tweetStatus.setVisibility(View.VISIBLE);
                holder.tweetStatus.setBackgroundColor(Color.GREEN);
                holder.rticon.setVisibility(View.VISIBLE);
            }
            holder.rt.setText("RT:" + item.getRetweetCount());
            holder.fav.setText("Fav:" + item.getFavoriteCount());
            String str = item.getSource();
            str = str.replaceAll("<.+?>", "");
            holder.via.setText(str + "：より");
            //自分のツイートか
            if (item.getUser().getId() == My_ID) {
                holder.tweetStatus.setVisibility(View.VISIBLE);
                holder.tweetStatus.setBackgroundColor(Color.CYAN);
            }
            //自分宛てのツイートか
            if (item.getUserMentionEntities() != null) {
                for (int i = 0; i < item.getUserMentionEntities().length; i++) {
                    if (item.getUserMentionEntities()[i].getScreenName().equals(CoreVariable.userName)) {
                        holder.tweetStatus.setVisibility(View.VISIBLE);
                        holder.tweetStatus.setBackgroundColor(Color.RED);
                    }
                }
            }
            holder.relativeLayout.setBackgroundResource(R.drawable.listitem_color);
        }else{
            convertView = mInflater.inflate(R.layout.list_item_null, null);
        }
        return convertView;
    }

    /**
     * ループテキストの排除メソッド
     */
    protected void replaceLoopText(){
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(getContext());
        if(sp.getBoolean("zipTweet", false)) {
            //ループテキストを圧縮準備
            boolean loopTextFound = false;
            TweetText = TweetText.replaceAll("\r\n", "\n");
            //一行ずつ取り出し
            String[] loopStrings = TweetText.split("\n");
            //ループしてるか判定ししていたら文字を消す
            //ただし最初に一致しているのを発見した場合「Following text looped」に置き換える
            for (int i = 1; i < loopStrings.length; i++) {
                if (loopStrings[0].equals(loopStrings[i]) && !loopTextFound) {
                    loopStrings[i] = "Following text looped";
                    loopTextFound = true;
                } else {
                    loopStrings[i] = loopStrings[i].replace(loopStrings[0], "");
                }
            }
            //分解して処理が終わったテキストを合成
            StringBuffer buf = new StringBuffer();
            for (int i = 0; i < loopStrings.length; i++) {
                if (i + 1 != loopStrings.length && !loopStrings[i].equals("")) {
                    buf.append(loopStrings[i] + "\n");
                } else {
                    buf.append(loopStrings[i]);
                }
            }
            TweetText = buf.toString();
        }
    }

    /**
     * holder初期化等々のメソッド
     */
    protected View iniHolder(){
        View convertView = mInflater.inflate(R.layout.list_item_tweet, null);
        holder = new ViewHolder();
        holder.icon = (ImageView) convertView.findViewById(R.id.icon);    //画像View
        holder.rticon = (ImageView) convertView.findViewById(R.id.rticon);    //画像View
        holder.name = (TextView) convertView.findViewById(R.id.name);        //名前View
        holder.screenName = (TextView) convertView.findViewById(R.id.screen_name);    //ＩＤView
        holder.text = (TextView) convertView.findViewById(R.id.text);        //ツイート本文View
        holder.time = (TextView) convertView.findViewById(R.id.time);    //投稿時間View
        holder.rt_To = (TextView) convertView.findViewById(R.id.RT_to);    //ＲＴした人View
        holder.rt = (TextView) convertView.findViewById(R.id.RT);        //RT数View
        holder.fav = (TextView) convertView.findViewById(R.id.Fav);    //お気に入り数View
        holder.via = (TextView) convertView.findViewById(R.id.via);    //投稿されたクライアント名View
        holder.tweetStatus = (ImageView) convertView.findViewById(R.id.TweetStatus);    //右の帯View
        holder.lockedImageView = (ImageView) convertView.findViewById(R.id.lockedImageView);
        holder.deletedTweetImageView = (ImageView) convertView.findViewById(R.id.deletedTweetImageView);
        holder.relativeLayout = (android.widget.RelativeLayout) convertView.findViewById(R.id.Tweet_List);
        return convertView;
    }
}