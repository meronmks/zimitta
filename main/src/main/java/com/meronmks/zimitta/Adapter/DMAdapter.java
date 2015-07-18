package com.meronmks.zimitta.Adapter;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import com.meronmks.zimitta.R;
import com.meronmks.zimitta.Variable.CoreVariable;
import twitter4j.DirectMessage;
import twitter4j.MediaEntity;
import com.bumptech.glide.Glide;
import twitter4j.URLEntity;

import java.util.Calendar;
import java.util.Date;

/**
 * Created by p-user on 2015/01/20.
 */
public class DMAdapter extends ArrayAdapter<DirectMessage> {
    private LayoutInflater mInflater;
    private String TweetText;
    private ViewHolder holder;

    public DMAdapter(Context context) {
        super(context, android.R.layout.simple_list_item_1);
        mInflater = (LayoutInflater) context.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
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
    public View getView(int position, View convertView, ViewGroup parent) {
        twitter4j.DirectMessage item = getItem(position);
        if(item != null) {
            Date TimeStatusNow = null;
            Date TimeStatusTweet = null;
            Calendar cal1 = Calendar.getInstance();
            Calendar cal2 = Calendar.getInstance();
            Long My_ID = CoreVariable.Userid;
            TimeStatusNow = new Date();
            if (convertView == null) {
                convertView = iniHolder();
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder)convertView.getTag();
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
            holder.rt.setVisibility(View.GONE);
            holder.fav.setVisibility(View.GONE);
            holder.via.setVisibility(View.GONE);
            holder.tweetStatus.setVisibility(View.GONE);
            holder.deletedTweetImageView.setVisibility(View.GONE);
            Glide.with(getContext()).load(item.getSender().getProfileImageURLHttps()).into(holder.icon);
            holder.name.setText(item.getSender().getName());
            holder.screenName.setText("@" + item.getSender().getScreenName());
            TweetText = item.getText();

            //鍵垢じゃなかったら
            if(!item.getSender().isProtected()){
                holder.lockedImageView.setVisibility(View.GONE);
            }else{
                holder.lockedImageView.setVisibility(View.VISIBLE);
            }

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

            //テキスト反映
            holder.text.setText(TweetText);
            TimeStatusTweet = item.getCreatedAt();
            cal1.setTime(TimeStatusTweet);
            cal2.setTime(TimeStatusNow);
            long date1 = cal1.getTimeInMillis();
            long date2 = cal2.getTimeInMillis();
            long time = (date2 - date1) / 1000;
            if (time <= 59) {
                if(9 < time) {
                    holder.time.setText(time + "s前");
                }else{
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
                holder.time.setText(item.getCreatedAt().toString());
            }
            holder.rt_To.setVisibility(View.GONE);
            holder.rticon.setVisibility(View.GONE);
            holder.relativeLayout.setBackgroundResource(R.drawable.listitem_color);
        }else{
            convertView = mInflater.inflate(R.layout.list_item_null, null);
        }
        return convertView;
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