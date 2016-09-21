package com.meronmks.zimitta.Menus;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.meronmks.zimitta.R;
import com.meronmks.zimitta.Settings.SettingFragment;
import com.meronmks.zimitta.Settings.SettingsActivity;

/**
 * Created by meron on 2016/09/21.
 */
public class MainMenu implements AdapterView.OnItemClickListener {

    private Activity activity;
    private ArrayAdapter<String> mAdapter;

    public MainMenu(Activity activity){
        this.activity = activity;
    }

    public void show(){
        View view = activity.getLayoutInflater().inflate(R.layout.menu_dialog, null);
        ListView listView = (ListView) view.findViewById(R.id.MenuList);
        listView.setOnItemClickListener(this);
        String[] members = { "プロフィール表示", "アカウント切り替えと変更", "設定", "API"};
        mAdapter = new ArrayAdapter<>(activity.getBaseContext(), android.R.layout.simple_expandable_list_item_1, members);
        listView.setAdapter(mAdapter);
        new AlertDialog.Builder(activity)
                .setView(view)
                .show();
    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
        switch (adapterView.getItemAtPosition(i).toString()){
            case "プロフィール表示":
                break;
            case "アカウント切り替えと変更":
                break;
            case "設定":
                Intent intent = new Intent(activity, SettingsActivity.class);
                activity.startActivity(intent);
                break;
            case "API":
                break;
        }
    }
}
