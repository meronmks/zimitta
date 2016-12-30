package com.meronmks.zimitta.Adapter;

/**
 * Created by meron on 2016/09/14.
 */
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.format.DateFormat;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.meronmks.zimitta.Activity.PlayVideoActivity;
import com.meronmks.zimitta.Activity.ShowImageActivity;
import com.meronmks.zimitta.Core.HashTagClickable;
import com.meronmks.zimitta.Core.MutableLinkMovementMethod;
import com.meronmks.zimitta.Core.StaticMethods;
import com.meronmks.zimitta.Core.UserIDClickable;
import com.meronmks.zimitta.Core.ViewHolder;
import com.meronmks.zimitta.R;

import java.util.Calendar;
import java.util.Date;
import java.util.HashSet;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import twitter4j.MediaEntity;
import twitter4j.ResponseList;
import twitter4j.Status;

import static com.meronmks.zimitta.Core.StaticMethods.replacrTimeAt;


public class BaseAdapter<T> extends ArrayAdapter<T> {

    private static final Pattern ID_MATCH_PATTERN = Pattern.compile("@[a-zA-Z0-9_]+", Pattern.CASE_INSENSITIVE);
    private static final Pattern HASH_TAG_MATCH_PATTERN = Pattern.compile("[#＃][Ａ-Ｚａ-ｚA-Za-z一-鿆0-9０-９ぁ-ヶｦ-ﾟー]+", Pattern.CASE_INSENSITIVE);

    public BaseAdapter(Context context, int resources) {
        super(context, resources);
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
            Glide.with(getContext())
                    .load(mediaEntity[i].getMediaURLHttps() + ":thumb")
                    .placeholder(R.mipmap.ic_sync_white_24dp)
                    .error(R.mipmap.ic_sync_problem_white_24dp)
                    .dontAnimate()
                    .into(imageViews[i]);

            final int finalI = i;
            imageViews[i].setOnClickListener(view -> {
                if(mediaEntity[finalI].getType().equals("photo")){
                    String imageURL = mediaEntity[finalI].getMediaURLHttps();
                    Intent image = new Intent(getContext(), ShowImageActivity.class);
                    image.putExtra("Images", imageURL);
                    getContext().startActivity(image);
                }else{
                    MediaEntity.Variant[] videoURLs = mediaEntity[finalI].getVideoVariants();
                    MediaEntity.Variant videoURL = videoURLs[0];
                    for(MediaEntity.Variant var : videoURLs){
                        if(var.getContentType().equals("mp4") && var.getBitrate() > videoURL.getBitrate()){
                            videoURL = var;
                        }
                    }
                    Intent video = new Intent(getContext(), PlayVideoActivity.class);
                    video.putExtra("Video", videoURL.getUrl());
                    getContext().startActivity(video);
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
        String text = status.getText();
        if(status.getMediaEntities().length != 0){
            vh.QuotePreviewImage.setVisibility(View.VISIBLE);
            setPreviewMedia(status.getMediaEntities(),vh.ImageQuotePreviewViews, vh.QuotePreviewVideoView1);
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
     * テキストからIDとハッシュタグを抽出してクリック可能に
     * @param string
     * @return
     */
    protected SpannableString mutableIDandHashTagMobement(String string){
        SpannableString spannable = new SpannableString(string);
        Matcher matcher = ID_MATCH_PATTERN.matcher(string);
        while (matcher.find()){
            UserIDClickable span = new UserIDClickable();
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
