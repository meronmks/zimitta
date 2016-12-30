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
import com.meronmks.zimitta.Activity.PlayVideoActivity;
import com.meronmks.zimitta.Activity.ShowImageActivity;
import com.meronmks.zimitta.Datas.Variable;
import com.meronmks.zimitta.R;

import java.util.Calendar;
import java.util.Date;

import twitter4j.MediaEntity;
import twitter4j.Status;
import twitter4j.UserMentionEntity;

import static com.meronmks.zimitta.Core.StaticMethods.deleteMediaURL;
import static com.meronmks.zimitta.Core.StaticMethods.quoteTweetSetting;
import static com.meronmks.zimitta.Core.StaticMethods.replacrTimeAt;
import static com.meronmks.zimitta.Core.StaticMethods.setPreviewMedia;

/**
 *
 * Created by meron on 2016/09/20.
 */
public class BaseActivity extends AppCompatActivity {

    private ViewHolder vh;

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
            setPreviewMedia(status.getMediaEntities(),vh.ImagePreviewViews, vh.PreviewVideoView1, getBaseContext());
            vh.TweetText.setText(deleteMediaURL(status.getText(), status.getMediaEntities()));
        }

        //引用ツイート関連
        if(status.getQuotedStatus() != null){
            quoteTweetSetting(status.getQuotedStatus(), vh, getBaseContext());
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
