package com.meronmks.zimitta.TwitterUtil;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import com.meronmks.zimitta.Datas.ParcelStatus;
import com.meronmks.zimitta.Datas.Variable;

import twitter4j.Status;

/**
 * Created by meron on 2016/09/16.
 */
public class StreamReceiver extends BroadcastReceiver {

    public interface Callback{
        void onEventInvoked(Status status);
    }

    private Callback callback;
    private LocalBroadcastManager manager;

    private StreamReceiver(Context context, Callback callback) {
        super();
        this.callback = callback;
        manager = LocalBroadcastManager.getInstance(context);

        IntentFilter filter = new IntentFilter();
        filter.addAction(Variable.ACTION_INVOKED);

        manager.registerReceiver(this, filter);
    }

    public static StreamReceiver register(Context context, Callback callback) {
        return new StreamReceiver(context, callback);
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        Bundle bundle = intent.getBundleExtra(Variable.STREAM_BUNDLE);
        ParcelStatus parcelStatus = bundle.getParcelable(Variable.STREAM_PARCELABLE);
        callback.onEventInvoked(parcelStatus.status);
    }

    public static void sendLocalBroadcast(Context context, Status status){
        ParcelStatus parcelStatus = new ParcelStatus();
        parcelStatus.status = status;
        Bundle bundle = new Bundle();
        bundle.putParcelable(Variable.STREAM_PARCELABLE, parcelStatus);
        Intent intent = new Intent();
        intent.setAction(Variable.ACTION_INVOKED);
        intent.putExtra(Variable.STREAM_BUNDLE, bundle);
        LocalBroadcastManager localBroadcastManager = LocalBroadcastManager.getInstance(context);
        localBroadcastManager.sendBroadcast(intent);
    }

    public void unregister() {
        manager.unregisterReceiver(this);
    }
}
