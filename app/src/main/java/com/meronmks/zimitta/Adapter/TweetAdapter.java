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
public class TweetAdapter extends BaseAdapter<Status> {
    private LayoutInflater mInflater;
    private Context mContext;

    static class ViewHolder {
        TextView Name;
        TextView ScreenName;
        TextView TweetText;
    }

    public TweetAdapter(Context context) {
        super(context, android.R.layout.simple_list_item_1);
        mInflater = (LayoutInflater) context.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
        mContext = context;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        ViewHolder viewHolder = null;
        if(convertView == null) {
            convertView = mInflater.inflate(R.layout.list_item_status, null);
            viewHolder = iniViewHolder(convertView);
            convertView.setTag(viewHolder);
        }else{
            viewHolder = (ViewHolder) convertView.getTag();
        }

        Status item = getItem(position);
        viewHolder.Name.setText(item.getUser().getName());
        viewHolder.ScreenName.setText("@" + item.getUser().getScreenName());
        viewHolder.TweetText.setText(item.getText());

        return convertView;
    }

    /**
     * Holderを初期化
     * @param convertView
     * @return
     */
    private ViewHolder iniViewHolder(View convertView){
        ViewHolder viewHolder = new ViewHolder();

        viewHolder.Name = (TextView) convertView.findViewById(R.id.Name);
        viewHolder.ScreenName = (TextView) convertView.findViewById(R.id.ScreenName);
        viewHolder.TweetText = (TextView) convertView.findViewById(R.id.TweetText);
        return viewHolder;
    }
}
