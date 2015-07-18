package com.meronmks.zimitta.user;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.PaintDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import com.bumptech.glide.Glide;
import com.meronmks.zimitta.Activity.*;
import com.meronmks.zimitta.R;
import com.meronmks.zimitta.core.MainActivity;
import com.meronmks.zimitta.core.TwitterActionClass;
import com.meronmks.zimitta.core.TwitterUtils;
import com.meronmks.zimitta.menu.List_Menu;
import twitter4j.User;


/**
 * Created by meronmks on 2015/03/25.
 */
public class Prof_Activity extends ActionBarActivity implements View.OnClickListener {
    private TextView ScreenName;
    private TextView UserName;
    private TextView ProfText;
    private TextView Location;
    private TextView URLText;
    private TextView UserStaticID;
    private Button Tweet;
    private Button Fav;
    private Button Following;
    private Button Follwers;
    private Button Follow;
    private Button Menu;
    private ImageView icon;
    private User user;
    private long Userid;
    private  SharedPreferences accountIDCount;
    private TwitterActionClass mtAction;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_prof);

        //ActionBarからアイコンを消す
        getSupportActionBar().setDisplayShowHomeEnabled(false);
        PaintDrawable paintDrawable = new PaintDrawable(Color.argb(255, 0, 0, 0));
        getWindow().setBackgroundDrawable(paintDrawable);
        MainActivity.showProcessDialog();
        ScreenName = (TextView)this.findViewById(R.id.NameView);
        UserName = (TextView)this.findViewById(R.id.ScreenNameView);
        ProfText = (TextView)this.findViewById(R.id.ProfTextView);
        icon = (ImageView) this.findViewById(R.id.icon);
        Location = (TextView)this.findViewById(R.id.LocView);
        URLText = (TextView)this.findViewById(R.id.URLTextView);
        Tweet = (Button)this.findViewById(R.id.TweetCountView);
        Fav = (Button)this.findViewById(R.id.FavCountView);
        Following = (Button)this.findViewById(R.id.FollowingCountView);
        Follwers = (Button)this.findViewById(R.id.FollwersCountView);
        Follow = (Button)this.findViewById(R.id.followButton);
        Menu = (Button)this.findViewById(R.id.MenuButton);
        UserStaticID = (TextView)this.findViewById(R.id.UserStaticID);
        //ボタンのクリックリスナの作成
        Tweet.setOnClickListener(this);
        Fav.setOnClickListener(this);
        Following.setOnClickListener(this);
        Follwers.setOnClickListener(this);
        Follow.setOnClickListener(this);
        Menu.setOnClickListener(this);

        accountIDCount = getSharedPreferences("accountidcount", 0);
        if (!TwitterUtils.hasAccessToken(this, accountIDCount.getLong("ID_Num_Now", 0))) {
            Intent intent = new Intent(this, TwitterOAuthActivity.class);
            startActivity(intent);
            finish();
        }else {
            mtAction = new TwitterActionClass(this);
            Intent intent = getIntent();
            Userid = intent.getLongExtra("UserID", BIND_ABOVE_CLIENT);
            Show_Prof();
        }

    }

    /**
     * ボタンのクリック処理
     * @param v
     */
    public void onClick(View v){
        switch(v.getId()){
            case R.id.TweetCountView:
                Intent TweetCountView = new Intent(Prof_Activity.this, UserTimeLineActivity.class);
                TweetCountView.putExtra("UserID_TL", Userid);
                TweetCountView.putExtra("ScreenName", ScreenName.getText());
                startActivity(TweetCountView);
                break;
            case R.id.FavCountView:
                Intent FavCountView = new Intent(Prof_Activity.this, UserFavActivity.class);
                FavCountView.putExtra("UserID_Fav", Userid);
                FavCountView.putExtra("ScreenName", ScreenName.getText());
                startActivity(FavCountView);
                break;
            case R.id.FollowingCountView:
                Intent FollowingCountView = new Intent(Prof_Activity.this, UserFollowActivity.class);
                FollowingCountView.putExtra("UserID_TL", Userid);
                FollowingCountView.putExtra("ScreenName", ScreenName.getText());
                startActivity(FollowingCountView);
                break;
            case R.id.FollwersCountView:
                Intent FollwersCountView = new Intent(Prof_Activity.this, UserFollowersActivity.class);
                FollwersCountView.putExtra("UserID_TL", Userid);
                FollwersCountView.putExtra("ScreenName", ScreenName.getText());
                startActivity(FollwersCountView);
                break;
            case R.id.followButton:
                if(Follow.getText().equals("フォロー")) {
                    mtAction.userFollow(Userid);
                }else if(Follow.getText().equals("フォロー済み")){
                    mtAction.userUnduFollow(Userid);
                }
                break;
            case R.id.MenuButton:
                List_Menu list = new List_Menu();
                list.Prof_Menu(this,mtAction.getTwitter(),user);
                break;
        }
    }

    /**
     * ユーザ詳細表示
     */
    private void Show_Prof(){
        AsyncTask<Void, Void, User> task = new AsyncTask<Void, Void, User>() {

            @Override
            protected User doInBackground(Void... params) {
                return mtAction.show_Prof(Userid);
            }

            @Override
            protected void onPostExecute(User result) {
                MainActivity.dismissProcessDialog();
                if(result != null){
                    ScreenName.setText("@" + result.getScreenName());
                    UserName.setText(result.getName());
                    String str = result.getDescription();
                    StringBuffer sb = new StringBuffer();
                    char[] chr = str.toCharArray();
                    for(int p=0;p<chr.length;p++) {
                        if( chr[p] == ' ' || chr[p] =='　') {
                            continue;
                        }
                        sb.append(chr[p]);
                    }
                    ProfText.setText(sb.toString());
                    Glide.with(Prof_Activity.this).load(result.getProfileImageURL()).into(icon);
                    Location.setText(result.getLocation());
                    URLText.setText(result.getURLEntity().getDisplayURL());
                    Tweet.setText("ツイート\r\n" + result.getStatusesCount());
                    Fav.setText("お気に入り\r\n" + result.getFavouritesCount());
                    Following.setText("フォロー\r\n" + result.getFriendsCount());
                    Follwers.setText("フォロワー\r\n" + result.getFollowersCount());
                    UserStaticID.setText("固有ID：" + result.getId());
                    mtAction.chackFollow(result, Follow);
                    user = result;
                }else{
                    Toast.makeText(Prof_Activity.this, "取得失敗・・・", Toast.LENGTH_SHORT).show();
                }
            }
        };
        task.execute();
    }
}
