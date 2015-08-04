package com.meronmks.zimitta.Activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import com.bumptech.glide.Glide;
import com.meronmks.zimitta.Adapter.TweetAdapter;
import com.meronmks.zimitta.R;
import com.meronmks.zimitta.core.TwitterActionClass;
import com.meronmks.zimitta.menu.List_Menu;
import twitter4j.Status;
import twitter4j.UserMentionEntity;

import java.text.DateFormat;

public class TweetDetail extends Activity implements OnClickListener {

	private Status Tweet;
    private TwitterActionClass mtAction;
	private TweetAdapter mAdapter;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.tweet_detail);
		//ListViewの取得
		ListView lv = (ListView)findViewById(R.id.mTMListView);
		//アダプタの使用準備
		mAdapter = new TweetAdapter(this);
		lv.setAdapter(mAdapter);
        Intent intent = getIntent();
        Tweet = (Status)intent.getSerializableExtra("Detail");
        mtAction = new TwitterActionClass(this,mAdapter,null,"",null);

        //ボタン設定
  		ImageButton mention_button = (ImageButton)findViewById(R.id.mentionimageButton);
  		ImageButton rt_button = (ImageButton)findViewById(R.id.retweetImageButton);
  		ImageButton fav_button = (ImageButton)findViewById(R.id.favImageButton);
  		ImageButton menu_button = (ImageButton)findViewById(R.id.menuImageButton);

  		//ボタンのクリックリスナの作成
  		mention_button.setOnClickListener(this);
  		rt_button.setOnClickListener(this);
  		fav_button.setOnClickListener(this);
  		menu_button.setOnClickListener(this);

  		//テキスト等の設定変更準備
  		ImageView icon = (ImageView)findViewById(R.id.icon);
  		TextView name = (TextView) findViewById(R.id.name);		//名前View
        TextView screenName = (TextView) findViewById(R.id.screenName);	//ＩＤView
        TextView text = (TextView) findViewById(R.id.text);		//ツイート本文View
        TextView Time =(TextView) findViewById(R.id.time);	//投稿時間View
        TextView RT_To = (TextView) findViewById(R.id.RT_to);	//ＲＴした人View
        TextView RT =(TextView) findViewById(R.id.RT);		//RT数View
        TextView Fav =(TextView) findViewById(R.id.Fav);	//お気に入り数View
        TextView Via =(TextView) findViewById(R.id.via);	//投稿元クライアント名Viwe
		TextView RTvia = (TextView) findViewById(R.id.RTvia);	//RTしたクライアント名

        //RTされているツイートかどうか？
        if(Tweet.getRetweetedStatus() == null)
        {
			Glide.with(this).load(Tweet.getUser().getProfileImageURLHttps()).into(icon);
        	name.setText(Tweet.getUser().getName());
        	screenName.setText( "@" + Tweet.getUser().getScreenName());
        	text.setText(Tweet.getText());
			Time.setText(DateFormat.getDateTimeInstance().format(Tweet.getCreatedAt()));
	        RT_To.setVisibility(View.GONE);	//RTした人の名前を非表示
			RTvia.setVisibility(View.GONE);
        }
        else
        {
			Glide.with(this).load(Tweet.getRetweetedStatus().getUser().getProfileImageURL()).into(icon);
        	name.setText(Tweet.getRetweetedStatus().getUser().getName().replaceAll("\n",""));
        	screenName.setText( "@" + Tweet.getRetweetedStatus().getUser().getScreenName());
        	text.setText(Tweet.getRetweetedStatus().getText());
			Time.setText(DateFormat.getDateTimeInstance().format(Tweet.getRetweetedStatus().getCreatedAt()));
	        RT_To.setVisibility(View.VISIBLE);
	        RT_To.setText(Tweet.getUser().getName() + " さんがリツイート");
			RTvia.setVisibility(View.VISIBLE);
			String str1 = Tweet.getRetweetedStatus().getSource();
			str1 = str1.replaceAll("<.+?>", "");
			RTvia.setText(str1 + "：RT元via");
        }
        RT.setText("RT:" + Tweet.getRetweetCount());
        Fav.setText("Fav:" + Tweet.getFavoriteCount());
        String str = Tweet.getSource();
        str = str.replaceAll("<.+?>", "");
        Via.setText(str + "：より");
		//会話取得処理
		mtAction.getConversation(Tweet.getInReplyToStatusId());
	}

	@Override
	public void onClick(View v) {
		switch(v.getId()){
        case R.id.mentionimageButton:
			//リプライ
    		Intent intent = new Intent(TweetDetail.this, MentionsActivity.class);
    		intent.putExtra("mentionID", Tweet.getId());
    		intent.putExtra("StatusID", Tweet.getUser().getScreenName());
    		intent.putExtra("Name", Tweet.getUser().getName());
    		intent.putExtra("Tweet", Tweet.getText());
    		intent.putExtra("Image", Tweet.getUser().getProfileImageURL());
    		String[] name = new String[20];
    		int j = 0;
    		for (UserMentionEntity UrlLink : Tweet.getUserMentionEntities()) {
    			name[j] = UrlLink.getScreenName();
    			j++;
    		}
    		intent.putExtra("UserMentionEntities", name);
            startActivity(intent);
            break;
        case R.id.retweetImageButton:
			//RT
    		new AlertDialog.Builder(TweetDetail.this)
    		.setTitle("RTしてよろしいですか？")
    		.setPositiveButton(
    		"はい",
    		new DialogInterface.OnClickListener() {
    			public void onClick(DialogInterface dialog, int which) {
    				// OK時の処理
                    mtAction.RTPost(Tweet.getId());
    			}
    		})
    		.setNegativeButton(
    		"いいえ",
    		new DialogInterface.OnClickListener() {
    			public void onClick(DialogInterface dialog, int which) {
    				// NO時の処理
    			}
    		})
    		.show();
            break;
		case R.id.favImageButton:
			//ふぁぼ
    		new AlertDialog.Builder(TweetDetail.this)
    		.setTitle("ふぁぼしますか？")
    		.setPositiveButton(
    		"はい",
    		new DialogInterface.OnClickListener() {
    			public void onClick(DialogInterface dialog, int which) {
    				// OK時の処理
                    mtAction.FaPost(Tweet.getId());
    			}
    		})
    		.setNegativeButton(
    		"いいえ",
    		new DialogInterface.OnClickListener() {
    			public void onClick(DialogInterface dialog, int which) {
    				// NO時の処理
    			}
    		})
    		.show();
			break;
		case R.id.menuImageButton:
			//その他メニュー呼び出し
			List_Menu list = new List_Menu();
			list.Detail_Menu(TweetDetail.this,Tweet);
		    break;
		}
	}
}
