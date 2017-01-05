package com.meronmks.zimitta.Core;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.format.DateFormat;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.meronmks.zimitta.Activity.PlayVideoActivity;
import com.meronmks.zimitta.Activity.ShowImageActivity;
import com.meronmks.zimitta.R;

import java.util.Calendar;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import twitter4j.MediaEntity;
import twitter4j.Status;

/**
 * Created by p-user on 2016/10/21.
 * 基底クラスが別々でも同じメソッドを使いたい場合はここへ
 */

public class StaticMethods {

    private static final Pattern ID_MATCH_PATTERN = Pattern.compile("@[a-zA-Z0-9_]+", Pattern.CASE_INSENSITIVE);
    private static final Pattern HASH_TAG_MATCH_PATTERN = Pattern.compile("[#＃][Ａ-Ｚａ-ｚA-Za-z一-鿆0-9０-９ぁ-ヶｦ-ﾟー_]+", Pattern.CASE_INSENSITIVE);

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

    /**
     * メディアURLを消す
     * @param tweet
     * @param mediaEntity
     */
    public static String deleteMediaURL(String tweet, MediaEntity[] mediaEntity){
        for(MediaEntity media : mediaEntity){
            tweet = tweet.replaceAll(media.getURL(), "");
        }
        return tweet;
    }

    /**
     * メディアのプレビュー表示
     * @param mediaEntity
     * @param imageViews
     */
    public static void setPreviewMedia(MediaEntity[] mediaEntity, ImageView[] imageViews, ImageView videoPlayView, Context context){
        for(int i = 0; i < mediaEntity.length; i++){
            imageViews[i].setVisibility(View.VISIBLE);
            if(mediaEntity[i].getType().equals("photo")) {
                videoPlayView.setVisibility(View.GONE);
            }else{
                videoPlayView.setVisibility(View.VISIBLE);
            }
            Glide.with(context)
                    .load(mediaEntity[i].getMediaURLHttps() + ":thumb")
                    .placeholder(R.mipmap.ic_sync_white_24dp)
                    .error(R.mipmap.ic_sync_problem_white_24dp)
                    .dontAnimate()
                    .into(imageViews[i]);

            final int finalI = i;
            imageViews[i].setOnClickListener(view -> {
                if(mediaEntity[finalI].getType().equals("photo")){
                    String imageURL = mediaEntity[finalI].getMediaURLHttps();
                    Intent image = new Intent(context, ShowImageActivity.class);
                    image.putExtra("Images", imageURL);
                    context.startActivity(image);
                }else{
                    MediaEntity.Variant[] videoURLs = mediaEntity[finalI].getVideoVariants();
                    MediaEntity.Variant videoURL = videoURLs[0];
                    for(MediaEntity.Variant var : videoURLs){
                        if(var.getContentType().equals("mp4") && var.getBitrate() > videoURL.getBitrate()){
                            videoURL = var;
                        }
                    }
                    Intent video = new Intent(context, PlayVideoActivity.class);
                    video.putExtra("Video", videoURL.getUrl());
                    context.startActivity(video);
                }
            });
        }
    }

    /**
     * 引用ツイートの処理
     * @param status
     */
    public static void quoteTweetSetting(Status status, ViewHolder vh, Context context){
        vh.QuoteTweetView.setVisibility(View.VISIBLE);
        vh.QuoteName.setText(status.getUser().getName());
        vh.QuoteScreenName.setText("@" + status.getUser().getScreenName());
        String text = status.getText();
        if(status.getMediaEntities().length != 0){
            vh.QuotePreviewImage.setVisibility(View.VISIBLE);
            setPreviewMedia(status.getMediaEntities(),vh.ImageQuotePreviewViews, vh.QuotePreviewVideoView1, context);
            text = deleteMediaURL(text, status.getMediaEntities());
        }
        vh.QuoteText.setText(mutableIDandHashTagMobement(text));
        if(vh.QuoteText.length() == 0){
            vh.QuoteText.setVisibility(View.GONE);
        }else{
            vh.QuoteText.setVisibility(View.VISIBLE);
        }
        replacrTimeAt(new Date(), status.getCreatedAt(), vh.QuoteAtTime);
        mutableLinkMovement(vh.QuoteText);
    }

    /**
     * テキストからIDとハッシュタグを抽出してクリック可能に
     * @param string
     * @return
     */
    public static SpannableString mutableIDandHashTagMobement(String string){
        SpannableString spannable = new SpannableString(string);
        Matcher matcher = ID_MATCH_PATTERN.matcher(string);
        while (matcher.find()){
            ScreenNameClickable span = new ScreenNameClickable(matcher.group());
            spannable.setSpan(span, matcher.start(), matcher.end(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        }

        matcher = HASH_TAG_MATCH_PATTERN.matcher(string);
        while (matcher.find()){
            HashTagClickable span = new HashTagClickable();
            spannable.setSpan(span, matcher.start(), matcher.end(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        }
        return spannable;
    }

    /**
     * TextViewのリンク以外のクリックイベントを更に下のViewへ渡す
     * @param TweetText
     */
    public static void mutableLinkMovement(TextView TweetText){
        TweetText.setOnTouchListener((view, event) -> {
            TextView textView = (TextView) view;
            //LinkMovementMethodを継承したもの 下記参照
            MutableLinkMovementMethod m = new MutableLinkMovementMethod();
            //リンクのチェックを行うため一時的にsetする
            textView.setMovementMethod(m);
            boolean mt = m.onTouchEvent(textView, (Spannable) textView.getText(), event);
            //チェックが終わったので解除する しないと親view(listview)に行けない
            textView.setMovementMethod(null);
            //setMovementMethodを呼ぶとフォーカスがtrueになるのでfalseにする
            textView.setFocusable(false);
            //戻り値がtrueの場合は今のviewで処理、falseの場合は親viewで処理
            return mt;
        });
    }
}
