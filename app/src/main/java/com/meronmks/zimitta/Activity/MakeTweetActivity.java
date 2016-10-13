package com.meronmks.zimitta.Activity;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.SpannableStringBuilder;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;

import com.crashlytics.android.answers.Answers;
import com.crashlytics.android.answers.CustomEvent;
import com.jakewharton.rxbinding.view.RxView;
import com.meronmks.zimitta.Core.BaseActivity;
import com.meronmks.zimitta.Core.MainActivity;
import com.meronmks.zimitta.Datas.ErrorLogs;
import com.meronmks.zimitta.Datas.UserSetting;
import com.meronmks.zimitta.Datas.Variable;
import com.meronmks.zimitta.R;
import com.meronmks.zimitta.TwitterUtil.TwitterAction;

import java.util.Timer;

import twitter4j.ResponseList;
import twitter4j.Status;
import twitter4j.StatusUpdate;
import twitter4j.TwitterAdapter;
import twitter4j.TwitterException;
import twitter4j.TwitterListener;
import twitter4j.TwitterMethod;

/**
 * Created by meron on 2016/09/20.
 */
public class MakeTweetActivity extends BaseActivity {

    private TwitterAction mAction;
    private EditText mEditText;
    private Context mContext;
    private Button tweetButton;
    private ImageButton[] imageClearButton = new ImageButton[4];

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

        RxView.clicks(tweetButton)
                .subscribe(x -> {
                    if(mEditText.getText().length() < 0)return;
                    mAction.statusUpdate(new StatusUpdate(mEditText.getText().toString()));
                });

        RxView.clicks(findViewById(R.id.AddImageButton))
                .subscribe(x -> {
                    if(ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED){
                        Intent intent = new Intent(Intent.ACTION_PICK);
                        intent.setType("image/*");
                        startActivityForResult(intent, 1);
                    }else {
                        ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.READ_EXTERNAL_STORAGE}, 0);
                    }
                });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode != RESULT_OK && (requestCode == Variable.REQUEST_PICK_CONTENT || requestCode == Variable.REQUEST_KITKAT_PICK_CONTENT)) return;


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
