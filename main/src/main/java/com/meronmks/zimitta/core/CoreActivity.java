package com.meronmks.zimitta.core;

import android.annotation.SuppressLint;
import android.app.*;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageButton;
import android.widget.Toast;
import com.jakewharton.rxbinding.view.RxView;
import com.lsjwzh.widget.materialloadingprogressbar.CircleProgressBar;
import com.meronmks.zimitta.Activity.TweetActivity;
import com.meronmks.zimitta.Activity.TwitterOAuthActivity;
import com.meronmks.zimitta.Adapter.MainTabFragmentPagerAdapter;
import com.meronmks.zimitta.BuildConfig;
import com.meronmks.zimitta.Fragments.TimeLineFragment;
import com.meronmks.zimitta.R;
import com.meronmks.zimitta.Receiver.NetworkInfoReceiver;
import com.meronmks.zimitta.Variable.CoreVariable;
import com.meronmks.zimitta.menu.List_Menu;

public class CoreActivity extends AppCompatActivity {

    private SharedPreferences accountIDCount;
//    private ShowRateLimit limit;
    //プログレスバー格納用
    private static CircleProgressBar progres;
    private TwitterActionClass mtAction;
    private ViewPager viewPager;
    private  MainTabFragmentPagerAdapter pagerAdapter;
    private NetworkInfoReceiver receiver;

    //共有変数定義
    private static Context MainContext;
    //デバックモードフラグ
    public static Boolean isDebugMode;


    /**
     * Activity作成時
     * @param savedInstanceState
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //レイアウト設定
        setContentView(R.layout.main);

        //Context格納
        MainContext = this;

        //初期化
        CoreVariable.initializationVariable(MainContext);
        isDebugMode = BuildConfig.DebugFlag;

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
            if(CoreVariable.muteList == null) {
                mtAction.getMyMuteList();
            }
            progres = (CircleProgressBar) findViewById(R.id.progressBar);
            progres.setShowArrow(true);

            //ボタンクリック時の動作設定
            RxView
                    .clicks((ImageButton)findViewById(R.id.tweet))
                    .subscribe(x -> {
                        Intent intent = new Intent(CoreActivity.this, TweetActivity.class);
                        startActivity(intent);
                    });

            RxView
                    .clicks((ImageButton)findViewById(R.id.Menu_button))
                    .subscribe(x -> {
                        CoreVariable.ActiveFragmentView = viewPager.getCurrentItem();
                        List_Menu list = new List_Menu();
                        list.Main_menu(CoreActivity.this, CoreActivity.this, isDebugMode, CoreVariable.userID);
                    });

            //レシーバー呼び出し
            IntentFilter filter = new IntentFilter("android.net.conn.CONNECTIVITY_CHANGE");
            receiver = new NetworkInfoReceiver();
            if(filter != null && receiver != null) {
                registerReceiver(receiver, filter);
            }

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
        new UiHandler(){
            public void run(){
                progres.setVisibility(View.VISIBLE);
            }
        }.post();
    }

    /**
     * 読み込み表示終了
     */
    public static void progresStop() {
        new UiHandler(){
            public void run(){
                progres.setVisibility(View.INVISIBLE);
            }
        }.post();
    }

    //通知のメゾット
    @SuppressLint("NewApi")
    @SuppressWarnings("deprecation")
    public static void sendRepNotification(String text) {
        Notification.Builder n; // Notificationの生成

        Intent i = new Intent(MainContext.getApplicationContext(), CoreActivity.class);
        PendingIntent pi = PendingIntent.getActivity(MainContext, 0, i, 0);

        n = new Notification.Builder(MainContext)
                .setContentIntent(pi).setSmallIcon(R.drawable.ic_launcher)
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
        if(MainContext == null || text == null || text.length() == 0) return;
        Toast.makeText(MainContext, text, Toast.LENGTH_SHORT).show();
    }

    /**
     * Twitpicか判定
     * @param URL
     * @return
     */
    public static boolean isTwitpic(String URL){
        return URL.indexOf("twitpic") != -1 ? true : false;
    }

    /**
     * モバイル版？ついっぷるフォトか判定
     * @param URL
     * @return
     */
    public static boolean isPtwipple(String URL){
        return URL.indexOf("p.twipple") != -1 ? true : false;
    }

    /**
     * デスクトップ版？ついっぷるフォトか判定
     * @param URL
     * @return
     */
    public static  boolean isTwipple(String URL){
        return URL.indexOf("twipple") != -1 ? true : false;
    }

    /**
     * アプリ終了時
     */
    @Override
    protected void onDestroy() {
        super.onDestroy();
        TimeLineFragment.StreamingStop();
        CoreVariable.Destroy();
        if(receiver == null)return;
        unregisterReceiver(receiver);
    }
}
