package com.meronmks.zimitta.Activity;

import android.Manifest;
import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.Spannable;
import android.text.TextWatcher;
import android.text.format.DateFormat;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.crashlytics.android.answers.Answers;
import com.crashlytics.android.answers.CustomEvent;
import com.jakewharton.rxbinding.view.RxView;
import com.meronmks.zimitta.Core.BaseActivity;
import com.meronmks.zimitta.Core.MutableLinkMovementMethod;
import com.meronmks.zimitta.Datas.ErrorLogs;
import com.meronmks.zimitta.Datas.ParcelStatus;
import com.meronmks.zimitta.Datas.UserSetting;
import com.meronmks.zimitta.Datas.Variable;
import com.meronmks.zimitta.Menus.ItemMenu;
import com.meronmks.zimitta.R;
import com.meronmks.zimitta.TwitterUtil.TwitterAction;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import twitter4j.ExtendedMediaEntity;
import twitter4j.MediaEntity;
import twitter4j.Status;
import twitter4j.StatusUpdate;
import twitter4j.TwitterAdapter;
import twitter4j.TwitterException;
import twitter4j.TwitterListener;
import twitter4j.TwitterMethod;
import twitter4j.UploadedMedia;
import twitter4j.UserMentionEntity;

import static android.R.attr.path;

/**
 * Created by meron on 2016/09/20.
 */
public class MakeTweetActivity extends BaseActivity {

    private TwitterAction mAction;
    private EditText mEditText;
    private Context mContext;
    private Button tweetButton;
    private ImageButton[] imageDelButtons = new ImageButton[4];
    private ImageView[] imageViews = new ImageView[4];
    private String[] filePaths = new String[4];
    private Status status;
    private ViewHolder vh;
    private boolean mentionFlag = false;

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

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContext = this;
        mentionFlag = getIntent().getBooleanExtra("mention", false);
        if(mentionFlag) {
            setContentView(R.layout.activity_make_mention);
            Toolbar toolbar = (Toolbar) findViewById(R.id.ToolBar);
            toolbar.setTitle("返信作成");
            ParcelStatus ps = getIntent().getParcelableExtra("status");
            status = ps.status;
            settingItemVIew(status);
            mEditText = (EditText) findViewById(R.id.TweetTextInput);
            mEditText.setHint("mention to @" + status.getUser().getScreenName());
            mEditText.setText("@" + status.getUser().getScreenName() + " ");
            mEditText.setSelection(mEditText.getText().length());
        }else{
            setContentView(R.layout.activity_make_tweet);
            Toolbar toolbar = (Toolbar) findViewById(R.id.ToolBar);
            toolbar.setTitle("新規ツイート作成");
            mEditText = (EditText) findViewById(R.id.TweetTextInput);
        }
        mAction = new TwitterAction(this, listener);

        tweetButton = (Button)findViewById(R.id.TweetPostButton);

        if(UserSetting.TextCountVisible(getApplicationContext())){
            tweetButton.setText("ツイート\n140");
        }else{
            tweetButton.setText("ツイート");
        }

        mEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if(!UserSetting.TextCountVisible(getApplicationContext()))return;

                String txtLength = Integer.toString(140 - s.length());
                tweetButton.setText("ツイート\n" + txtLength);
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        imageViews[0] = (ImageView) findViewById(R.id.selectImageView1);
        imageViews[1] = (ImageView) findViewById(R.id.selectImageView2);
        imageViews[2] = (ImageView) findViewById(R.id.selectImageView3);
        imageViews[3] = (ImageView) findViewById(R.id.selectImageView4);

        imageDelButtons[0] = (ImageButton) findViewById(R.id.imageDelButton1);
        imageDelButtons[1] = (ImageButton) findViewById(R.id.imageDelButton2);
        imageDelButtons[2] = (ImageButton) findViewById(R.id.imageDelButton3);
        imageDelButtons[3] = (ImageButton) findViewById(R.id.imageDelButton4);

        for(int i = 0; i < 4; i++){
            imageViews[i].setVisibility(View.GONE);
            imageDelButtons[i].setVisibility(View.GONE);
            int finalI = i;
            RxView.clicks(imageDelButtons[i])
                    .subscribe(x ->{
                        imageDelButtons[finalI].setVisibility(View.GONE);
                        filePaths[finalI] = null;
                        imageViews[finalI].setVisibility(View.GONE);
                    });
        }

        RxView.clicks(tweetButton)
                .subscribe(x -> {
                    if(UserSetting.ShowPostDialog(this)){
                        new AlertDialog.Builder(this)
                                .setTitle("確認")
                                .setMessage("ツイートしますか？")
                                .setPositiveButton("はい", (dialog, which) -> PostTweet())
                                .setNegativeButton("いいえ", null)
                                .show();
                    }else {
                        PostTweet();
                    }
                });

