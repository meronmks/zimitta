package com.meronmks.zimitta.Menus;

import android.app.Activity;
import android.content.Context;
import android.support.v7.app.AlertDialog;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.meronmks.zimitta.R;

import twitter4j.Status;

/**
 * Created by p-user on 2016/10/03.
 */

public class ItemMenu implements AdapterView.OnItemClickListener {

    private Activity activity;
    private ArrayAdapter<String> adapter;
    private AlertDialog alertDialog;

    public ItemMenu(Activity activity){
        this.activity = activity;
    }

    public void show(Status status){
        View view = activity.getLayoutInflater().inflate(R.layout.list_item_dialog, null);
        ListView listView = (ListView) view.findViewById(R.id.listItemMenu);
        listView.setOnItemClickListener(this);
        String[] members = { "プロフィール表示", "アカウント切り替えと変更", "設定", "API"};
        adapter = new ArrayAdapter<>(activity.getBaseContext(), android.R.layout.simple_expandable_list_item_1, members);
        listView.setAdapter(adapter);
        alertDialog = new AlertDialog.Builder(activity)
                .setView(view)
                .show();
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

    }
}
