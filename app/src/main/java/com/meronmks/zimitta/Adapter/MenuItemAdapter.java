package com.meronmks.zimitta.Adapter;

import android.app.Activity;
import android.content.Context;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.meronmks.zimitta.Datas.MenuItems;
import com.meronmks.zimitta.R;

/**
 * Created by meron on 2016/12/15.
 */

public class MenuItemAdapter extends ArrayAdapter<MenuItems>{

    private LayoutInflater mInflater;
    private ViewHolder vh;

    static class ViewHolder{
        TextView name;
    }

    public MenuItemAdapter(Context context){
        super(context, android.R.layout.simple_list_item_1);
        mInflater = (LayoutInflater) context.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
    }

    @NonNull
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        vh = null;
        if(convertView == null){
            convertView = mInflater.inflate(R.layout.list_item_menu, null);
            vh = new ViewHolder();
            vh.name = (TextView) convertView.findViewById(R.id.MenuItemName);
            convertView.setTag(vh);
        }else{
            vh = (ViewHolder)convertView.getTag();
        }

        MenuItems item = getItem(position);

        vh.name.setText(item.name);
        return convertView;
    }
}
