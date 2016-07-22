package com.meronmks.zimitta.Listener;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.AttributeSet;
import android.view.View;
import android.view.Window;

import com.meronmks.zimitta.R;
import com.meronmks.zimitta.core.CustomSurfaceView;

/**
 * Created by p-user on 2016/07/22.
 */
class LoadingStatusProgress extends View implements LoadingStatusListener {

    public LoadingStatusProgress(Context context) {
        super(context);
    }

    public LoadingStatusProgress(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public LoadingStatusProgress(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    public void onLoadingProgressUpdated(long totalReadSize, long contentSize) {

    }

    @Override
    public void onLoadingSuccess() {

    }

    @Override
    public void onLoadingFailed(String messege) {

    }
}
