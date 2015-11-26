package com.meronmks.zimitta.Activity;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.provider.MediaStore;
import android.support.v7.app.ActionBarActivity;
import android.text.Editable;
import android.text.SpannableStringBuilder;
import android.text.TextWatcher;
import android.text.method.ScrollingMovementMethod;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.*;
import com.bumptech.glide.Glide;
import com.meronmks.zimitta.R;
import com.meronmks.zimitta.Variable.CoreVariable;
import com.meronmks.zimitta.core.CoreActivity;
import com.meronmks.zimitta.core.TwitterActionClass;
import com.meronmks.zimitta.core.TwitterUtils;
import twitter4j.Twitter;

import java.io.InputStream;

/**
 * Created by meronmks on 2015/04/07.
 */
public class MentionsActivity extends ActionBarActivity {
    private EditText mInputText;
    private Twitter mTwitter;
    private ProgressDialog progressDialog;
    private String[] path;
    private Uri[] uri;
    private TextView textCount;
    private SharedPreferences accountIDCount,sp,appSharedPreferences;
    private ImageButton button1;
    private ImageButton button2;
    private ImageButton button3;
    private ImageButton button4;
    private TwitterActionClass mtAction;
    private long mentionID;
    private String StatusID;
    private int txtLength;

    @SuppressLint("NewApi")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_mentions);

        //配列数確保
        path = new String[4];
        uri = new Uri[4];

        mtAction = new TwitterActionClass(this);

        getSupportActionBar().setDisplayShowHomeEnabled(false);
        accountIDCount = getSharedPreferences("accountidcount", 0);
        appSharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        mTwitter = TwitterUtils.getTwitterInstance(this, accountIDCount.getLong("ID_Num_Now", 0));

        Intent intent = getIntent();
        mentionID = intent.getLongExtra("mentionID", BIND_ABOVE_CLIENT);
        StatusID = intent.getStringExtra("StatusID");
        String name = intent.getStringExtra("Name");
        String tweet = intent.getStringExtra("Tweet");
        String Image = intent.getStringExtra("Image");
        String MentionEntity[] = intent.getStringArrayExtra("UserMentionEntities");

        accountIDCount = getSharedPreferences("accountidcount", 0);
        mTwitter = TwitterUtils.getTwitterInstance(this,accountIDCount.getLong("ID_Num_Now", 0));

        TextView ScreenName = (TextView)this.findViewById(R.id.m_screen_name);	//IDの受け取り
        ScreenName.setText("@" + StatusID);										//表示
        TextView Name = (TextView)this.findViewById(R.id.m_name);				//表示名の受け取り
        sp = getSharedPreferences("accountidcount", 0);
        Name.setText(name);

        mInputText = (EditText) findViewById(R.id.input_text);
        textCount = ((TextView)findViewById(R.id.textCount));

        textCount.setText(Integer.toString(140 - mInputText.getText().length()));

        mInputText.addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                int textColor = Color.GRAY;

                // 入力文字数の表示
                txtLength = 140 - s.length();
                textCount.setText(Integer.toString(txtLength) + "");

                // 指定文字数オーバーで文字色を赤くする
                if (txtLength < 0) {
                    textColor = Color.RED;
                }
                textCount.setTextColor(textColor);
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                // TODO 自動生成されたメソッド・スタブ

            }

            @Override
            public void afterTextChanged(Editable s) {
                // TODO 自動生成されたメソッド・スタブ

            }
        });

        mInputText.setText("@" + StatusID + " ");
        for(int i = 0;i < 20;i++)
        {
            if(MentionEntity[i] == null)
            {
                break;
            }
            else if(!MentionEntity[i].equals(CoreVariable.userName) && !MentionEntity[i].equals(StatusID))
            {
                mInputText.append("@" + MentionEntity[i] + " ");
            }
        }

        CharSequence str = mInputText.getText();
        mInputText.setSelection(str.length());
        TextView Tweet = (TextView)this.findViewById(R.id.m_text);				//ツイート本文の受け取り
        Tweet.setMovementMethod(ScrollingMovementMethod.getInstance());
        Tweet.setText(tweet);
        ImageView icon = (ImageView) this.findViewById(R.id.m_icon);
        Glide.with(this).load(Image).into(icon);

        // ビュー
        final View view = this.findViewById(R.id.Activity_Mention);
        view.setBackgroundColor(Color.BLACK);
        findViewById(R.id.Image_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_PICK);
                intent.setType("image/*");
                startActivityForResult(intent, 1);
            }
        });

        //画像取り消し用ボタン関連
        button1 = (ImageButton)findViewById(R.id.listTlReloadButton);
        button2 = (ImageButton)findViewById(R.id.Button02);
        button3 = (ImageButton)findViewById(R.id.Button04);
        button4 = (ImageButton)findViewById(R.id.Button03);

        //ボタンのクリックリスナの作成
        button1.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // クリックの処理を実行する
                button1.setVisibility(View.GONE);
                path[0] = null;
                ImageView Select_Image_button = (ImageView) MentionsActivity.this.findViewById(R.id.Select_Image_button);
                Select_Image_button.setImageResource(R.drawable.clear);
            }

        });

        button2.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // クリックの処理を実行する
                button2.setVisibility(View.GONE);
                path[1] = null;
                ImageView Select_Image_button = (ImageView) MentionsActivity.this.findViewById(R.id.Select_Image_button1);
                Select_Image_button.setImageResource(R.drawable.clear);
            }

        });

        button3.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // クリックの処理を実行する
                button3.setVisibility(View.GONE);
                path[2] = null;
                ImageView Select_Image_button = (ImageView) MentionsActivity.this.findViewById(R.id.Select_Image_button2);
                Select_Image_button.setImageResource(R.drawable.clear);
            }

        });

        button4.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // クリックの処理を実行する
                button4.setVisibility(View.GONE);
                path[3] = null;
                ImageView Select_Image_button = (ImageView) MentionsActivity.this.findViewById(R.id.Select_Image_button3);
                Select_Image_button.setImageResource(R.drawable.clear);
            }

        });

    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        String[] columns;
        columns = new String[]{ MediaStore.Images.Media.DATA };
        if (resultCode != RESULT_OK) {
            return;
        }

        if (requestCode == 1) {
            for(int i = 0;i<4;i++) {
                if (path[i] == null) {
                    uri[i] = data.getData();    //選択した画像の受け取り
                    ContentResolver cr = getContentResolver();
                    if (uri[i] != null) {
                        Cursor c = cr.query(uri[i], columns, null, null, null);
                        if(c != null) {
                            c.moveToFirst();
                            path[i] = c.getString(0);
                            c.close();
                        }else{
                            showDialog("画像受け取りに失敗しました。\nCursorがnullです。");
                        }
                        break;    //選択したらfor文を抜ける
                    } else {
                        showDialog("画像受け取りに失敗しました。\nUriがnullです。");
                    }
                }
            }
            Button Image_button = (Button)this.findViewById(R.id.Image_button);
            Image_button.setText("さらに選択する");
            //画像の表示
            try {
                if(uri[3] != null){
                    InputStream in = getContentResolver().openInputStream(uri[3]);
                    Bitmap img = BitmapFactory.decodeStream(in);
                    in.close();
                    ImageView Select_Image_button = (ImageView) this.findViewById(R.id.Select_Image_button2);
                    Select_Image_button.setImageBitmap(img);
                    button3.setVisibility(View.VISIBLE);
                }
                if(uri[2] != null)
                {
                    InputStream in = getContentResolver().openInputStream(uri[2]);
                    Bitmap img = BitmapFactory.decodeStream(in);
                    in.close();
                    ImageView Select_Image_button = (ImageView) this.findViewById(R.id.Select_Image_button3);
                    Select_Image_button.setImageBitmap(img);
                    button4.setVisibility(View.VISIBLE);
                }
                if(uri[1] != null)
                {
                    InputStream in = getContentResolver().openInputStream(uri[1]);
                    Bitmap img = BitmapFactory.decodeStream(in);
                    in.close();
                    ImageView Select_Image_button = (ImageView) this.findViewById(R.id.Select_Image_button1);
                    Select_Image_button.setImageBitmap(img);
                    button2.setVisibility(View.VISIBLE);
                }
                if(uri[0] != null){
                    InputStream in = getContentResolver().openInputStream(uri[0]);
                    Bitmap img = BitmapFactory.decodeStream(in);
                    in.close();
                    ImageView Select_Image_button = (ImageView) this.findViewById(R.id.Select_Image_button);
                    Select_Image_button.setImageBitmap(img);
                    button1.setVisibility(View.VISIBLE);
                }
            }catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * メニュー追加
     * @param menu
     * @return
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        Button sendTweet = (Button)findViewById(R.id.TweetPostButton);
        if(appSharedPreferences.getBoolean("PostTweetTweetPosition",true)){
            MenuInflater inflater = getMenuInflater();
            inflater.inflate(R.menu.tweet_button, menu);
            sendTweet.setVisibility(View.GONE);
        }else{
            sendTweet.setVisibility(View.VISIBLE);
            sendTweet.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    postMention();
                }
            });
        }
        return true;
    }

    //メニューのアイテムを押したときの判別
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.tweetButton:        //ツイート
                postMention();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void postMention(){
        boolean imageFound = false;
        //画像が添付されているか
        for(int i = 0;i<4;i++) {
            if (path[i] != null) {
                imageFound = true;
            }
        }

        if(txtLength > 0 || imageFound) {
            SpannableStringBuilder sb = (SpannableStringBuilder) mInputText.getText();
            mtAction.sendMention(sb.toString(), path, mentionID);
            finish();
        }else{
            CoreActivity.showToast("テキスト又は画像を入力してください");
        }
    }

    private void showToast(String text) {
        Toast.makeText(this, text, Toast.LENGTH_SHORT).show();
    }

    //エラーダイアログ
    private void showDialog(String text){
        AlertDialog.Builder alertDialog=new AlertDialog.Builder(this);
        alertDialog.setTitle("Error!");      //タイトル設定
        alertDialog.setMessage(text);  //内容(メッセージ)設定

        // OK(肯定的な)ボタンの設定
        alertDialog.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                // OKボタン押下時の処理
            }
        });
        alertDialog.show();
    }
}
