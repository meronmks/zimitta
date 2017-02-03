package com.meronmks.zimitta.Menus;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.support.v7.app.AlertDialog;
import android.text.Spannable;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.meronmks.zimitta.Activity.MakeTweetActivity;
import com.meronmks.zimitta.Activity.TweetDetailActivity;
import com.meronmks.zimitta.Activity.UserDetailActivity;
import com.meronmks.zimitta.Adapter.MenuItemAdapter;
import com.meronmks.zimitta.Core.MutableLinkMovementMethod;
import com.meronmks.zimitta.Core.ViewHolder;
import com.meronmks.zimitta.Datas.ErrorLogs;
import com.meronmks.zimitta.Datas.MenuItems;
import com.meronmks.zimitta.Datas.ParcelStatus;
import com.meronmks.zimitta.Datas.UserSetting;
import com.meronmks.zimitta.Datas.Variable;
import com.meronmks.zimitta.R;
import com.meronmks.zimitta.TwitterUtil.TwitterAction;

import java.util.Date;
import java.util.List;

import twitter4j.HashtagEntity;
import twitter4j.Status;
import twitter4j.TwitterAdapter;
import twitter4j.TwitterException;
import twitter4j.TwitterListener;
import twitter4j.TwitterMethod;
import twitter4j.UserMentionEntity;

import static com.meronmks.zimitta.Core.StaticMethods.deleteMediaURL;
import static com.meronmks.zimitta.Core.StaticMethods.expansionURL;
import static com.meronmks.zimitta.Core.StaticMethods.mutableIDandHashTagMobement;
import static com.meronmks.zimitta.Core.StaticMethods.quoteTweetSetting;
import static com.meronmks.zimitta.Core.StaticMethods.replacrTimeAt;
import static com.meronmks.zimitta.Core.StaticMethods.setPreviewMedia;

/**
 * Created by p-user on 2016/10/03.
 */

public class ItemMenu implements AdapterView.OnItemClickListener {

    private Activity activity;
    private MenuItemAdapter adapter;
    private AlertDialog alertDialog;
    private ViewHolder vh;
    private TwitterAction mAction;
    private Status status;

    public ItemMenu(Activity activity){
        this.activity = activity;
        mAction = new TwitterAction(activity.getApplicationContext(), listener);
    }

    public void show(Status status){
        View view = activity.getLayoutInflater().inflate(R.layout.list_item_dialog, null);
        this.status = status;
        settingItemVIew(status, view);

        ListView listView = (ListView) view.findViewById(R.id.listItemMenu);
        listView.setOnItemClickListener(this);
        adapter = new MenuItemAdapter(activity.getBaseContext());
        makeItemMenu();
        listView.setAdapter(adapter);
        alertDialog = new AlertDialog.Builder(activity)
                .setView(view)
                .show();
    }

