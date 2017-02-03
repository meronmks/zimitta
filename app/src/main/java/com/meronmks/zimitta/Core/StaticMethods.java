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
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.meronmks.zimitta.Activity.PlayVideoActivity;
import com.meronmks.zimitta.Activity.ShowImageActivity;
import com.meronmks.zimitta.Activity.UserDetailActivity;
import com.meronmks.zimitta.Datas.Variable;
import com.meronmks.zimitta.R;

import java.util.Calendar;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import twitter4j.MediaEntity;
import twitter4j.Status;
import twitter4j.URLEntity;
import twitter4j.UserMentionEntity;

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
     * @param mediaEntities
     */
    public static String deleteMediaURL(String tweet, MediaEntity[] mediaEntities){
        for(MediaEntity media : mediaEntities){
            tweet = tweet.replaceAll(media.getURL(), "");
        }
        return tweet;
    }

    /**
     * 短縮URLを展開する
     * @param tweet
     * @param urlEntities
     * @return
     */
    public static String expansionURL(String tweet, URLEntity[] urlEntities){
        for(URLEntity url : urlEntities){
            tweet = tweet.replaceAll(url.getURL(), url.getExpandedURL());
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

    /**
     * Holderを初期化
     * @param cv
     * @return
     */
    public static ViewHolder iniViewHolder(View cv){
        ViewHolder vh = new ViewHolder();

        vh.Name = (TextView) cv.findViewById(R.id.Name);
        vh.UserIcon = (ImageView) cv.findViewById(R.id.UserIcon);
        vh.RTUserIcon = (ImageView) cv.findViewById(R.id.RTUserIcon);
        vh.RTIcon = (ImageView) cv.findViewById(R.id.RTIcon);
        vh.ScreenName = (TextView) cv.findViewById(R.id.ScreenName);
        vh.TweetText = (TextView) cv.findViewById(R.id.TweetText);
        vh.Via = (TextView) cv.findViewById(R.id.Via);
        vh.RTCount = (TextView) cv.findViewById(R.id.RTCount);
        vh.FavCount = (TextView) cv.findViewById(R.id.FavCount);

        vh.RTUserName = (TextView) cv.findViewById(R.id.RTUserName);

        vh.TweetDeletedStatus = (ImageView) cv.findViewById(R.id.TweetDeletedStatus);
        vh.LockedStatus = (ImageView) cv.findViewById(R.id.LockedStatus);
        vh.TweetStatus = cv.findViewById(R.id.TweetStatus);
        vh.Time = (TextView) cv.findViewById(R.id.Time);

        vh.PreviewImage = (LinearLayout) cv.findViewById(R.id.PreviewImage);
        vh.ImagePreviewViews[0] = (ImageView) cv.findViewById(R.id.PreviewImageView1);
        vh.ImagePreviewViews[1] = (ImageView) cv.findViewById(R.id.PreviewImageView2);
        vh.ImagePreviewViews[2] = (ImageView) cv.findViewById(R.id.PreviewImageView3);
        vh.ImagePreviewViews[3] = (ImageView) cv.findViewById(R.id.PreviewImageView4);
        vh.PreviewVideoView1 = (ImageView) cv.findViewById(R.id.PreviewVideoView1);

        //引用ツイート関連
        vh.QuoteTweetView = (LinearLayout) cv.findViewById(R.id.QuoteTweetView);
        vh.QuoteName = (TextView) cv.findViewById(R.id.QuoteName);
        vh.QuoteScreenName = (TextView) cv.findViewById(R.id.QuoteScreenName);
        vh.QuoteText = (TextView) cv.findViewById(R.id.QuoteText);
        vh.QuoteAtTime = (TextView) cv.findViewById(R.id.QuoteAtTime);
        vh.QuotePreviewImage = (LinearLayout) cv.findViewById(R.id.QuotePreviewImage);
        vh.ImageQuotePreviewViews[0] = (ImageView) cv.findViewById(R.id.QuotePreviewImageView1);
        vh.ImageQuotePreviewViews[1] = (ImageView) cv.findViewById(R.id.QuotePreviewImageView2);
        vh.ImageQuotePreviewViews[2] = (ImageView) cv.findViewById(R.id.QuotePreviewImageView3);
        vh.ImageQuotePreviewViews[3] = (ImageView) cv.findViewById(R.id.QuotePreviewImageView4);
        vh.QuotePreviewVideoView1 = (ImageView) cv.findViewById(R.id.QuotePreviewVideoView1);

        return vh;
    }

    /**
     * ツイート表示処理
     * @param context
     * @param vh
     * @param item
     */
    public static void setStatusitemtoView(Context context, ViewHolder vh, Status item){
        vh.TweetDeletedStatus.setVisibility(View.GONE);
        vh.RTUserIcon.setVisibility(View.GONE);
        vh.RTIcon.setVisibility(View.GONE);
        vh.RTUserName.setVisibility(View.GONE);
        vh.TweetStatus.setVisibility(View.GONE);
        vh.PreviewImage.setVisibility(View.GONE);
        vh.QuoteTweetView.setVisibility(View.GONE);
        vh.QuotePreviewImage.setVisibility(View.GONE);
        for(int i = 0; i < vh.ImagePreviewViews.length; i++){
            vh.ImagePreviewViews[i].setVisibility(View.GONE);
        }
        for(int i = 0; i < vh.ImageQuotePreviewViews.length; i++){
            vh.ImageQuotePreviewViews[i].setVisibility(View.GONE);
        }

        if(item.getUser().getId() == Variable.userInfo.userID){
            vh.TweetStatus.setVisibility(View.VISIBLE);
            vh.TweetStatus.setBackgroundResource(R.color.Blue);
        }else {
            for (UserMentionEntity entity : item.getUserMentionEntities()) {
                if(!entity.getScreenName().equals(Variable.userInfo.userScreenName))continue;
                vh.TweetStatus.setVisibility(View.VISIBLE);
                vh.TweetStatus.setBackgroundResource(R.color.Rad);
            }
        }
        if(item.isRetweet()){
            vh.TweetStatus.setVisibility(View.VISIBLE);
            vh.TweetStatus.setBackgroundResource(R.color.Green);
            vh.RTUserName.setVisibility(View.VISIBLE);
            vh.RTUserName.setText(item.getUser().getName() + " さんがRT");
            vh.RTUserIcon.setVisibility(View.VISIBLE);
            vh.RTIcon.setVisibility(View.VISIBLE);
            Glide.with(context).load(item.getUser().getProfileImageURLHttps()).into(vh.RTUserIcon);
            item = item.getRetweetedStatus();
        }

        vh.Name.setText(item.getUser().getName());
        Glide.with(context).load(item.getUser().getProfileImageURLHttps()).into(vh.UserIcon);
        vh.ScreenName.setText("@" + item.getUser().getScreenName());
        String text = item.getText();
        //画像処理
        if(item.getMediaEntities().length != 0){
            vh.PreviewImage.setVisibility(View.VISIBLE);
            setPreviewMedia(item.getMediaEntities(),vh.ImagePreviewViews, vh.PreviewVideoView1, context);
            text = deleteMediaURL(text, item.getMediaEntities());
        }
        text = expansionURL(text, item.getURLEntities());
        vh.TweetText.setText(mutableIDandHashTagMobement(text));
        if(vh.TweetText.length() == 0){
            vh.TweetText.setVisibility(View.GONE);
        }else{
            vh.TweetText.setVisibility(View.VISIBLE);
        }
        replacrTimeAt(new Date(), item.getCreatedAt(), vh.Time);
        vh.Via.setText(item.getSource().replaceAll("<.+?>", "") + " : より");
        vh.RTCount.setText("RT : " + item.getRetweetCount());
        vh.FavCount.setText("Fav : " + item.getFavoriteCount());

        //引用ツイート関連
        if(item.getQuotedStatus() != null){
            quoteTweetSetting(item.getQuotedStatus(), vh, context);
        }

        //鍵垢判定
        if(item.getUser().isProtected()){
            vh.LockedStatus.setVisibility(View.VISIBLE);
        }else{
            vh.LockedStatus.setVisibility(View.GONE);
        }

        //リンク処理
        mutableLinkMovement(vh.TweetText);

        final Status finalStatus = item;

        //アイコンクリック処理
        vh.UserIcon.setOnClickListener(view -> {
            Intent intent = new Intent(context, UserDetailActivity.class);
            intent.putExtra("userName", finalStatus.getUser().getScreenName());
            context.startActivity(intent);
        });
    }
}
