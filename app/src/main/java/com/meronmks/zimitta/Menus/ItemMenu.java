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
import static com.meronmks.zimitta.Core.StaticMethods.iniViewHolder;
import static com.meronmks.zimitta.Core.StaticMethods.mutableIDandHashTagMobement;
import static com.meronmks.zimitta.Core.StaticMethods.quoteTweetSetting;
import static com.meronmks.zimitta.Core.StaticMethods.replacrTimeAt;
import static com.meronmks.zimitta.Core.StaticMethods.setPreviewMedia;
import static com.meronmks.zimitta.Core.StaticMethods.setStatusitemtoView;

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
        if (intent.resolveActivity(activity.getPackageManager()) != null) {
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
        setStatusitemtoView(activity, vh, status);
    }
}
