package com.meronmks.zimitta.Adapter;

/**
 * Created by meron on 2016/09/14.
 */
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.text.Spannable;
import android.text.format.DateFormat;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.meronmks.zimitta.Activity.PlayVideoActivity;
import com.meronmks.zimitta.Activity.ShowImageActivity;
import com.meronmks.zimitta.Core.MainActivity;
import com.meronmks.zimitta.Core.MutableLinkMovementMethod;
import com.meronmks.zimitta.R;

import java.util.Calendar;
import java.util.Date;
import java.util.HashSet;

import twitter4j.ExtendedMediaEntity;
import twitter4j.MediaEntity;
import twitter4j.ResponseList;
import twitter4j.Status;


public class BaseAdapter<T> extends ArrayAdapter<T> {

    static class ViewHolder {
        RelativeLayout listItemBase;
        TextView Name;
        ImageView UserIcon;
        ImageView RTUserIcon;
        TextView ScreenName;
        TextView TweetText;
        TextView Time;
        TextView Via;
        TextView RTCount;
        TextView FavCount;

        TextView RTUserName;

        ImageView TweetDeletedStatus;
        ImageView LockedStatus;
        View TweetStatus;

        LinearLayout PreviewImage;
        ImageView[] ImagePreviewViews = new ImageView[4];
        ImageView PreviewVideoView1;

        //引用ツイート関連
        LinearLayout QuoteTweetView;
        TextView QuoteName;
        TextView QuoteScreenName;
        TextView QuoteText;
        TextView QuoteAtTime;
        LinearLayout QuotePreviewImage;
        ImageView[] ImageQuotePreviewViews = new ImageView[4];
        ImageView QuotePreviewVideoView1;
    }

    public BaseAdapter(Context context, int resources) {
        super(context, resources);
    }

    /**
     * ループテキストの排除メソッド
     */
    protected String replaceLoopText(String tweetText){
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(getContext());
        if(sp.getBoolean("zipTweet", false)) {
            //ループテキストを圧縮準備
            boolean loopTextFound = false;
            tweetText = tweetText.replaceAll("\r\n", "\n");
            //一行ずつ取り出し
            String[] loopStrings = tweetText.split("\n");
            //ループしてるか判定し、していたら文字を消す
            //ただし最初に一致しているのを発見した場合「Following text looped」に置き換える
            for (int i = 1; i < loopStrings.length; i++) {
                if (loopStrings[0].equals(loopStrings[i]) && !loopTextFound) {
                    loopStrings[i] = "Following text looped";
                    loopTextFound = true;
                } else {
                    loopStrings[i] = loopStrings[i].replace(loopStrings[0], "");
                }
            }
            //処理が終わったテキストを合成
            StringBuffer buf = new StringBuffer();
            for (int i = 0; i < loopStrings.length; i++) {
                if (i + 1 != loopStrings.length && !loopStrings[i].equals("")) {
                    buf.append(loopStrings[i] + "\n");
                } else {
                    buf.append(loopStrings[i]);
                }
            }
            tweetText = buf.toString();
        }
        return tweetText;
    }

    /**
     * 時間を変換するやつ
     */
    protected void replacrTimeAt(Date TimeStatusNow, Date CreatedAt, TextView timeView){
        Calendar cal1 = Calendar.getInstance();
        Calendar cal2 = Calendar.getInstance();
        cal1.setTime(CreatedAt);
        cal2.setTime(TimeStatusNow);
        long date1 = cal1.getTimeInMillis();
        long date2 = cal2.getTimeInMillis();
        long time = (date2 - date1) / 1000;
        if(time < 5){
            timeView.setText("now");
        }
        if (5 <= time && time <= 59) {
            timeView.setText(time + "s前");
        }
        time = time / 60;
        if ((time <= 59) && (time >= 1)) {
            timeView.setText(time + "m前");
        }
        time = time / 60;
        if ((time <= 23) && (time >= 1)) {
            timeView.setText(time + "h前");
        }
        time = time / 24;
        if (time != 0) {
            timeView.setText(DateFormat.format("yyyy/MM/dd kk:mm:ss", CreatedAt));
        }
    }

