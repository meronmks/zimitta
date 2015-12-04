package com.meronmks.zimitta.settings;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.os.Bundle;
import android.preference.*;
import android.preference.Preference.OnPreferenceClickListener;
import com.meronmks.zimitta.R;


public class SettingActivity extends AppCompatPreferenceActivity implements OnSharedPreferenceChangeListener {
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.preferences);

        //表示時の初期化
        ListPreference list_preference = (ListPreference)getPreferenceScreen().findPreference("Load_Tweet");
        if(list_preference.getValue() == null){
            list_preference.setValue("20");
        }
        list_preference.setSummary(list_preference.getValue() + "件");

        CheckBoxChange();

        // PreferenceScreenからのIntent
        PreferenceScreen nextMove1 = (PreferenceScreen) findPreference("About");
        nextMove1.setOnPreferenceClickListener(new OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {

                // Activityの遷移
                Intent nextActivity = new Intent(SettingActivity.this,About.class);
                startActivity(nextActivity);
                return true;
            }
        });
    }

    //ここで設定が変更されたらテキストも変更する
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences,  String key) {
        @SuppressWarnings("deprecation")
        ListPreference list_preference = (ListPreference)getPreferenceScreen().findPreference("Load_Tweet");
        list_preference.setSummary(list_preference.getValue() + "件");
        CheckBoxChange();
    }

    //チェックボックスの使用状態変更
    private void CheckBoxChange()
    {
        CheckBoxPreference str_checkBoxPreference = (CheckBoxPreference)getPreferenceScreen().findPreference("Streem_Flug");
        CheckBoxPreference men_checkBoxPreference = (CheckBoxPreference)getPreferenceScreen().findPreference("NotificationMen");
        CheckBoxPreference RT_checkBoxPreference = (CheckBoxPreference)getPreferenceScreen().findPreference("NotificationRT");
        CheckBoxPreference Fav_checkBoxPreference = (CheckBoxPreference)getPreferenceScreen().findPreference("NotificationFav");
        CheckBoxPreference DM_checkBoxPreference = (CheckBoxPreference)getPreferenceScreen().findPreference("NotificationDM");
        CheckBoxPreference Fol_checkBoxPreference = (CheckBoxPreference)getPreferenceScreen().findPreference("NotificationFol");
        if(str_checkBoxPreference.isChecked())
        {
            men_checkBoxPreference.setEnabled(true);
            RT_checkBoxPreference.setEnabled(true);
            Fav_checkBoxPreference.setEnabled(true);
            DM_checkBoxPreference.setEnabled(true);
            Fol_checkBoxPreference.setEnabled(true);
        }
        else
        {
            men_checkBoxPreference.setEnabled(false);
            RT_checkBoxPreference.setEnabled(false);
            Fav_checkBoxPreference.setEnabled(false);
            DM_checkBoxPreference.setEnabled(false);
            Fol_checkBoxPreference.setEnabled(false);
        }
    }

    @SuppressWarnings("deprecation")
    @Override
    protected void onResume() {
        super.onResume();
        getPreferenceScreen().getSharedPreferences().registerOnSharedPreferenceChangeListener(this);
    }

    @SuppressWarnings("deprecation")
    @Override
    protected void onPause() {
        super.onPause();
        getPreferenceScreen().getSharedPreferences().unregisterOnSharedPreferenceChangeListener(this);
    }
}
