package com.meronmks.zimitta.Core;

import android.content.Intent;
import android.text.style.ClickableSpan;
import android.view.View;

import com.meronmks.zimitta.Activity.UserDetailActivity;

/**
 * Created by meron on 2016/10/06.
 * ScreenNameがClickされた時の動作
 */

public class ScreenNameClickable extends ClickableSpan {

    private String screenName;

    public ScreenNameClickable(String screenName){
        this.screenName = screenName;
    }

    @Override
    public void onClick(View widget) {
        Intent intent = new Intent(widget.getContext(), UserDetailActivity.class);
        intent.putExtra("userName", screenName);
        widget.getContext().startActivity(intent);
    }


}
