package com.meronmks.zimitta.Settings;

import android.os.Bundle;
import android.preference.PreferenceActivity;

/**
 * Created by meron on 2016/09/21.
 */
public class SettingsActivity extends PreferenceActivity {
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getFragmentManager()
                .beginTransaction()
                .replace(android.R.id.content, new SettingFragment())
                .commit();
    }
}
