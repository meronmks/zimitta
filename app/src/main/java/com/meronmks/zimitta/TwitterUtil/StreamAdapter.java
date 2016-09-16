package com.meronmks.zimitta.TwitterUtil;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import com.meronmks.zimitta.Datas.ParcelStatus;
import com.meronmks.zimitta.Datas.Variable;

import twitter4j.Status;
import twitter4j.UserStreamAdapter;

/**
 * Created by meron on 2016/09/14.
 */
public class StreamAdapter extends UserStreamAdapter {

    private Context context;

    public StreamAdapter(Context context){
        this.context = context;
    }

    @Override
    public void onStatus(Status status) {
        super.onStatus(status);
        StreamReceiver.sendLocalBroadcast(context, status);
    }
}
