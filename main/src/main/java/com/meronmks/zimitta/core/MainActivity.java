package com.meronmks.zimitta.core;

import android.annotation.SuppressLint;
import android.app.*;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBarActivity;
import android.view.View;
import android.widget.ImageButton;
import android.widget.Toast;
import com.gc.materialdesign.views.ProgressBarIndeterminate;
import com.meronmks.zimitta.Activity.TweetActivity;
import com.meronmks.zimitta.Activity.TwitterOAuthActivity;
import com.meronmks.zimitta.Adapter.MainTabFragmentPagerAdapter;
import com.meronmks.zimitta.BuildConfig;
import com.meronmks.zimitta.R;
import com.meronmks.zimitta.Variable.CoreVariable;
import com.meronmks.zimitta.menu.List_Menu;

public class MainActivity extends ActionBarActivity {

    private SharedPreferences accountIDCount;
//    private ShowRateLimit limit;
    //プログレスバー格納用
    private static ProgressBarIndeterminate progres;
    private static ProgressDialog progressDialog;
    private TwitterActionClass mtAction;
    private ViewPager viewPager;
    private  MainTabFragmentPagerAdapter pagerAdapter;

    //共有変数定義
    private static Context MainContext;
    //デバックモードフラグ
    public static Boolean DebugMode;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //レイアウト設定
        setContentView(R.layout.main);

        //Context格納
        MainContext = this;

        //初期化
        CoreVariable.iniVariable(MainContext);
        DebugMode = BuildConfig.DebugFlag;

        //アカウント情報を読み込む
        accountIDCount = getSharedPreferences("accountidcount", 0);

        //アクセストークンがあるかどうか
        if (!TwitterUtils.hasAccessToken(this, accountIDCount.getLong("ID_Num_Now", 0))) {
            Intent intent = new Intent(this, TwitterOAuthActivity.class);
            intent.putExtra("Flag", false);
            startActivity(intent);
            finish();
        } else {
            //IDとミュートリスト取得
            mtAction = new TwitterActionClass(this);
            if(CoreVariable.userName == "") {
                mtAction.getMyID();
            }
            if(CoreVariable.mutelist == null) {
                mtAction.getMyMuteList();
            }
            progres = (ProgressBarIndeterminate) findViewById(R.id.progressBar);
            //ボタン準備
            ImageButton tweet = (ImageButton) findViewById(R.id.tweet);
            ImageButton menu = (ImageButton) findViewById(R.id.Menu_button);

            //ボタンクリック時の動作設定
            tweet.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {
                    // クリックの処理を実行する
                    Intent intent = new Intent(MainActivity.this, TweetActivity.class);
                    startActivity(intent);
                }

            });

            menu.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {
                    // クリックの処理を実行する
                    List_Menu list = new List_Menu();
                    list.Main_menu(MainActivity.this, MainActivity.this, DebugMode, CoreVariable.Userid);
                }

            });

            //Fragment準備
            android.support.v4.app.FragmentManager fragmentManager = getSupportFragmentManager();
            pagerAdapter = new MainTabFragmentPagerAdapter(fragmentManager,this);
            viewPager = (ViewPager) findViewById(R.id.pager);
            viewPager.setAdapter(pagerAdapter);
        }
    }

    /**
     * 読み込み表示
     */
    public static void progresRun() {
        progres.setVisibility(View.VISIBLE);
    }

    /**
     * 読み込み表示終了
     */
    public static void progresStop() {
        progres.setVisibility(View.INVISIBLE);
    }

    //通知のメゾット
    @SuppressLint("NewApi")
    @SuppressWarnings("deprecation")
    public static void sendRepNotification(String text) {
        Notification.Builder n; // Notificationの生成

        Intent i = new Intent(MainContext.getApplicationContext(), MainActivity.class);
        PendingIntent pi = PendingIntent.getActivity(MainContext, 0, i, 0);

        n = new Notification.Builder(MainContext)
                .setContentIntent(pi).setSmallIcon(R.drawable.icon)
                .setTicker(text).setContentTitle(text)
                .setContentText("").setWhen(System.currentTimeMillis());
        // 通知時の音・バイブ・ライト
        n.setDefaults(Notification.DEFAULT_SOUND
                | Notification.DEFAULT_VIBRATE
                | Notification.DEFAULT_LIGHTS);

        // NotificationManagerのインスタンス取得
        NotificationManager nm = (NotificationManager) MainContext.getSystemService(Service.NOTIFICATION_SERVICE);
        nm.notify(1, n.build()); // 設定したNotificationを通知する
    }

    /**
     * トースト表示処理
     */
    public static void showToast(String text){
        Toast.makeText(MainContext, text, Toast.LENGTH_SHORT).show();
    }

    /**
     * プロセスダイアログを表示
     */
    public static void showProcessDialog() {
        progressDialog = new ProgressDialog(MainContext);
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progressDialog.setCancelable(true);
        progressDialog.setMessage(MainContext.getString(R.string.now_loading_text));
        progressDialog.setCancelable(false);
        progressDialog.show();
    }

    /**
     * プロセスダイアログを閉じる
     */
    public static void dismissProcessDialog(){
        progressDialog.dismiss();
    }

    /**
     * アプリ終了時
     */
    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(mtAction != null) {
            mtAction.stopStreaming();
        }
        CoreVariable.Destroy();
    }
}
