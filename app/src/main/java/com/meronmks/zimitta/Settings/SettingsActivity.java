package com.meronmks.zimitta.Settings;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import com.meronmks.zimitta.R;

/**
 * Created by meron on 2016/09/21.
 */
public class SettingsActivity extends AppCompatActivity {
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        Toolbar toolbar = (Toolbar) findViewById(R.id.ToolBar);
        setSupportActionBar(toolbar);
        getFragmentManager()
                .beginTransaction()
                .replace(R.id.PrefContent, new SettingFragment())
                .commit();
    }
}
