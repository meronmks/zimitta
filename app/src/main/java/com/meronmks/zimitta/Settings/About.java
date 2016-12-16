package com.meronmks.zimitta.Settings;

import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.TextView;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.meronmks.zimitta.BuildConfig;
import com.meronmks.zimitta.R;

/**
 * Created by meron on 2016/09/23.
 */
public class About extends AppCompatActivity {

    private AdView adView;
    private Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.about);
        toolbar = (Toolbar)findViewById(R.id.ToolBar);
        toolbar.setTitle("About");
        setSupportActionBar(toolbar);
        TextView verName = (TextView)findViewById(R.id.verTextView);
        verName.setText(BuildConfig.VERSION_NAME);
        Button button = (Button)findViewById(R.id.aboutLicenseButton);
        button.setOnClickListener(v -> {
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
            builder.setPositiveButton("OK", (dialog, whichButton) -> {
            });
            AlertDialog dialog = builder.create();
            dialog.show();
        });
        adView = (AdView)findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder()
                .addTestDevice(AdRequest.DEVICE_ID_EMULATOR)
                .addTestDevice("6589876C65F878753519D46A23B269B6")
                .build();
        adView.loadAd(adRequest);
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
