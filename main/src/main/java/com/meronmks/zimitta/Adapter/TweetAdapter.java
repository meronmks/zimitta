package com.meronmks.zimitta.Adapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.*;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.meronmks.zimitta.Activity.ImageActivity;
import com.meronmks.zimitta.R;
import com.meronmks.zimitta.Variable.CoreVariable;
import com.meronmks.zimitta.core.CoreActivity;
import twitter4j.*;

import java.text.DateFormat;
import java.util.Calendar;
import java.util.Date;

public class TweetAdapter extends ArrayAdapter<Status> {
	private LayoutInflater mInflater;
	private String TweetText;
    private ViewHolder holder;
    private Context mContext;

    public TweetAdapter(Context context) {
        super(context, android.R.layout.simple_list_item_1);
        mInflater = (LayoutInflater) context.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
        mContext = context;
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

        RelativeLayout quoteTweetLayout;
        TextView quoteName;
        TextView quoteScreenName;
        TextView quoteText;

        LinearLayout previewImageLinearLayout;
        ImageView previewImageView1;
        ImageView previewImageView2;
        ImageView previewImageView3;
        ImageView previewImageView4;

    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {

        final Status item = getItem(position);
        if(item != null) {
            Date TimeStatusNow = null;
            Long My_ID = CoreVariable.userID;	//共有変数の呼び出し
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
            holder.quoteTweetLayout.setVisibility(View.GONE);
            holder.rticon.setVisibility(View.GONE);
            holder.previewImageLinearLayout.setVisibility(View.GONE);
            holder.previewImageView1.setVisibility(View.GONE);
            holder.previewImageView2.setVisibility(View.GONE);
            holder.previewImageView3.setVisibility(View.GONE);
            holder.previewImageView4.setVisibility(View.GONE);

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
                //引用されていたら
                if(item.getQuotedStatus() != null){
                    Status quoteedItem = item.getQuotedStatus();
                    holder.quoteTweetLayout.setVisibility(View.VISIBLE);
                    holder.quoteName.setText(quoteedItem.getUser().getName());
                    holder.quoteScreenName.setText("@" + quoteedItem.getUser().getScreenName());
                    holder.quoteText.setText(quoteedItem.getText());
                }
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

                replaceMediaURL(item.getMediaEntities());
                replaceMediaURL(item.getExtendedMediaEntities());
                replaceURLEntities(item.getURLEntities());

                replaceLoopText();
                //テキストを反映
                holder.text.setText(TweetText);

                replacrTimeAt(TimeStatusNow, item.getCreatedAt());

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
                TweetText = item.getRetweetedStatus().getText();

                replaceMediaURL(item.getRetweetedStatus().getMediaEntities());
                replaceMediaURL(item.getRetweetedStatus().getExtendedMediaEntities());
                replaceURLEntities(item.getRetweetedStatus().getURLEntities());

                replaceLoopText();
                //テキストを反映
                holder.text.setText(TweetText);

                replacrTimeAt(TimeStatusNow, item.getRetweetedStatus().getCreatedAt());

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
            for (UserMentionEntity UrlLink : item.getUserMentionEntities()) {
                if(UrlLink.getScreenName().equals(CoreVariable.userName) && !item.getUser().getScreenName().equals(CoreVariable.userName))
                {
                    holder.tweetStatus.setVisibility(View.VISIBLE);
                    holder.tweetStatus.setBackgroundColor(Color.RED);
                    break;
                }
            }

            holder.relativeLayout.setBackgroundResource(R.drawable.listitem_color);
        }else{
            convertView = mInflater.inflate(R.layout.list_item_null, null);
        }

//        if(position == 0) {
//            Animation anim = AnimationUtils.loadAnimation(getContext(), R.anim.list_item_add);
//            convertView.startAnimation(anim);
//        }

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
            TweetText = buf.toString();
        }
    }

    /**
     * 画像URLの置換
     * @param mediaEntity
     */
    protected void replaceMediaURL(MediaEntity[] mediaEntity){
        if(mediaEntity == null || mediaEntity.length == 0) return;

        holder.previewImageLinearLayout.setVisibility(View.VISIBLE);

        for(MediaEntity media : mediaEntity){
            TweetText = TweetText.replaceAll(media.getURL(), media.getMediaURL());
        }

        switch (mediaEntity.length){
            case 4:
                holder.previewImageView4 = setPreviewImage(holder.previewImageView4, mediaEntity[3].getMediaURL() + ":small");
            case 3:
                holder.previewImageView3 = setPreviewImage(holder.previewImageView3, mediaEntity[2].getMediaURL() + ":small");
            case 2:
                holder.previewImageView2 = setPreviewImage(holder.previewImageView2, mediaEntity[1].getMediaURL() + ":small");
            case 1:
                holder.previewImageView1 = setPreviewImage(holder.previewImageView1, mediaEntity[0].getMediaURL() + ":small");
        }
    }

