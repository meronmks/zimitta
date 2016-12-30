package com.meronmks.zimitta.Core;

import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

/**
 * Created by meron on 2016/12/30.
 * ツイートの表示に使うレイアウトを入れておくやつ
 */

public class ViewHolder {
    public TextView Name;
    public ImageView UserIcon;
    public ImageView RTUserIcon;
    public ImageView RTIcon;
    public TextView ScreenName;
    public TextView TweetText;
    public TextView Time;
    public TextView Via;
    public TextView RTCount;
    public TextView FavCount;
    public TextView RTUserName;
    public ImageView TweetDeletedStatus;
    public ImageView LockedStatus;
    public View TweetStatus;
    public LinearLayout PreviewImage;
    public ImageView[] ImagePreviewViews = new ImageView[4];
    public ImageView PreviewVideoView1;
    //引用ツイート関連
    public LinearLayout QuoteTweetView;
    public TextView QuoteName;
    public TextView QuoteScreenName;
    public TextView QuoteText;
    public TextView QuoteAtTime;
    public LinearLayout QuotePreviewImage;
    public ImageView[] ImageQuotePreviewViews = new ImageView[4];
    public ImageView QuotePreviewVideoView1;
}
