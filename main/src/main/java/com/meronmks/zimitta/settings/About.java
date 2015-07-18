package com.meronmks.zimitta.settings;

import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
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