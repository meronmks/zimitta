package com.meronmks.zimitta.Activity;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.meronmks.zimitta.Core.BaseActivity;
import com.meronmks.zimitta.Datas.ErrorLogs;
import com.meronmks.zimitta.R;
import com.meronmks.zimitta.TwitterUtil.TwitterAction;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import twitter4j.TwitterAdapter;
import twitter4j.TwitterException;
import twitter4j.TwitterListener;
import twitter4j.TwitterMethod;
import twitter4j.User;

import static com.meronmks.zimitta.Core.StaticMethods.mutableIDandHashTagMobement;

/**
 * Created by meron on 2016/12/30.
 * ユーザの詳細（プロフィール）画面
 */

public class UserDetailActivity extends BaseActivity{
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_detail);
        mAction = new TwitterAction(this, listener);
        String userName = getIntent().getStringExtra("userName");
        Pattern p = Pattern.compile("@");
        Matcher m = p.matcher(userName);
        mAction.showUser(m.replaceAll(""));
    }

    private void showUserDetail(User user){
        ImageView userIcon = (ImageView) findViewById(R.id.UserIcon);
        Glide.with(this).load(user.getProfileImageURLHttps()).into(userIcon);
        ImageView headerImage = (ImageView) findViewById(R.id.HeaderImage);
        Glide.with(this).load(user.getProfileBannerURL()).into(headerImage);
        TextView name = (TextView) findViewById(R.id.Name);
        name.setText(user.getName());
        TextView screenName = (TextView) findViewById(R.id.ScreenName);
        screenName.setText(user.getScreenName());
        Button  tweetCountButton = (Button) findViewById(R.id.TweetCountButton);
        tweetCountButton.setText(Integer.toString(user.getStatusesCount()) + "\nツイート");
        Button  followCountButton = (Button) findViewById(R.id.FollowCountButton);
        followCountButton.setText(Integer.toString(user.getFriendsCount()) + "\nフォロー");
        Button  followerCountButton = (Button) findViewById(R.id.FollowerCountButton);
        followerCountButton.setText(Integer.toString(user.getFollowersCount()) + "\nフォロワー");
        Button  favCountButton = (Button) findViewById(R.id.FavCountButton);
        favCountButton.setText(Integer.toString(user.getFavouritesCount()) + "\nお気に入り");
        TextView userVio = (TextView) findViewById(R.id.UserVioText);
        userVio.setText(mutableIDandHashTagMobement(user.getDescription()));
    }

    /**
     * Listener定義
     */
    private TwitterListener listener = new TwitterAdapter() {
        @Override
        public void gotUserDetail(User user) {
            super.gotUserDetail(user);
            runOnUiThread(() -> showUserDetail(user));
        }

        @Override
        public void onException(TwitterException te, TwitterMethod method) {
            runOnUiThread(() -> {
                switch (method){
                    case SHOW_USER:
                        showToast("ユーザ情報の取得に失敗しました。");
                        ErrorLogs.putErrorLog("ユーザ情報の取得に失敗しました", te.getMessage());
                        break;
                }
            });
        }
    };
}
