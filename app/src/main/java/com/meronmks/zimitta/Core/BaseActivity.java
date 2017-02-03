package com.meronmks.zimitta.Core;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.text.Spannable;
import android.text.format.DateFormat;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.meronmks.zimitta.Activity.PlayVideoActivity;
import com.meronmks.zimitta.Activity.ShowImageActivity;
import com.meronmks.zimitta.Datas.Variable;
import com.meronmks.zimitta.R;
import com.meronmks.zimitta.TwitterUtil.StreamReceiver;
import com.meronmks.zimitta.TwitterUtil.TwitterAction;

import java.util.Calendar;
import java.util.Date;

import twitter4j.MediaEntity;
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
 *
 * Created by meron on 2016/09/20.
 */
public class BaseActivity extends AppCompatActivity {

    protected ViewHolder vh;
    protected TwitterAction mAction;

    protected void showToast(String text){
        if(text == null || text.length() == 0) return;
        runOnUiThread(() -> {
            Toast.makeText(this, text, Toast.LENGTH_SHORT).show();
        });
    }

    /**
     * ツイートの表示部分
     * @param status
     */
    protected void settingItemVIew(Status status){
        vh = iniViewHolder(findViewById(android.R.id.content)); //ルートディレクトリを投げつける
        setStatusitemtoView(this, vh, status);
    }
}
