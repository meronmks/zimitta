package com.meronmks.zimitta.Core;

import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;

/**
 *
 * Created by meron on 2016/09/20.
 */
public class BaseActivity extends AppCompatActivity {
    protected void showToast(String text){
        if(text == null || text.length() == 0) return;
        runOnUiThread(() -> {
            Toast.makeText(this, text, Toast.LENGTH_SHORT).show();
        });
    }
}