    /**
     * 画像URLの置換
     * @param extendedMediaEntity
     */
    protected void replaceMediaURL(ExtendedMediaEntity[] extendedMediaEntity){
        if(extendedMediaEntity == null || extendedMediaEntity.length == 0) return;

        holder.previewImageLinearLayout.setVisibility(View.VISIBLE);

        for(MediaEntity media : extendedMediaEntity){
            TweetText = TweetText.replaceAll(media.getURL(), media.getMediaURL());
        }

        switch (extendedMediaEntity.length) {
            case 4:
                holder.previewImageView4 = setPreviewImage(holder.previewImageView4,extendedMediaEntity[3].getMediaURL() + ":small");
            case 3:
                holder.previewImageView3 = setPreviewImage(holder.previewImageView3,extendedMediaEntity[2].getMediaURL() + ":small");
            case 2:
                holder.previewImageView2 = setPreviewImage(holder.previewImageView2,extendedMediaEntity[1].getMediaURL() + ":small");
            case 1:
                holder.previewImageView1 = setPreviewImage(holder.previewImageView1,extendedMediaEntity[0].getMediaURL() + ":small");
        }
    }

    /**
     * プレビュー画像のセットとImageViewの設定
     * @param previewImageView
     * @param URL
     */
    protected ImageView setPreviewImage(ImageView previewImageView, final String URL){

        previewImageView.setVisibility(View.VISIBLE);
        Glide.with(getContext())
                .load(URL)
                .listener(new RequestListener<String, GlideDrawable>() {
                    @Override
                    public boolean onException(Exception e, String s, Target<GlideDrawable> glideDrawableTarget, boolean b) {
                        Log.e("Glide", "Error in Glide listener");
                        if (e != null) {
                            e.printStackTrace();
                        }
                        return false;
                    }

                    @Override
                    public boolean onResourceReady(GlideDrawable glideDrawable, String s, Target<GlideDrawable> glideDrawableTarget, boolean b, boolean b2) {
                        return false;
                    }
                })
                .placeholder(R.drawable.ic_action_refresh)
                .error(R.drawable.x_c)
                .into(previewImageView);

        previewImageView.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                String imageURL = URL;
                Intent image = new Intent(mContext, ImageActivity.class);
                if (CoreActivity.isTwitpic(imageURL)){
                    imageURL = imageURL.replace("thumb/", "full/");
                }else if (CoreActivity.isPtwipple(imageURL)){
                    imageURL = imageURL.replace("thumb/", "large/");
                }else if (CoreActivity.isTwipple(imageURL)){
                    imageURL = imageURL.replace("thumb/", "large/");
                }else{
                    imageURL = imageURL.replace(":small", ":orig");
                }
                image.putExtra("Imeges", imageURL);
                mContext.startActivity(image);
            }
        });
        return  previewImageView;
    }

    /**
     * URLの置換
     * @param urlEntities
     */
    protected void replaceURLEntities(URLEntity[] urlEntities){
        if(urlEntities == null || urlEntities.length == 0) return;

        int count = 1;
        for (int i = 0; i < urlEntities.length; i++) {
            TweetText = TweetText.replaceAll(urlEntities[i].getURL(), urlEntities[i].getExpandedURL());
            if(CoreActivity.isTwitpic(urlEntities[i].getExpandedURL())){
                holder.previewImageLinearLayout.setVisibility(View.VISIBLE);
                String url = urlEntities[i].getExpandedURL().replace("twitpic.com/", "twitpic.com/show/thumb/");
                switch (count){
                    case 4:
                        holder.previewImageView4 = setPreviewImage(holder.previewImageView4,url);
                    case 3:
                        holder.previewImageView3 = setPreviewImage(holder.previewImageView3,url);
                    case 2:
                        holder.previewImageView2 = setPreviewImage(holder.previewImageView2,url);
                    case 1:
                        holder.previewImageView1 = setPreviewImage(holder.previewImageView1,url);
                }
                count++;
            }else if(CoreActivity.isPtwipple(urlEntities[i].getExpandedURL())){
                holder.previewImageLinearLayout.setVisibility(View.VISIBLE);
                String url = urlEntities[i].getExpandedURL().replace("p.twipple.jp/", "p.twipple.jp/show/thumb/");
                switch (count){
                    case 4:
                        holder.previewImageView4 = setPreviewImage(holder.previewImageView4,url);
                    case 3:
                        holder.previewImageView3 = setPreviewImage(holder.previewImageView3,url);
                    case 2:
                        holder.previewImageView2 = setPreviewImage(holder.previewImageView2,url);
                    case 1:
                        holder.previewImageView1 = setPreviewImage(holder.previewImageView1,url);
                }
                count++;
            }else if(CoreActivity.isTwitpic(urlEntities[i].getExpandedURL())){
                holder.previewImageLinearLayout.setVisibility(View.VISIBLE);
                String url = urlEntities[i].getExpandedURL().replace("twipple.jp/", "twipple.jp/show/thumb/");
                switch (count){
                    case 4:
                        holder.previewImageView4 = setPreviewImage(holder.previewImageView4,url);
                    case 3:
                        holder.previewImageView3 = setPreviewImage(holder.previewImageView3,url);
                    case 2:
                        holder.previewImageView2 = setPreviewImage(holder.previewImageView2,url);
                    case 1:
                        holder.previewImageView1 = setPreviewImage(holder.previewImageView1,url);
                }
                count++;
            }
        }
    }

    /**
     * 時間を変換するやつ
     * @param TimeStatusNow
     * @param CreatedAt
     */
    protected void replacrTimeAt(Date TimeStatusNow, Date CreatedAt){
        Calendar cal1 = Calendar.getInstance();
        Calendar cal2 = Calendar.getInstance();
        cal1.setTime(CreatedAt);
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
            holder.time.setText(DateFormat.getDateTimeInstance().format(CreatedAt));
        }
    }

    /**
     * holder初期化等々のメソッド
     */
    protected View iniHolder(){
        View convertView = mInflater.inflate(R.layout.list_item_tweet, null);
        holder = new ViewHolder();
        holder.icon = (ImageView) convertView.findViewById(R.id.icon);    //画像View
        holder.rticon = (ImageView) convertView.findViewById(R.id.rtIcon);    //画像View
        holder.name = (TextView) convertView.findViewById(R.id.name);        //名前View
        holder.screenName = (TextView) convertView.findViewById(R.id.screenName);    //ＩＤView
        holder.text = (TextView) convertView.findViewById(R.id.text);        //ツイート本文View
        holder.time = (TextView) convertView.findViewById(R.id.time);    //投稿時間View
        holder.rt_To = (TextView) convertView.findViewById(R.id.RT_to);    //ＲＴした人View
        holder.rt = (TextView) convertView.findViewById(R.id.RT);        //RT数View
        holder.fav = (TextView) convertView.findViewById(R.id.Fav);    //お気に入り数View
        holder.via = (TextView) convertView.findViewById(R.id.via);    //投稿されたクライアント名View
        holder.tweetStatus = (ImageView) convertView.findViewById(R.id.tweetStatus);    //右の帯View
        holder.lockedImageView = (ImageView) convertView.findViewById(R.id.lockedImageView);
        holder.deletedTweetImageView = (ImageView) convertView.findViewById(R.id.deletedTweetImageView);
        holder.relativeLayout = (android.widget.RelativeLayout) convertView.findViewById(R.id.tweetList);
        holder.quoteTweetLayout = (android.widget.RelativeLayout) convertView.findViewById(R.id.quoteTweet);
        holder.quoteName = (TextView) convertView.findViewById(R.id.quoteName);
        holder.quoteScreenName = (TextView) convertView.findViewById(R.id.quoteScreenName);
        holder.quoteText = (TextView) convertView.findViewById(R.id.quoteText);
        holder.previewImageLinearLayout = (LinearLayout) convertView.findViewById(R.id.previewImageLinearLayout);
        holder.previewImageView1 = (ImageView) convertView.findViewById(R.id.previewImageView1);
        holder.previewImageView2 = (ImageView) convertView.findViewById(R.id.previewImageView2);
        holder.previewImageView3 = (ImageView) convertView.findViewById(R.id.previewImageView3);
        holder.previewImageView4 = (ImageView) convertView.findViewById(R.id.previewImageView4);
        return convertView;
    }
}