    /**
     * 動的にメニュー内容生成
     * @return
     */
    private void makeItemMenu(){
        adapter.add(new MenuItems().getInstans("詳細", MenuItems.Tags.Detail));
        adapter.add(new MenuItems().getInstans("返信", MenuItems.Tags.Replay));
        adapter.add(new MenuItems().getInstans("リツイート", MenuItems.Tags.RT));
        adapter.add(new MenuItems().getInstans("お気に入り", MenuItems.Tags.Fav));
        adapter.add(new MenuItems().getInstans("お気に入り＋リツイート", MenuItems.Tags.RTandFav));
        adapter.add(new MenuItems().getInstans("@" + status.getUser().getScreenName(), MenuItems.Tags.User));
        if(status.getRetweetedStatus() != null){
            adapter.add(new MenuItems().getInstans("@" + status.getRetweetedStatus().getUser().getScreenName(), MenuItems.Tags.User));
            status = status.getRetweetedStatus();
        }
        for (UserMentionEntity entity : status.getUserMentionEntities()) {
            adapter.add(new MenuItems().getInstans("@" + entity.getScreenName(), MenuItems.Tags.User));
        }
        for(HashtagEntity entity : status.getHashtagEntities()){
            adapter.add(new MenuItems().getInstans("#" + entity.getText(), MenuItems.Tags.HashTag));
        }
        if(Variable.userInfo.userID == status.getUser().getId()){
            adapter.add(new MenuItems().getInstans("削除", MenuItems.Tags.Delete));
        }
        adapter.add(new MenuItems().getInstans("共有", MenuItems.Tags.Share));
        adapter.add(new MenuItems().getInstans("プラグイン", MenuItems.Tags.Plugin));
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        if(adapter == null || adapter.getItem(position) == null) return;
        Intent intent;
        ParcelStatus ps;
        switch (adapter.getItem(position).tag){
            case Detail:
                intent = new Intent(activity, TweetDetailActivity.class);
                ps = new ParcelStatus();
                ps.status = status;
                intent.putExtra("status", ps);
                activity.startActivity(intent);
                break;
            case Replay:
                intent = new Intent(activity, MakeTweetActivity.class);
                intent.putExtra("mention", true);
                ps = new ParcelStatus();
                ps.status = status;
                intent.putExtra("status", ps);
                activity.startActivity(intent);
                break;
            case RT:
                if(UserSetting.ShowRTDialog(activity)){
                    new AlertDialog.Builder(activity)
                            .setTitle("確認")
                            .setMessage("リツイートをしますか？")
                            .setPositiveButton("はい", (dialog, which) -> mAction.retweetStatus(status.getId()))
                            .setNegativeButton("いいえ", null)
                            .show();
                }else{
                    mAction.retweetStatus(status.getId());
                }
                break;
            case Fav:
                if(UserSetting.ShowFavDialog(activity)){
                    new AlertDialog.Builder(activity)
                            .setTitle("確認")
                            .setMessage("お気に入りをしますか？")
                            .setPositiveButton("はい", (dialog, which) -> mAction.createFavorite(status.getId()))
                            .setNegativeButton("いいえ", null)
                            .show();
                }else{
                    mAction.createFavorite(status.getId());
                }
                break;
            case RTandFav:
                if(UserSetting.ShowFavRTDialog(activity)){
                    new AlertDialog.Builder(activity)
                            .setTitle("確認")
                            .setMessage("お気に入り+リツイートをしますか？")
                            .setPositiveButton("はい", (dialog, which) -> {
                                mAction.retweetStatus(status.getId());
                                mAction.createFavorite(status.getId());
                            })
                            .setNegativeButton("いいえ", null)
                            .show();
                }else{
                    mAction.retweetStatus(status.getId());
                    mAction.createFavorite(status.getId());
                }
                break;
            case Share:
                break;
            case Delete:
                if(UserSetting.ShowTweetDelDialog(activity)) {
                    new AlertDialog.Builder(activity)
                            .setTitle("確認")
                            .setMessage("ツイートの削除をしますか？")
                            .setPositiveButton("はい", (dialog, which) -> mAction.destroyStatus(status.getId()))
                            .setNegativeButton("いいえ", null)
                            .show();
                }else {
                    mAction.destroyStatus(status.getId());
                }
                break;
            case HashTag:
                break;
            case User:
                intent = new Intent(activity, UserDetailActivity.class);
                intent.putExtra("userName", adapter.getItem(position).name);
                activity.startActivity(intent);
                break;
            case Plugin:
                getPluginList();
                break;
            default:
                break;
        }
        alertDialog.dismiss();
    }

    /**
     * プラグイン一覧表示
     */
    private void getPluginList(){
        Intent intent = new Intent("jp.r246.twicca.ACTION_SHOW_TWEET");
        intent.putExtra("android.intent.extra.TEXT", status.getText());
        intent.putExtra("id", (String.valueOf(status.getId())));
        if(status.getGeoLocation() != null) {
            intent.putExtra("latitude", (String.valueOf(status.getGeoLocation().getLatitude())));
            intent.putExtra("longitude", (String.valueOf(status.getGeoLocation().getLongitude())));
        }
        intent.putExtra("created_at", (String.valueOf(status.getCreatedAt().getTime())));
        intent.putExtra("source", (status.getSource()));
        intent.putExtra("in_reply_to_status_id", (String.valueOf(status.getInReplyToStatusId())));
        intent.putExtra("user_screen_name", (status.getUser().getScreenName()));
        intent.putExtra("user_name", (status.getUser().getName()));
        intent.putExtra("user_id", (String.valueOf(status.getUser().getId())));
        intent.putExtra("user_profile_image_url", (status.getUser().getProfileImageURLHttps()));
        intent.putExtra("user_profile_image_url_mini", (status.getUser().getProfileImageURLHttps() + "_mini"));
        intent.putExtra("user_profile_image_url_normal", (status.getUser().getProfileImageURLHttps() + "_normal"));
        intent.putExtra("user_profile_image_url_bigger", (status.getUser().getProfileImageURLHttps() + "_bigger"));
        Intent chooser = Intent.createChooser(intent, "プラグイン一覧");
        if (intent.resolveActivity(activity.getPackageManager()) != null) {
            activity.startActivity(chooser);
        }else{
            showToast("使用できるプラグインが見つかりません。");
            ErrorLogs.putErrorLog("プラグイン関連エラー","使用できるプラグインが見つかりません。インストールされていないか、対応していません。");
        }
    }

