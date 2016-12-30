package com.meronmks.zimitta.Core;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.text.Spannable;
import android.text.format.DateFormat;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.meronmks.zimitta.Activity.MakeTweetActivity;
import com.meronmks.zimitta.Activity.PlayVideoActivity;
import com.meronmks.zimitta.Activity.ShowImageActivity;
import com.meronmks.zimitta.Datas.Variable;
import com.meronmks.zimitta.R;

import java.util.Calendar;
import java.util.Date;

import twitter4j.MediaEntity;
import twitter4j.Status;
import twitter4j.UserMentionEntity;

/**
 *
 * Created by meron on 2016/09/20.
 */
public class BaseActivity extends AppCompatActivity {

    private ViewHolder vh;

    static class ViewHolder {
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
        ImageView[] ImagePreviewViews = new ImageView[4];
        ImageView PreviewVideoView1;

        //引用ツイート関連
        LinearLayout QuoteTweetView;
        TextView QuoteName;
        TextView QuoteScreenName;
        TextView QuoteText;
        TextView QuoteAtTime;
        LinearLayout QuotePreviewImage;
        ImageView[] ImageQuotePreviewViews = new ImageView[4];
        ImageView QuotePreviewVideoView1;
    }

    protected void showToast(String text){
        if(text == null || text.length() == 0) return;
        runOnUiThread(() -> {
            Toast.makeText(this, text, Toast.LENGTH_SHORT).show();
        });
    }

