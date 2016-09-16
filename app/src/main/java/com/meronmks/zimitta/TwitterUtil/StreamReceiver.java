package com.meronmks.zimitta.TwitterUtil;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;

import com.meronmks.zimitta.Datas.ParcelStatus;

import org.jetbrains.annotations.NotNull;
import org.parceler.Parcels;

/**
 * Created by meron on 2016/09/16.
 */
public class StreamReceiver extends BroadcastReceiver {

    private static final String ACTION_INVOKED = "com.meronmks.zimitta.ACTION_INVOKED";
    private static final String BUNDLE = "BUNDLE";
    private static final String STREAMSTATUS = "STREAMSTATUS";

    public interface Callback {
        void onEventInvoked(ParcelStatus status);
    }

    private Callback callback;
    private LocalBroadcastManager manager;

    private StreamReceiver(@NotNull Context context, @NotNull Callback callback) {
        super();
        this.callback = callback;
        manager = LocalBroadcastManager.getInstance(context.getApplicationContext());

        IntentFilter filter = new IntentFilter();
        filter.addAction(ACTION_INVOKED);

        manager.registerReceiver(this, filter);
    }

    public static StreamReceiver register(@NotNull Context context, @NotNull Callback callback) {
        return new StreamReceiver(context, callback);
    }

    @Override
    public void onReceive(@NotNull Context context, @NotNull Intent intent) {
        String action = intent.getAction();

        if (ACTION_INVOKED.equals(action)) {
            Bundle bundle = intent.getBundleExtra(BUNDLE);
            callback.onEventInvoked(Parcels.unwrap(bundle.getParcelable(STREAMSTATUS)));
        }
    }

    public static void sendBroadcast(@NotNull Context context, @NotNull ParcelStatus status) {
        Intent intent = new Intent();
        Bundle bundle = new Bundle();
        bundle.putParcelable(STREAMSTATUS, Parcels.wrap(status));
        intent.putExtra(BUNDLE, bundle);

        LocalBroadcastManager manager = LocalBroadcastManager.getInstance(context.getApplicationContext());
        manager.sendBroadcast(intent);
    }

    public void unregister() {
        manager.unregisterReceiver(this);
    }
}
