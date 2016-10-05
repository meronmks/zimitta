package com.meronmks.zimitta.Menus;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.text.Spannable;
import android.text.format.DateFormat;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.meronmks.zimitta.Activity.PlayVideoActivity;
import com.meronmks.zimitta.Activity.ShowImageActivity;
import com.meronmks.zimitta.Adapter.BaseAdapter;
import com.meronmks.zimitta.Core.MutableLinkMovementMethod;
import com.meronmks.zimitta.Datas.ErrorLogs;
import com.meronmks.zimitta.Datas.Variable;
import com.meronmks.zimitta.R;
import com.meronmks.zimitta.Settings.SettingsActivity;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import twitter4j.ExtendedMediaEntity;
import twitter4j.MediaEntity;
import twitter4j.Status;
import twitter4j.UserMentionEntity;

/**
 * Created by p-user on 2016/10/03.
 */

public class ItemMenu implements AdapterView.OnItemClickListener {

    private Activity activity;
    private ArrayAdapter<String> adapter;
    private AlertDialog alertDialog;
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

    public ItemMenu(Activity activity){
        this.activity = activity;
    }

    public void show(Status status){
        View view = activity.getLayoutInflater().inflate(R.layout.list_item_dialog, null);

        settingItemVIew(status, view);

        ListView listView = (ListView) view.findViewById(R.id.listItemMenu);
        listView.setOnItemClickListener(this);
        String[] members = makeItemMenu(status);
        adapter = new ArrayAdapter<>(activity.getBaseContext(), android.R.layout.simple_expandable_list_item_1, members);
        listView.setAdapter(adapter);
        alertDialog = new AlertDialog.Builder(activity)
                .setView(view)
                .show();
    }

    /**
     * 動的にメニュー内容生成
     * @param status
     * @return
     */
    @NonNull
    private String[] makeItemMenu(Status status){
        List<String> menuItem = new ArrayList<>();
        menuItem.add("詳細");
        menuItem.add("返信");
        menuItem.add("リツイート");
        menuItem.add("お気に入り");
        menuItem.add("お気に入り+リツイート");
        menuItem.add("@" + status.getUser().getScreenName());
        if(status.getRetweetedStatus() != null){
            menuItem.add("@" + status.getRetweetedStatus().getUser().getScreenName());
            status = status.getRetweetedStatus();
        }
        for (UserMentionEntity entity : status.getUserMentionEntities()) {
            menuItem.add("@" + entity.getScreenName());
        }
        menuItem.add("共有");
        return menuItem.toArray(new String[menuItem.size()]);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        switch (parent.getItemAtPosition(position).toString()){
            case "詳細":
                break;
            case "返信":
                break;
            case "リツイート":
                break;
            case "お気に入り":
                break;
            case "お気に入り+リツイート":
                break;
            case "共有":
                break;
            default:    //IDに対する処理
                break;
        }
    }

    /**
     * ツイートの表示部分
     * @param status
     * @param cv
     */
    private void settingItemVIew(Status status, View cv){

        vh = iniViewHolder(cv);
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
                if(!entity.getScreenName().equals(Variable.userInfo.userName))continue;
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
            Glide.with(activity).load(status.getUser().getProfileImageURLHttps()).into(vh.RTUserIcon);
            status = status.getRetweetedStatus();
        }

        vh.Name.setText(status.getUser().getName());
        Glide.with(activity).load(status.getUser().getProfileImageURLHttps()).into(vh.UserIcon);
        vh.ScreenName.setText("@" + status.getUser().getScreenName());
        vh.TweetText.setText(status.getText());
        replacrTimeAt(new Date(), status.getCreatedAt(), vh.Time);
        vh.Via.setText(status.getSource().replaceAll("<.+?>", "") + " : より");
        vh.RTCount.setText("RT : " + status.getRetweetCount());
        vh.FavCount.setText("Fav : " + status.getFavoriteCount());

        //画像処理
        if(status.getExtendedMediaEntities().length != 0){
            vh.PreviewImage.setVisibility(View.VISIBLE);
            setPreviewMedia(status.getExtendedMediaEntities(),vh.ImagePreviewViews, vh.PreviewVideoView1);
            vh.TweetText.setText(deleteMediaURL(status.getText(), status.getExtendedMediaEntities()));
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
     * @param extendedMediaEntity
     * @param imageViews
     */
    protected void setPreviewMedia(ExtendedMediaEntity[] extendedMediaEntity, ImageView[] imageViews, ImageView videoPlayView){
        for(int i = 0; i < extendedMediaEntity.length; i++){
            imageViews[i].setVisibility(View.VISIBLE);
            if(extendedMediaEntity[i].getType().equals("photo")) {
                videoPlayView.setVisibility(View.GONE);
            }else{
                videoPlayView.setVisibility(View.VISIBLE);
            }
            Glide.with(activity)
                    .load(extendedMediaEntity[i].getMediaURLHttps() + ":thumb")
                    .placeholder(R.mipmap.ic_sync_white_24dp)
                    .error(R.mipmap.ic_sync_problem_white_24dp)
                    .dontAnimate()
                    .into(imageViews[i]);

            final int finalI = i;
            imageViews[i].setOnClickListener(view -> {
                if(extendedMediaEntity[finalI].getType().equals("photo")){
                    String imageURL = extendedMediaEntity[finalI].getMediaURLHttps();
                    Intent image = new Intent(activity, ShowImageActivity.class);
                    image.putExtra("Images", imageURL);
                    activity.startActivity(image);
                }else{
                    ExtendedMediaEntity.Variant[] videoURLs = extendedMediaEntity[finalI].getVideoVariants();
                    ExtendedMediaEntity.Variant videoURL = videoURLs[0];
                    for(ExtendedMediaEntity.Variant var : videoURLs){
                        if(var.getContentType().equals("mp4") && var.getBitrate() > videoURL.getBitrate()){
                            videoURL = var;
                        }
                    }
                    Intent video = new Intent(activity, PlayVideoActivity.class);
                    video.putExtra("Video", videoURL.getUrl());
                    activity.startActivity(video);
                }
            });
        }
    }

    /**
     * メディアURLを消す
     * @param tweet
     * @param extendedMediaEntity
     */
    protected String deleteMediaURL(String tweet, ExtendedMediaEntity[] extendedMediaEntity){
        for(MediaEntity media : extendedMediaEntity){
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
        if(status.getExtendedMediaEntities().length != 0){
            vh.QuotePreviewImage.setVisibility(View.VISIBLE);
            setPreviewMedia(status.getExtendedMediaEntities(),vh.ImageQuotePreviewViews, vh.QuotePreviewVideoView1);
            vh.QuoteText.setText(deleteMediaURL(status.getText(), status.getExtendedMediaEntities()));
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
     * @param cv
     * @return
     */
    private ViewHolder iniViewHolder(View cv){
        vh = new ViewHolder();

        vh.Name = (TextView) cv.findViewById(R.id.Name);
        vh.UserIcon = (ImageView) cv.findViewById(R.id.UserIcon);
        vh.RTUserIcon = (ImageView) cv.findViewById(R.id.RTUserIcon);
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
}
