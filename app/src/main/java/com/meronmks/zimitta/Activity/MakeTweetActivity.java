package com.meronmks.zimitta.Activity;

import android.Manifest;
import android.content.ContentResolver;
import android.content.Context;
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
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;

import com.crashlytics.android.answers.Answers;
import com.crashlytics.android.answers.CustomEvent;
import com.jakewharton.rxbinding.view.RxView;
import com.meronmks.zimitta.Core.BaseActivity;
import com.meronmks.zimitta.Datas.ErrorLogs;
import com.meronmks.zimitta.Datas.UserSetting;
import com.meronmks.zimitta.Datas.Variable;
import com.meronmks.zimitta.R;
import com.meronmks.zimitta.TwitterUtil.TwitterAction;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import twitter4j.Status;
import twitter4j.StatusUpdate;
import twitter4j.TwitterAdapter;
import twitter4j.TwitterException;
import twitter4j.TwitterListener;
import twitter4j.TwitterMethod;
import twitter4j.UploadedMedia;

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

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContext = this;
        setContentView(R.layout.activity_make_tweet);
        mAction = new TwitterAction(this, listener);

        Toolbar toolbar = (Toolbar) findViewById(R.id.ToolBar);
        toolbar.setTitle("新規ツイート作成");

        mEditText = (EditText) findViewById(R.id.TweetTextInput);

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
                            mAction.statusUpdate(statusUpdate);
                            return null;
                        }
                    };
                    postTask.execute();
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
     * 呼び出したアクティビティの結果受け取り
     * @param requestCode
     * @param resultCode
     * @param data
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        Uri uri = data.getData();
        if(requestCode == Variable.REQUEST_PICK_CONTENT){
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
}
