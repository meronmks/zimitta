package com.meronmks.zimitta.Adapter;
import android.content.Context;
import android.widget.ArrayAdapter;


public class AppListAdapter extends ArrayAdapter<String> {
	public AppListAdapter(Context context) {
        super(context, android.R.layout.simple_list_item_1);
    }
}
