package com.meronmks.zimitta.Settings;


import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.preference.PreferenceScreen;

import com.meronmks.zimitta.R;

/**
 * Created by meron on 2016/09/21.
 */
public class SettingFragment extends PreferenceFragment implements SharedPreferences.OnSharedPreferenceChangeListener {
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.preferences);

        ListPreference list_preference = (ListPreference)getPreferenceScreen().findPreference("LoadTweetCount");
        list_preference.setSummary(list_preference.getEntry());

        Preference About = findPreference("About");
        About.setOnPreferenceClickListener(preference -> {
            Intent nextActivity = new Intent(getActivity() ,About.class);
            startActivity(nextActivity);
            return true;
        });

        Preference ErrorLog = findPreference("ErrorLog");
        ErrorLog.setOnPreferenceClickListener(preference -> {
            Intent nextActivity = new Intent(getActivity() ,ErrorLogActivity.class);
            startActivity(nextActivity);
            return true;
        });
    }


    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String s) {
        ListPreference list_preference = (ListPreference)getPreferenceScreen().findPreference("LoadTweetCount");
        list_preference.setSummary(list_preference.getEntry());
    }
}
