package com.meronmks.zimitta.Adapter;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import com.meronmks.zimitta.R;

import twitter4j.Status;

/**
 * Created by meron on 2016/09/14.
 */
public class TweetAdapter extends ArrayAdapter<Status> {
    private LayoutInflater mInflater;
    private Context mContext;

    public TweetAdapter(Context context) {
        super(context, android.R.layout.simple_list_item_1);
        mInflater = (LayoutInflater) context.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
        mContext = context;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        Status item = getItem(position);
        TextView tweetText = (TextView) convertView.findViewById(R.id.TweetText);
        tweetText.setText(item.getText());
        super.getView(position, convertView, parent);
        return convertView;
    }
}
