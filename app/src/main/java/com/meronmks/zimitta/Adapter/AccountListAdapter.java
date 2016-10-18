package com.meronmks.zimitta.Adapter;

import android.app.Activity;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.meronmks.zimitta.R;

import twitter4j.User;

/**
 * Created by meron on 2016/10/18.
 */

public class AccountListAdapter extends ArrayAdapter<User> {
    private LayoutInflater mInflater;

    public AccountListAdapter(Context context) {
        super(context, android.R.layout.simple_list_item_1);
        mInflater = (LayoutInflater) context.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = mInflater.inflate(R.layout.account_list_item, null);
        }
        User item = getItem(position);
        TextView name = (TextView) convertView.findViewById(R.id.name);
        name.setText(item.getName());
        TextView screenName = (TextView) convertView.findViewById(R.id.atid);
        screenName.setText("@" + item.getScreenName());
        ImageView Icon = (ImageView)convertView.findViewById(R.id.icon);

        try{
            Glide.with(getContext()).load(item.getProfileImageURLHttps()).into(Icon);
        }catch (Exception e){
            e.getStackTrace();
            Icon.setImageResource(R.mipmap.ic_sync_problem_white_24dp);
            Log.e("GlideException", e.getMessage());
        }
        //super.getView(position, convertView, parent);
        return convertView;
    }
}
