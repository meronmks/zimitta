package com.meronmks.zimitta.Adapter;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.meronmks.zimitta.Datas.Variable;
import com.meronmks.zimitta.R;

import java.util.Date;

import twitter4j.Status;

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

        vh.TweetDeletedStatus.setVisibility(View.GONE);
        vh.RTUserIcon.setVisibility(View.GONE);
        vh.RTUserName.setVisibility(View.GONE);
        vh.TweetStatus.setVisibility(View.GONE);
        vh.PreviewImage.setVisibility(View.GONE);
        vh.QuoteTweetView.setVisibility(View.GONE);
        vh.QuotePreviewImage.setVisibility(View.GONE);
        for(int i = 0; i < vh.ImagePreviewViews.length; i++){
            vh.ImagePreviewViews[i].setVisibility(View.GONE);
        }
        for(int i = 0; i < vh.ImageQuotePreviewViews.length; i++){
            vh.ImageQuotePreviewViews[i].setVisibility(View.GONE);
        }

        if(item.getUser().getId() == Variable.userID){
            vh.TweetStatus.setVisibility(View.VISIBLE);
            vh.TweetStatus.setBackgroundResource(R.color.Blue);
        }

        if(item.getRetweetedStatus() != null){
            vh.TweetStatus.setVisibility(View.VISIBLE);
            vh.TweetStatus.setBackgroundResource(R.color.Green);
            vh.RTUserName.setVisibility(View.VISIBLE);
            vh.RTUserName.setText(item.getUser().getName() + " さんがRT");
            vh.RTUserIcon.setVisibility(View.VISIBLE);
            Glide.with(getContext()).load(item.getUser().getProfileImageURLHttps()).into(vh.RTUserIcon);
            item = item.getRetweetedStatus();
        }

        vh.Name.setText(item.getUser().getName());
        Glide.with(getContext()).load(item.getUser().getProfileImageURLHttps()).into(vh.UserIcon);
        vh.ScreenName.setText("@" + item.getUser().getScreenName());
        vh.TweetText.setText(item.getText());
        replacrTimeAt(new Date(), item.getCreatedAt(), vh.Time);
        vh.Via.setText(item.getSource().replaceAll("<.+?>", "") + " : より");
        vh.RTCount.setText("RT : " + item.getRetweetCount());
        vh.FavCount.setText("Fav : " + item.getFavoriteCount());

        //画像処理
        if(item.getExtendedMediaEntities().length != 0){
            vh.PreviewImage.setVisibility(View.VISIBLE);
            setPreviewMedia(item.getExtendedMediaEntities(),vh.ImagePreviewViews, vh.PreviewVideoView1);
            vh.TweetText.setText(deleteMediaURL(item.getText(), item.getExtendedMediaEntities()));
        }

        //引用ツイート関連
        if(item.getQuotedStatus() != null){
            quoteTweetSetting(item.getQuotedStatus(), vh);
        }

        //鍵垢判定
        if(item.getUser().isProtected()){
            vh.LockedStatus.setVisibility(View.VISIBLE);
        }else{
            vh.LockedStatus.setVisibility(View.GONE);
        }

        //リンク処理
        mutableLinkMovement(vh.TweetText);

        vh.listItemBase.setBackgroundResource(R.drawable.list_item);
       return convertView;
    }



    /**
     * Holderを初期化
     * @param cv
     * @return
     */
    private ViewHolder iniViewHolder(View cv){
        vh = new ViewHolder();

        vh.listItemBase = (RelativeLayout) cv.findViewById(R.id.listItemBase);
        vh.Name = (TextView) cv.findViewById(R.id.Name);
        vh.UserIcon = (ImageView) cv.findViewById(R.id.UserIcon);
        vh.RTUserIcon = (ImageView) cv.findViewById(R.id.RTUserIcon);
        vh.ScreenName = (TextView) cv.findViewById(R.id.ScreenName);
        vh.TweetText = (TextView) cv.findViewById(R.id.TweetText);
        vh.Via = (TextView) cv.findViewById(R.id.Via);
        vh.RTCount = (TextView) cv.findViewById(R.id.RTCount);
        vh.FavCount = (TextView) cv.findViewById(R.id.FavCount);

        vh.RTUserName = (TextView) cv.findViewById(R.id.RTUserName);

        vh.TweetDeletedStatus = (ImageView) cv.findViewById(R.id.TweetDeletedStatus);
        vh.LockedStatus = (ImageView) cv.findViewById(R.id.LockedStatus);
        vh.TweetStatus = cv.findViewById(R.id.TweetStatus);
        vh.Time = (TextView) cv.findViewById(R.id.Time);

        vh.PreviewImage = (LinearLayout) cv.findViewById(R.id.PreviewImage);
        vh.ImagePreviewViews[0] = (ImageView) cv.findViewById(R.id.PreviewImageView1);
        vh.ImagePreviewViews[1] = (ImageView) cv.findViewById(R.id.PreviewImageView2);
        vh.ImagePreviewViews[2] = (ImageView) cv.findViewById(R.id.PreviewImageView3);
        vh.ImagePreviewViews[3] = (ImageView) cv.findViewById(R.id.PreviewImageView4);
        vh.PreviewVideoView1 = (ImageView) cv.findViewById(R.id.PreviewVideoView1);

        //引用ツイート関連
        vh.QuoteTweetView = (LinearLayout) cv.findViewById(R.id.QuoteTweetView);
        vh.QuoteName = (TextView) cv.findViewById(R.id.QuoteName);
        vh.QuoteScreenName = (TextView) cv.findViewById(R.id.QuoteScreenName);
        vh.QuoteText = (TextView) cv.findViewById(R.id.QuoteText);
        vh.QuoteAtTime = (TextView) cv.findViewById(R.id.QuoteAtTime);
        vh.QuotePreviewImage = (LinearLayout) cv.findViewById(R.id.QuotePreviewImage);
        vh.ImageQuotePreviewViews[0] = (ImageView) cv.findViewById(R.id.QuotePreviewImageView1);
        vh.ImageQuotePreviewViews[1] = (ImageView) cv.findViewById(R.id.QuotePreviewImageView2);
        vh.ImageQuotePreviewViews[2] = (ImageView) cv.findViewById(R.id.QuotePreviewImageView3);
        vh.ImageQuotePreviewViews[3] = (ImageView) cv.findViewById(R.id.QuotePreviewImageView4);
        vh.QuotePreviewVideoView1 = (ImageView) cv.findViewById(R.id.QuotePreviewVideoView1);

        return vh;
    }
}
