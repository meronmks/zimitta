package com.meronmks.zimitta.Settings;


import android.os.Bundle;
import android.preference.ListPreference;
import android.preference.PreferenceFragment;

import com.meronmks.zimitta.R;

/**
 * Created by meron on 2016/09/21.
 */
public class SettingFragment extends PreferenceFragment {
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.preferences);

        ListPreference list_preference = (ListPreference)getPreferenceScreen().findPreference("LoadTweetCount");
        list_preference.setSummary(list_preference.getEntry());
    }
}
