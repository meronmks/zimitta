package com.meronmks.zimitta.Adapter;

import android.app.Activity;
import android.content.Context;
import android.provider.ContactsContract;
import android.text.Spannable;
import android.text.util.Linkify;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.meronmks.zimitta.Core.MutableLinkMovementMethod;
import com.meronmks.zimitta.R;

import java.util.Date;

import twitter4j.Status;

/**
 * Created by meron on 2016/09/14.
 */
public class TweetAdapter extends BaseAdapter<Status> {
    private LayoutInflater mInflater;
    private Context mContext;

    static class ViewHolder {
        TextView Name;
        ImageView UserIcon;
        TextView ScreenName;
        TextView TweetText;
        TextView Time;
        TextView Via;

        TextView RTUserName;

        ImageView TweetDeletedStatus;
        ImageView LockedStatus;
        View TweetStatus;
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

        viewHolder.TweetDeletedStatus.setVisibility(View.GONE);
        viewHolder.RTUserName.setVisibility(View.GONE);
        viewHolder.TweetStatus.setVisibility(View.GONE);

        Linkify.addLinks(viewHolder.TweetText, Linkify.WEB_URLS);

        viewHolder.TweetText.setOnTouchListener((view, event) -> {
            TextView textView = (TextView) view;
            //LinkMovementMethodを継承したもの 下記参照
            MutableLinkMovementMethod m = new MutableLinkMovementMethod();
            //MovementMethod m=LinkMovementMethod.getInstance();
            //リンクのチェックを行うため一時的にsetする
            textView.setMovementMethod(m);
            boolean mt = m.onTouchEvent(textView, (Spannable) textView.getText(), event);
            //チェックが終わったので解除する しないと親view(listview)に行けない
            textView.setMovementMethod(null);
            //setMovementMethodを呼ぶとフォーカスがtrueになるのでfalseにする
            textView.setFocusable(false);
            //戻り値がtrueの場合は今のviewで処理、falseの場合は親viewで処理
            return mt;
        });

        if(item.getRetweetedStatus() != null){
            viewHolder.TweetStatus.setVisibility(View.VISIBLE);
            viewHolder.TweetStatus.setBackgroundResource(R.color.Green);
            viewHolder.RTUserName.setVisibility(View.VISIBLE);
            viewHolder.RTUserName.setText(item.getUser().getName() + " さんがRT");
            item = item.getRetweetedStatus();
        }

        viewHolder.Name.setText(item.getUser().getName());
        Glide.with(getContext()).load(item.getUser().getProfileImageURLHttps()).into(viewHolder.UserIcon);
        viewHolder.ScreenName.setText("@" + item.getUser().getScreenName());
        viewHolder.TweetText.setText(item.getText());
        replacrTimeAt(new Date(), item.getCreatedAt(), viewHolder.Time);
        viewHolder.Via.setText(item.getSource().replaceAll("<.+?>", "") + " : より");

        //鍵垢判定
        if(item.getUser().isProtected()){
            viewHolder.LockedStatus.setVisibility(View.VISIBLE);
        }else{
            viewHolder.LockedStatus.setVisibility(View.GONE);
        }
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
        viewHolder.UserIcon = (ImageView) convertView.findViewById(R.id.UserIcon);
        viewHolder.ScreenName = (TextView) convertView.findViewById(R.id.ScreenName);
        viewHolder.TweetText = (TextView) convertView.findViewById(R.id.TweetText);
        viewHolder.Via = (TextView) convertView.findViewById(R.id.Via);

        viewHolder.RTUserName = (TextView) convertView.findViewById(R.id.RTUserName);

        viewHolder.TweetDeletedStatus = (ImageView) convertView.findViewById(R.id.TweetDeletedStatus);
        viewHolder.LockedStatus = (ImageView) convertView.findViewById(R.id.LockedStatus);
        viewHolder.TweetStatus = convertView.findViewById(R.id.TweetStatus);
        viewHolder.Time = (TextView) convertView.findViewById(R.id.Time);
        return viewHolder;
    }
}
