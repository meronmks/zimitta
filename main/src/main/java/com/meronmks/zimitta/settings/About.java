package com.meronmks.zimitta.settings;

import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.*;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.TextView;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.meronmks.zimitta.BuildConfig;
import com.meronmks.zimitta.R;
import com.meronmks.zimitta.core.CoreActivity;

public class About extends AppCompatActivity {

    private AdView adView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.about);

        TextView verName = (TextView)findViewById(R.id.verTextView);

        verName.setText(BuildConfig.VERSION_NAME);

        Button button = (Button)findViewById(R.id.aboutLicenseButton);

        button.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // クリックの処理を実行する
                LayoutInflater factory = LayoutInflater.from(About.this);
                final View inputView = factory.inflate(R.layout.license_dialog, null);

                AlertDialog.Builder builder = new AlertDialog.Builder(About.this);
                builder.setTitle("オープンソースライブラリ");
                builder.setView(inputView);

                if (inputView != null){
                    WebView webView = (WebView) inputView.findViewById(R.id.webView);
                    webView.loadUrl("file:///android_asset/licenses.html");
                }

                builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int whichButton) {

                    }
                });

                AlertDialog dialog = builder.create();
                dialog.show();
            }

        });

        adView = (AdView)findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder()
                .addTestDevice(AdRequest.DEVICE_ID_EMULATOR)
                .addTestDevice("6589876C65F878753519D46A23B269B6")
                .build();
        adView.loadAd(adRequest);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.debugmode, menu);
        return true;
    }

    //メニューのアイテムを押したときの判別
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.Debug_Menu:        //アカウント追加画面へ
                CoreActivity.isDebugMode = !CoreActivity.isDebugMode;
                if(CoreActivity.isDebugMode){
                    CoreActivity.showToast("デバックモードに入ります");
                }else{
                    CoreActivity.showToast("デバックモードを終了します");
                }
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onPause() {
        super.onPause();
        adView.pause();
    }

    @Override
    protected void onResume() {
        super.onResume();
        adView.resume();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        adView.destroy();
    }
}