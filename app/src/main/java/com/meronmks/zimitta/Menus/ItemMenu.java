package com.meronmks.zimitta.Menus;

import android.app.Activity;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.format.DateFormat;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.meronmks.zimitta.Activity.MakeTweetActivity;
import com.meronmks.zimitta.Activity.PlayVideoActivity;
import com.meronmks.zimitta.Activity.ShowImageActivity;
import com.meronmks.zimitta.Core.HashTagClickable;
import com.meronmks.zimitta.Core.MutableLinkMovementMethod;
import com.meronmks.zimitta.Core.StaticMethods;
import com.meronmks.zimitta.Core.UserIDClickable;
import com.meronmks.zimitta.Datas.ErrorLogs;
import com.meronmks.zimitta.Datas.ParcelStatus;
import com.meronmks.zimitta.Datas.UserSetting;
import com.meronmks.zimitta.Datas.Variable;
import com.meronmks.zimitta.R;
import com.meronmks.zimitta.TwitterUtil.TwitterAction;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import twitter4j.ExtendedMediaEntity;
import twitter4j.HashtagEntity;
import twitter4j.MediaEntity;
import twitter4j.Status;
import twitter4j.TwitterAdapter;
import twitter4j.TwitterException;
import twitter4j.TwitterListener;
import twitter4j.TwitterMethod;
import twitter4j.UserMentionEntity;

/**
 * Created by p-user on 2016/10/03.
 */

public class ItemMenu implements AdapterView.OnItemClickListener {

    private Activity activity;
    private ArrayAdapter<String> adapter;
    private AlertDialog alertDialog;
    private ViewHolder vh;
    private TwitterAction mAction;
    private Status status;

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

    private static final Pattern ID_MATCH_PATTERN = Pattern.compile("@[a-zA-Z0-9_]+", Pattern.CASE_INSENSITIVE);
    private static final Pattern HASH_TAG_MATCH_PATTERN = Pattern.compile("[#＃][Ａ-Ｚａ-ｚA-Za-z一-鿆0-9０-９ぁ-ヶｦ-ﾟー]+", Pattern.CASE_INSENSITIVE);


    public ItemMenu(Activity activity){
        this.activity = activity;
        mAction = new TwitterAction(activity.getApplicationContext(), listener);
    }

    public void show(Status status){
        View view = activity.getLayoutInflater().inflate(R.layout.list_item_dialog, null);
        this.status = status;
        settingItemVIew(status, view);

        ListView listView = (ListView) view.findViewById(R.id.listItemMenu);
        listView.setOnItemClickListener(this);
        String[] members = makeItemMenu();
        adapter = new ArrayAdapter<>(activity.getBaseContext(), android.R.layout.simple_expandable_list_item_1, members);
        listView.setAdapter(adapter);
        alertDialog = new AlertDialog.Builder(activity)
                .setView(view)
                .show();
    }

    /**
     * 動的にメニュー内容生成
     * @return
     */
    @NonNull
    private String[] makeItemMenu(){
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
        for(HashtagEntity entity : status.getHashtagEntities()){
            menuItem.add("#" + entity.getText());
        }
        if(Variable.userInfo.userID == status.getUser().getId()){
            menuItem.add("削除");
        }
        return menuItem.toArray(new String[menuItem.size()]);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        switch (parent.getItemAtPosition(position).toString()){
            case "詳細":
                break;
            case "返信":
                Intent intent = new Intent(activity, MakeTweetActivity.class);
                intent.putExtra("mention", true);
                ParcelStatus ps = new ParcelStatus();
                ps.status = status;
                intent.putExtra("status", ps);
                activity.startActivity(intent);
                break;
            case "リツイート":
                if(UserSetting.ShowRTDialog(activity)){
                    new AlertDialog.Builder(activity)
                            .setTitle("確認")
                            .setMessage("リツイートをしますか？")
                            .setPositiveButton("はい", (dialog, which) -> mAction.retweetStatus(status.getId()))
                            .setNegativeButton("いいえ", null)
                            .show();
                }else{
                    mAction.retweetStatus(status.getId());
                }
                break;
            case "お気に入り":
                if(UserSetting.ShowFavDialog(activity)){
                    new AlertDialog.Builder(activity)
                            .setTitle("確認")
                            .setMessage("お気に入りをしますか？")
                            .setPositiveButton("はい", (dialog, which) -> mAction.createFavorite(status.getId()))
                            .setNegativeButton("いいえ", null)
                            .show();
                }else{
                    mAction.createFavorite(status.getId());
                }
                break;
            case "お気に入り+リツイート":
                if(UserSetting.ShowFavRTDialog(activity)){
                    new AlertDialog.Builder(activity)
                            .setTitle("確認")
                            .setMessage("お気に入り+リツイートをしますか？")
                            .setPositiveButton("はい", (dialog, which) -> {
                                mAction.retweetStatus(status.getId());
                                mAction.createFavorite(status.getId());
                            })
                            .setNegativeButton("いいえ", null)
                            .show();
                }else{
                    mAction.retweetStatus(status.getId());
                    mAction.createFavorite(status.getId());
                }
                break;
            case "共有":
                break;
            case "削除":
                if(UserSetting.ShowTweetDelDialog(activity)) {
                    new AlertDialog.Builder(activity)
                            .setTitle("確認")
                            .setMessage("ツイートの削除をしますか？")
                            .setPositiveButton("はい", (dialog, which) -> mAction.destroyStatus(status.getId()))
                            .setNegativeButton("いいえ", null)
                            .show();
                }else {
                    mAction.destroyStatus(status.getId());
                }
                break;
            default:    //IDとハッシュタグに対する処理
                break;
        }
        alertDialog.dismiss();
    }

    /**
     * Listener定義
     */
    private TwitterListener listener = new TwitterAdapter() {

        @Override
        public void retweetedStatus(Status retweetedStatus) {
            showToast("リツイートしました");
        }

        @Override
        public void createdFavorite(Status status) {
            showToast("お気に入りしました");
        }

        @Override
        public void destroyedFavorite(Status status) {
            super.destroyedFavorite(status);
            showToast("お気に入りを解除しました");
        }

        @Override
        public void destroyedStatus(Status destroyedStatus) {
            super.destroyedStatus(destroyedStatus);
            showToast("ツイートの削除完了");
        }

        @Override
        public void onException(TwitterException te, TwitterMethod method) {
            activity.runOnUiThread(() -> {
                switch (method){
                    case RETWEET_STATUS:
                        showToast("リツイートに失敗しました");
                        ErrorLogs.putErrorLog("リツイートに失敗しました", te.getMessage());
                        break;
                    case CREATE_FAVORITE:
                        showToast("お気に入りに失敗しました");
                        ErrorLogs.putErrorLog("お気に入りに失敗しました", te.getMessage());
                        break;
                    case DESTROY_STATUS:
                        showToast("ツイートの削除に失敗しました");
                        ErrorLogs.putErrorLog("ツイートの削除に失敗しました", te.getMessage());
                        break;
                    case DESTROY_FAVORITE:
                        showToast("お気に入りの解除に失敗しました");
                        ErrorLogs.putErrorLog("お気に入りの解除に失敗しました", te.getMessage());
                        break;
                }
            });
        }
    };

    private void showToast(String text){
        if(text == null || text.length() == 0) return;
        activity.runOnUiThread(() -> {
            Toast.makeText(activity.getApplicationContext(), text, Toast.LENGTH_SHORT).show();
        });
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
        vh.TweetText.setText(mutableIDandHashTagMobement(status.getText()));
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
        StaticMethods.replacrTimeAt(TimeStatusNow, CreatedAt, timeView);
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