        RxView.clicks(findViewById(R.id.AddImageButton))
                .subscribe(x -> {
                    if(ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED){
                        Intent intent = new Intent(Intent.ACTION_PICK);
                        intent.setType("image/*");
                        startActivityForResult(intent, Variable.REQUEST_PICK_CONTENT);
                    }else {
                        ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.READ_EXTERNAL_STORAGE}, 0);
                    }
                });
    }

    /**
     * ツイートを送信する
     */
    private void PostTweet(){
        if(mEditText.getText().length() < 0)return;
        StatusUpdate statusUpdate = new StatusUpdate(mEditText.getText().toString());
        List<Long> media = new ArrayList<>();
        boolean[] imageFlags = {false,false,false,false};
        for(int i = 0; i < 4; i++){
            if(filePaths[i] == null)continue;
            imageFlags[i] = true;
            int finalI = i;
            AsyncTask<Void, Void, Long> task = new AsyncTask<Void, Void, Long>() {
                @Override
                protected Long doInBackground(Void... params) {
                    try {
                        return mAction.uploadMedia(new File(filePaths[finalI])).getMediaId();
                    } catch (TwitterException e) {
                        e.printStackTrace();
                        showToast("TwitterException");
                        ErrorLogs.putErrorLog("TwitterException", e.getMessage());
                    }
                    return null;
                }

                @Override
                protected void onPostExecute(Long aLong) {
                    super.onPostExecute(aLong);
                    media.add(aLong);
                    imageFlags[finalI] = false;
                }
            };
            task.execute();
        }

        AsyncTask<Void, Void, Void> postTask = new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... params) {
                while(imageFlags[0] || imageFlags[1] || imageFlags[2] || imageFlags[3]){

                }

                long[] mediaIDs = new long[media.size()];
                for(int i = 0; i < media.size(); i++){
                    mediaIDs[i] = media.toArray(new Long[media.size()])[i];
                }
                if(mediaIDs.length != 0) {
                    statusUpdate.setMediaIds(mediaIDs);
                }
                if(mentionFlag){
                    statusUpdate.setInReplyToStatusId(status.getId());
                }
                mAction.statusUpdate(statusUpdate);
                return null;
            }
        };
        postTask.execute();
    }

    /**
     * 呼び出したアクティビティの結果受け取り
     * @param requestCode
     * @param resultCode
     * @param data
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);


        if(requestCode == Variable.REQUEST_PICK_CONTENT){
            if(data == null)return;
            Uri uri = data.getData();
            String[] columns = new String[]{ MediaStore.Images.Media.DATA };
            for(int i = 0; i < 4; i++){
                if(filePaths[i] != null) continue;
                ContentResolver cr = getContentResolver();
                Cursor c = cr.query(uri, columns, null, null, null);
                c.moveToFirst();
                filePaths[i] = c.getString(0);
                try {
                    BitmapFactory.Options options = new BitmapFactory.Options();
                    options.inSampleSize = 4; // 元の1/4サイズでbitmap取得
                    InputStream in = getContentResolver().openInputStream(uri);
                    Bitmap img = BitmapFactory.decodeStream(in, null, options);
                    in.close();
                    imageViews[i].setImageBitmap(img);
                    imageViews[i].setVisibility(View.VISIBLE);
                    imageDelButtons[i].setVisibility(View.VISIBLE);
                    break;
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                    showToast("FileNotFoundException");
                    ErrorLogs.putErrorLog("FileNotFoundException", e.getMessage());
                } catch (IOException e) {
                    e.printStackTrace();
                    showToast("IOException");
                    ErrorLogs.putErrorLog("IOException", e.getMessage());
                }
            }
        }
    }

    /**
     * Listener定義
     */
    private TwitterListener listener = new TwitterAdapter() {

        @Override
        public void updatedStatus(Status status) {
            showToast("投稿しました");
            Answers.getInstance().logCustom(new CustomEvent("Tweet Post"));
            finish();
        }

        @Override
        public void onException(TwitterException te, TwitterMethod method) {
            showToast("投稿に失敗しました");
            ErrorLogs.putErrorLog("投稿に失敗しました", te.getMessage());
        }
    };

    /**
     * ツイートの表示部分
     * @param status
     */
    private void settingItemVIew(Status status){

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
            Glide.with(this)
                    .load(extendedMediaEntity[i].getMediaURLHttps() + ":thumb")
                    .placeholder(R.mipmap.ic_sync_white_24dp)
                    .error(R.mipmap.ic_sync_problem_white_24dp)
                    .dontAnimate()
                    .into(imageViews[i]);

            final int finalI = i;
            imageViews[i].setOnClickListener(view -> {
                if(extendedMediaEntity[finalI].getType().equals("photo")){
                    String imageURL = extendedMediaEntity[finalI].getMediaURLHttps();
                    Intent image = new Intent(this, ShowImageActivity.class);
                    image.putExtra("Images", imageURL);
                    startActivity(image);
                }else{
                    ExtendedMediaEntity.Variant[] videoURLs = extendedMediaEntity[finalI].getVideoVariants();
                    ExtendedMediaEntity.Variant videoURL = videoURLs[0];
                    for(ExtendedMediaEntity.Variant var : videoURLs){
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
     * @return
     */
    private ViewHolder iniViewHolder(){
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