    /**
     * Listener定義
     */
    private TwitterListener listener = new TwitterAdapter() {

        @Override
        public void retweetedStatus(Status retweetedStatus) {
            showToast("リツイートしました");
        }

        @Override
        public void createdFavorite(Status status) {
            showToast("お気に入りしました");
        }

        @Override
        public void destroyedFavorite(Status status) {
            super.destroyedFavorite(status);
            showToast("お気に入りを解除しました");
        }

        @Override
        public void destroyedStatus(Status destroyedStatus) {
            super.destroyedStatus(destroyedStatus);
            showToast("ツイートの削除完了");
        }

        @Override
        public void onException(TwitterException te, TwitterMethod method) {
            activity.runOnUiThread(() -> {
                switch (method){
                    case RETWEET_STATUS:
                        showToast("リツイートに失敗しました");
                        ErrorLogs.putErrorLog("リツイートに失敗しました", te.getMessage());
                        break;
                    case CREATE_FAVORITE:
                        showToast("お気に入りに失敗しました");
                        ErrorLogs.putErrorLog("お気に入りに失敗しました", te.getMessage());
                        break;
                    case DESTROY_STATUS:
                        showToast("ツイートの削除に失敗しました");
                        ErrorLogs.putErrorLog("ツイートの削除に失敗しました", te.getMessage());
                        break;
                    case DESTROY_FAVORITE:
                        showToast("お気に入りの解除に失敗しました");
                        ErrorLogs.putErrorLog("お気に入りの解除に失敗しました", te.getMessage());
                        break;
                }
            });
        }
    };

    private void showToast(String text){
        if(text == null || text.length() == 0) return;
        activity.runOnUiThread(() -> {
            Toast.makeText(activity.getApplicationContext(), text, Toast.LENGTH_SHORT).show();
        });
    }

    /**
     * ツイートの表示部分
     * @param status
     * @param cv
     */
    private void settingItemVIew(Status status, View cv){

        vh = iniViewHolder(cv);
        vh.TweetDeletedStatus.setVisibility(View.GONE);
        vh.RTUserIcon.setVisibility(View.GONE);
        vh.RTIcon.setVisibility(View.GONE);
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

        if(status.getUser().getId() == Variable.userInfo.userID){
            vh.TweetStatus.setVisibility(View.VISIBLE);
            vh.TweetStatus.setBackgroundResource(R.color.Blue);
        }else {
            for (UserMentionEntity entity : status.getUserMentionEntities()) {
                if(!entity.getScreenName().equals(Variable.userInfo.userScreenName))continue;
                vh.TweetStatus.setVisibility(View.VISIBLE);
                vh.TweetStatus.setBackgroundResource(R.color.Rad);
            }
        }
        if(status.isRetweet()){
            vh.TweetStatus.setVisibility(View.VISIBLE);
            vh.TweetStatus.setBackgroundResource(R.color.Green);
            vh.RTUserName.setVisibility(View.VISIBLE);
            vh.RTUserName.setText(status.getUser().getName() + " さんがRT");
            vh.RTUserIcon.setVisibility(View.VISIBLE);
            vh.RTIcon.setVisibility(View.VISIBLE);
            Glide.with(activity).load(status.getUser().getProfileImageURLHttps()).into(vh.RTUserIcon);
            status = status.getRetweetedStatus();
        }

        vh.Name.setText(status.getUser().getName());
        Glide.with(activity).load(status.getUser().getProfileImageURLHttps()).into(vh.UserIcon);
        vh.ScreenName.setText("@" + status.getUser().getScreenName());
        vh.TweetText.setText(mutableIDandHashTagMobement(status.getText()));
        replacrTimeAt(new Date(), status.getCreatedAt(), vh.Time);
        vh.Via.setText(status.getSource().replaceAll("<.+?>", "") + " : より");
        vh.RTCount.setText("RT : " + status.getRetweetCount());
        vh.FavCount.setText("Fav : " + status.getFavoriteCount());
        String text = status.getText();
        //画像処理
        if(status.getMediaEntities().length != 0){
            vh.PreviewImage.setVisibility(View.VISIBLE);
            setPreviewMedia(status.getMediaEntities(),vh.ImagePreviewViews, vh.PreviewVideoView1, activity);
            text = deleteMediaURL(text, status.getMediaEntities());
        }
        text = expansionURL(text, status.getURLEntities());
        vh.TweetText.setText(mutableIDandHashTagMobement(text));

        //引用ツイート関連
        if(status.getQuotedStatus() != null){
            quoteTweetSetting(status.getQuotedStatus(), vh, activity);
        }

        //鍵垢判定
        if(status.getUser().isProtected()){
            vh.LockedStatus.setVisibility(View.VISIBLE);
        }else{
            vh.LockedStatus.setVisibility(View.GONE);
        }

        //リンク処理
        mutableLinkMovement(vh.TweetText);
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
     * Holderを初期化
     * @param cv
     * @return
     */
    private ViewHolder iniViewHolder(View cv){
        vh = new ViewHolder();

        vh.Name = (TextView) cv.findViewById(R.id.Name);
        vh.UserIcon = (ImageView) cv.findViewById(R.id.UserIcon);
        vh.RTUserIcon = (ImageView) cv.findViewById(R.id.RTUserIcon);
        vh.RTIcon = (ImageView) cv.findViewById(R.id.RTIcon);
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
