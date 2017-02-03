package com.meronmks.zimitta.Adapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.meronmks.zimitta.Activity.UserDetailActivity;
import com.meronmks.zimitta.Core.ViewHolder;
import com.meronmks.zimitta.Datas.Variable;
import com.meronmks.zimitta.R;

import java.util.Date;

import twitter4j.Status;
import twitter4j.UserMentionEntity;

import static com.meronmks.zimitta.Core.StaticMethods.deleteMediaURL;
import static com.meronmks.zimitta.Core.StaticMethods.expansionURL;
import static com.meronmks.zimitta.Core.StaticMethods.iniViewHolder;
import static com.meronmks.zimitta.Core.StaticMethods.mutableIDandHashTagMobement;
import static com.meronmks.zimitta.Core.StaticMethods.mutableLinkMovement;
import static com.meronmks.zimitta.Core.StaticMethods.quoteTweetSetting;
import static com.meronmks.zimitta.Core.StaticMethods.replacrTimeAt;
import static com.meronmks.zimitta.Core.StaticMethods.setPreviewMedia;
import static com.meronmks.zimitta.Core.StaticMethods.setStatusitemtoView;

/**
 * Created by meron on 2016/09/14.
 */
public class TweetAdapter extends BaseAdapter<Status> {
    private LayoutInflater mInflater;
    private ViewHolder vh;

    public TweetAdapter(Context context) {
        super(context, android.R.layout.simple_list_item_1);
        mInflater = (LayoutInflater) context.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        vh = null;
        if(convertView == null) {
            convertView = mInflater.inflate(R.layout.list_item_status, null);
            vh = iniViewHolder(convertView);
            convertView.setTag(vh);
        }else{
            vh = (ViewHolder) convertView.getTag();
        }

        Status item = getItem(position);

        setStatusitemtoView(getContext(), vh, item);

       return convertView;
    }

}
