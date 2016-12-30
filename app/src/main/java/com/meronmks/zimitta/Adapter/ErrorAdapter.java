package com.meronmks.zimitta.Adapter;

import android.app.Activity;
import android.content.Context;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.meronmks.zimitta.Datas.ErrorLogs;
import com.meronmks.zimitta.R;

/**
 * Created by meron on 2016/09/28.
 */
public class ErrorAdapter extends ArrayAdapter<ErrorLogs> {

    private LayoutInflater mInflater;

    public ErrorAdapter(Context context) {
        super(context, android.R.layout.simple_list_item_1);
        mInflater = (LayoutInflater) context.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        convertView = mInflater.inflate(R.layout.list_item_error, null);

        ErrorLogs item = getItem(position);

        TextView errorOverview = (TextView) convertView.findViewById(R.id.errorOverview);
        TextView errorCreatedAt = (TextView) convertView.findViewById(R.id.errorCreatedAt);

        errorOverview.setText(item.overview);
        errorCreatedAt.setText(DateFormat.format("yy/MM/dd kk:mm", item.createdAt));

        return convertView;
    }
}