    /**
     * ツイートの表示部分
     * @param status
     */
    protected void settingItemVIew(Status status){

        vh = iniViewHolder();
        vh.TweetDeletedStatus.setVisibility(View.GONE);
        vh.RTUserIcon.setVisibility(View.GONE);
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

        if(status.getUser().getId() == Variable.userInfo.userID){
            vh.TweetStatus.setVisibility(View.VISIBLE);
            vh.TweetStatus.setBackgroundResource(R.color.Blue);
        }else {
            for (UserMentionEntity entity : status.getUserMentionEntities()) {
                if(!entity.getScreenName().equals(Variable.userInfo.userScreenName))continue;
                vh.TweetStatus.setVisibility(View.VISIBLE);
                vh.TweetStatus.setBackgroundResource(R.color.Rad);
            }
        }
        if(status.isRetweet()){
            vh.TweetStatus.setVisibility(View.VISIBLE);
            vh.TweetStatus.setBackgroundResource(R.color.Green);
            vh.RTUserName.setVisibility(View.VISIBLE);
            vh.RTUserName.setText(status.getUser().getName() + " さんがRT");
            vh.RTUserIcon.setVisibility(View.VISIBLE);
            Glide.with(this).load(status.getUser().getProfileImageURLHttps()).into(vh.RTUserIcon);
            status = status.getRetweetedStatus();
        }

        vh.Name.setText(status.getUser().getName());
        Glide.with(this).load(status.getUser().getProfileImageURLHttps()).into(vh.UserIcon);
        vh.ScreenName.setText("@" + status.getUser().getScreenName());
        vh.TweetText.setText(status.getText());
        replacrTimeAt(new Date(), status.getCreatedAt(), vh.Time);
        vh.Via.setText(status.getSource().replaceAll("<.+?>", "") + " : より");
        vh.RTCount.setText("RT : " + status.getRetweetCount());
        vh.FavCount.setText("Fav : " + status.getFavoriteCount());

        //画像処理
        if(status.getMediaEntities().length != 0){
            vh.PreviewImage.setVisibility(View.VISIBLE);
            setPreviewMedia(status.getMediaEntities(),vh.ImagePreviewViews, vh.PreviewVideoView1);
            vh.TweetText.setText(deleteMediaURL(status.getText(), status.getMediaEntities()));
        }

        //引用ツイート関連
        if(status.getQuotedStatus() != null){
            quoteTweetSetting(status.getQuotedStatus(), vh);
        }

        //鍵垢判定
        if(status.getUser().isProtected()){
            vh.LockedStatus.setVisibility(View.VISIBLE);
        }else{
            vh.LockedStatus.setVisibility(View.GONE);
        }

        //リンク処理
        mutableLinkMovement(vh.TweetText);
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
            timeView.setText(DateFormat.format("yyyy/MM/dd kk:mm:ss", CreatedAt));
        }
    }

    /**
     * メディアのプレビュー表示
     * @param mediaEntity
     * @param imageViews
     */
    protected void setPreviewMedia(MediaEntity[] mediaEntity, ImageView[] imageViews, ImageView videoPlayView){
        for(int i = 0; i < mediaEntity.length; i++){
            imageViews[i].setVisibility(View.VISIBLE);
            if(mediaEntity[i].getType().equals("photo")) {
                videoPlayView.setVisibility(View.GONE);
            }else{
                videoPlayView.setVisibility(View.VISIBLE);
            }
            Glide.with(this)
                    .load(mediaEntity[i].getMediaURLHttps() + ":thumb")
                    .placeholder(R.mipmap.ic_sync_white_24dp)
                    .error(R.mipmap.ic_sync_problem_white_24dp)
                    .dontAnimate()
                    .into(imageViews[i]);

            final int finalI = i;
            imageViews[i].setOnClickListener(view -> {
                if(mediaEntity[finalI].getType().equals("photo")){
                    String imageURL = mediaEntity[finalI].getMediaURLHttps();
                    Intent image = new Intent(this, ShowImageActivity.class);
                    image.putExtra("Images", imageURL);
                    startActivity(image);
                }else{
                    MediaEntity.Variant[] videoURLs = mediaEntity[finalI].getVideoVariants();
                    MediaEntity.Variant videoURL = videoURLs[0];
                    for(MediaEntity.Variant var : videoURLs){
                        if(var.getContentType().equals("mp4") && var.getBitrate() > videoURL.getBitrate()){
                            videoURL = var;
                        }
                    }
                    Intent video = new Intent(this, PlayVideoActivity.class);
                    video.putExtra("Video", videoURL.getUrl());
                    startActivity(video);
                }
            });
        }
    }

    /**
     * メディアURLを消す
     * @param tweet
     * @param mediaEntity
     */
    protected String deleteMediaURL(String tweet, MediaEntity[] mediaEntity){
        for(MediaEntity media : mediaEntity){
            tweet = tweet.replaceAll(media.getURL(), "");
        }
        return tweet;
    }

    /**
     * 引用ツイートの処理
     * @param status
     */
    protected void quoteTweetSetting(Status status, ViewHolder vh){
        vh.QuoteTweetView.setVisibility(View.VISIBLE);
        vh.QuoteName.setText(status.getUser().getName());
        vh.QuoteScreenName.setText("@" + status.getUser().getScreenName());
        vh.QuoteText.setText(status.getText());
        replacrTimeAt(new Date(), status.getCreatedAt(), vh.QuoteAtTime);
        mutableLinkMovement(vh.QuoteText);
        if(status.getMediaEntities().length != 0){
            vh.QuotePreviewImage.setVisibility(View.VISIBLE);
            setPreviewMedia(status.getMediaEntities(),vh.ImageQuotePreviewViews, vh.QuotePreviewVideoView1);
            vh.QuoteText.setText(deleteMediaURL(status.getText(), status.getMediaEntities()));
        }
    }

    /**
     * TextViewのリンク以外のクリックイベントを更に下のViewへ渡す
     * @param TweetText
     */
    protected void mutableLinkMovement(TextView TweetText){
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
     * @return
     */
    protected ViewHolder iniViewHolder(){
        vh = new ViewHolder();

        vh.Name = (TextView) findViewById(R.id.Name);
        vh.UserIcon = (ImageView) findViewById(R.id.UserIcon);
        vh.RTUserIcon = (ImageView) findViewById(R.id.RTUserIcon);
        vh.ScreenName = (TextView) findViewById(R.id.ScreenName);
        vh.TweetText = (TextView) findViewById(R.id.TweetText);
        vh.Via = (TextView) findViewById(R.id.Via);
        vh.RTCount = (TextView) findViewById(R.id.RTCount);
        vh.FavCount = (TextView) findViewById(R.id.FavCount);

        vh.RTUserName = (TextView) findViewById(R.id.RTUserName);

        vh.TweetDeletedStatus = (ImageView) findViewById(R.id.TweetDeletedStatus);
        vh.LockedStatus = (ImageView) findViewById(R.id.LockedStatus);
        vh.TweetStatus = findViewById(R.id.TweetStatus);
        vh.Time = (TextView) findViewById(R.id.Time);

        vh.PreviewImage = (LinearLayout) findViewById(R.id.PreviewImage);
        vh.ImagePreviewViews[0] = (ImageView) findViewById(R.id.PreviewImageView1);
        vh.ImagePreviewViews[1] = (ImageView) findViewById(R.id.PreviewImageView2);
        vh.ImagePreviewViews[2] = (ImageView) findViewById(R.id.PreviewImageView3);
        vh.ImagePreviewViews[3] = (ImageView) findViewById(R.id.PreviewImageView4);
        vh.PreviewVideoView1 = (ImageView) findViewById(R.id.PreviewVideoView1);

        //引用ツイート関連
        vh.QuoteTweetView = (LinearLayout) findViewById(R.id.QuoteTweetView);
        vh.QuoteName = (TextView) findViewById(R.id.QuoteName);
        vh.QuoteScreenName = (TextView) findViewById(R.id.QuoteScreenName);
        vh.QuoteText = (TextView) findViewById(R.id.QuoteText);
        vh.QuoteAtTime = (TextView) findViewById(R.id.QuoteAtTime);
        vh.QuotePreviewImage = (LinearLayout) findViewById(R.id.QuotePreviewImage);
        vh.ImageQuotePreviewViews[0] = (ImageView) findViewById(R.id.QuotePreviewImageView1);
        vh.ImageQuotePreviewViews[1] = (ImageView) findViewById(R.id.QuotePreviewImageView2);
        vh.ImageQuotePreviewViews[2] = (ImageView) findViewById(R.id.QuotePreviewImageView3);
        vh.ImageQuotePreviewViews[3] = (ImageView) findViewById(R.id.QuotePreviewImageView4);
        vh.QuotePreviewVideoView1 = (ImageView) findViewById(R.id.QuotePreviewVideoView1);

        return vh;
    }
}
