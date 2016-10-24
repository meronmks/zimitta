package com.meronmks.zimitta.Menus;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.meronmks.zimitta.Activity.AccountChangeActivity;
import com.meronmks.zimitta.Datas.ErrorLogs;
import com.meronmks.zimitta.R;
import com.meronmks.zimitta.Settings.SettingsActivity;

/**
 * Created by meron on 2016/09/21.
 */
public class MainMenu implements AdapterView.OnItemClickListener {

    private Activity activity;
    private ArrayAdapter<String> adapter;
    private AlertDialog alertDialog;

    public MainMenu(Activity activity) {
        this.activity = activity;
    }

    public void show() {
        View view = activity.getLayoutInflater().inflate(R.layout.menu_dialog, null);
        ListView listView = (ListView) view.findViewById(R.id.MenuList);
        listView.setOnItemClickListener(this);
        String[] members = {"プロフィール表示", "アカウント切り替えと変更", "設定", "API"};
        adapter = new ArrayAdapter<>(activity.getBaseContext(), android.R.layout.simple_expandable_list_item_1, members);
        listView.setAdapter(adapter);
        alertDialog = new AlertDialog.Builder(activity)
                .setView(view)
                .show();
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long l) {
        switch (parent.getItemAtPosition(position).toString()) {
            case "プロフィール表示":
                break;
            case "アカウント切り替えと変更":
                Intent Account = new Intent(activity, AccountChangeActivity.class);
                activity.startActivityForResult(Account, 104);
                break;
            case "設定":
                Intent intent = new Intent(activity, SettingsActivity.class);
                activity.startActivity(intent);
                break;
            case "API":
                ErrorLogs.putErrorLog("APITest", "Test");
                break;
        }
        alertDialog.dismiss();
    }
}


