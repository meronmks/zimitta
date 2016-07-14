package com.meronmks.zimitta.Adapter;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import com.bumptech.glide.Glide;
import com.meronmks.zimitta.R;
import twitter4j.User;

/**
 * Created by p-user on 2015/05/25.
 */
public class FollowAdapter extends StatusCoreAdapter<User> {
    private LayoutInflater mInflater;

    public FollowAdapter(Context context) {
        super(context, android.R.layout.simple_list_item_1);
        mInflater = (LayoutInflater) context.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
    }

    static class ViewHolder {
        ImageView icon;
        TextView name;
        TextView screenName;
        TextView text;
        RelativeLayout relativeLayout;

    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if (convertView == null) {
            convertView = mInflater.inflate(R.layout.list_item_follow_follwer, null);

            ImageView Icon = (ImageView) convertView.findViewById(R.id.icon);
            TextView Name = (TextView) convertView.findViewById(R.id.name);
            TextView ScreenName = (TextView) convertView.findViewById(R.id.screenName);
            TextView Text = (TextView) convertView.findViewById(R.id.text);
            RelativeLayout RelativeLayout = (RelativeLayout) convertView.findViewById(R.id.tweetList);

            holder = new ViewHolder();
            holder.icon = Icon;
            holder.name = Name;
            holder.screenName = ScreenName;
            holder.text = Text;
            holder.relativeLayout = RelativeLayout;

            convertView.setTag(holder);

        } else {
            holder = (ViewHolder)convertView.getTag();
        }
        User item = getItem(position);
        holder.icon.setVisibility(View.VISIBLE);
        holder.name.setVisibility(View.VISIBLE);
        holder.screenName.setVisibility(View.VISIBLE);
        holder.text.setVisibility(View.VISIBLE);

        Glide.with(getContext()).load(item.getProfileImageURL()).into(holder.icon);
        holder.name.setText(item.getName());
        holder.screenName.setText( "@" + item.getScreenName());
        String str = item.getDescription();
        StringBuffer sb = new StringBuffer();
        char[] chr = str.toCharArray();
        for(int p=0;p<chr.length;p++) {
            if( chr[p] == ' ' || chr[p] == '　' ) continue;
            sb.append(chr[p]);
        }
        holder.text.setText(sb.toString());

        holder.relativeLayout.setBackgroundResource(R.drawable.listitem_color);
        super.getView(position, convertView, parent);
        return convertView;
    }
}