    /**
     * メディアのプレビュー表示
     * @param extendedMediaEntity
     * @param imageViews
     */
    protected void setPreviewMedia(ExtendedMediaEntity[] extendedMediaEntity, ImageView[] imageViews, ImageView videoPlayView){
        for(int i = 0; i < extendedMediaEntity.length; i++){
            imageViews[i].setVisibility(View.VISIBLE);
            if(extendedMediaEntity[i].getType().equals("photo")) {
                videoPlayView.setVisibility(View.GONE);
            }else{
                videoPlayView.setVisibility(View.VISIBLE);
            }
            Glide.with(getContext())
                    .load(extendedMediaEntity[i].getMediaURLHttps() + ":thumb")
                    .placeholder(R.mipmap.ic_sync_white_24dp)
                    .error(R.mipmap.ic_sync_problem_white_24dp)
                    .dontAnimate()
                    .into(imageViews[i]);

            final int finalI = i;
            imageViews[i].setOnClickListener(view -> {
                if(extendedMediaEntity[finalI].getType().equals("photo")){
                    String imageURL = extendedMediaEntity[finalI].getMediaURLHttps();
                    Intent image = new Intent(getContext(), ShowImageActivity.class);
                    image.putExtra("Images", imageURL);
                    getContext().startActivity(image);
                }else if(extendedMediaEntity[finalI].getType().equals("video")){
                    String videoURL = extendedMediaEntity[finalI].getMediaURLHttps();
                    Intent video = new Intent(getContext(), PlayVideoActivity.class);
                    video.putExtra("Video", videoURL);
                    getContext().startActivity(video);
                }else{
                    MainActivity.showToast(extendedMediaEntity[finalI].getType());
                }
            });
        }
    }

    /**
     * メディアURLを消す
     * @param tweet
     * @param extendedMediaEntity
     */
    protected String deleteMediaURL(String tweet, ExtendedMediaEntity[] extendedMediaEntity){
        for(MediaEntity media : extendedMediaEntity){
            tweet = tweet.replaceAll(media.getURL(), "");
        }
        return tweet;
    }

    /**
     * 引用ツイートの処理
     * @param status
     */
    protected void quoteTweetSetting(Status status, ViewHolder vh){
        vh.QuoteTweetView.setVisibility(View.VISIBLE);
        vh.QuoteName.setText(status.getUser().getName());
        vh.QuoteScreenName.setText("@" + status.getUser().getScreenName());
        vh.QuoteText.setText(status.getText());
        replacrTimeAt(new Date(), status.getCreatedAt(), vh.QuoteAtTime);
        mutableLinkMovement(vh.QuoteText);
        if(status.getExtendedMediaEntities().length != 0){
            vh.QuotePreviewImage.setVisibility(View.VISIBLE);
            setPreviewMedia(status.getExtendedMediaEntities(),vh.ImageQuotePreviewViews, vh.QuotePreviewVideoView1);
            vh.QuoteText.setText(deleteMediaURL(status.getText(), status.getExtendedMediaEntities()));
        }
    }

    /**
     * TextViewのリンク以外のクリックイベントを更に下のViewへ渡す
     * @param TweetText
     */
    protected void mutableLinkMovement(TextView TweetText){
        TweetText.setOnTouchListener((view, event) -> {
            TextView textView = (TextView) view;
            //LinkMovementMethodを継承したもの 下記参照
            MutableLinkMovementMethod m = new MutableLinkMovementMethod();
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
    }

    /**
     * 重複がなくなるように入れる
     * @param adapter なんか気持ち悪いけどこうするしかなさそう？
     * @param statuses 取得したツイート
     */
    public void statusAddAll(TweetAdapter adapter, ResponseList<Status> statuses){
        adapter.addAll(statuses);
        //ID判断で重複消し
        HashSet<Long> hs = new HashSet<>();
        for (int i = 0; i < adapter.getCount();) {
            if (!hs.contains(adapter.getItem(i).getId())) {
                hs.add(adapter.getItem(i).getId());
                ++i;
            } else {
                adapter.remove(adapter.getItem(i));
            }
        }
        //時間でソート
        adapter.sort((t2, t1) -> t1.getCreatedAt().compareTo(t2.getCreatedAt()));
    }

    /**
     * 重複がなくなるように入れる
     * @param adapter なんか気持ち悪いけどこうするしかなさそう？
     * @param status 取得したツイート
     */
    public void statusAdd(TweetAdapter adapter, Status status){
        adapter.add(status);
        //ID判断で重複消し
        HashSet<Long> hs = new HashSet<>();
        for (int i = 0; i < adapter.getCount();) {
            if (!hs.contains(adapter.getItem(i).getId())) {
                hs.add(adapter.getItem(i).getId());
                ++i;
            } else {
                adapter.remove(adapter.getItem(i));
            }
        }
        //時間でソート
        adapter.sort((t2, t1) -> t1.getCreatedAt().compareTo(t2.getCreatedAt()));
    }
}
