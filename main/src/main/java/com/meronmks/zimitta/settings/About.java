package com.meronmks.zimitta.settings;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.AlertDialog;
import android.view.*;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.TextView;
import com.meronmks.zimitta.BuildConfig;
import com.meronmks.zimitta.R;
import com.meronmks.zimitta.core.MainActivity;

public class About extends ActionBarActivity {

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
                MainActivity.DebugMode = !MainActivity.DebugMode;
                if(MainActivity.DebugMode){
                    MainActivity.showToast("デバックモードに入ります");
                }else{
                    MainActivity.showToast("デバックモードを終了します");
                }
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

}