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
import android.widget.ProgressBar;
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
    private ImageButton[] imageDelButtons = new ImageButton[4];
    private ImageView[] imageViews = new ImageView[4];
    private String[] filePaths = new String[4];
    private Status status;
    private boolean mentionFlag = false;
    private ProgressBar progressBar;
    private Toolbar toolbar;
    private String toolBarTitle;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mentionFlag = getIntent().getBooleanExtra("mention", false);
        if(mentionFlag) {
            setContentView(R.layout.activity_make_mention);
            ParcelStatus ps = getIntent().getParcelableExtra("status");
            status = ps.status;
            settingItemVIew(status);
            mEditText = (EditText) findViewById(R.id.TweetTextInput);
            mEditText.setHint("mention to @" + status.getUser().getScreenName());
            mEditText.setText("@" + status.getUser().getScreenName() + " ");
            mEditText.setSelection(mEditText.getText().length());
            toolBarTitle = "返信（@" + status.getUser().getScreenName() + "）";
        }else{
            setContentView(R.layout.activity_make_tweet);
            mEditText = (EditText) findViewById(R.id.TweetTextInput);
            toolBarTitle = "ツイート作成";
        }
        toolbar = (Toolbar) findViewById(R.id.ToolBar);
        toolbar.setTitle(toolBarTitle);
        mAction = new TwitterAction(this, listener);

        Button tweetButton = (Button) findViewById(R.id.TweetPostButton);

        if(UserSetting.TextCountVisible(getApplicationContext())){
            String txtLength = Integer.toString(140 - mEditText.length());
            toolbar.setTitle(toolBarTitle + "：" + txtLength);
        }

        progressBar = (ProgressBar) findViewById(R.id.postProgressBar);
        progressBar.setVisibility(View.GONE);

        mEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if(!UserSetting.TextCountVisible(getApplicationContext()))return;

                String txtLength = Integer.toString(140 - s.length());
                toolbar.setTitle(toolBarTitle + "：" + txtLength);
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
                                .setMessage("ツイートをしますか？")
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
        progressBar.setVisibility(View.VISIBLE);
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

                //画像の投稿が終わるまで待つ
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
            progressBar.setVisibility(View.GONE);
            showToast("投稿に失敗しました");
            ErrorLogs.putErrorLog("投稿に失敗しました", te.getMessage());
        }
    };
}
