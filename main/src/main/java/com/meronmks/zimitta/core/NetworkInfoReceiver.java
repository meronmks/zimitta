package com.meronmks.zimitta.core;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.preference.PreferenceManager;
import android.util.Log;
import com.meronmks.zimitta.Fragments.TimeLineFragment;
import com.meronmks.zimitta.Variable.CoreVariable;

import java.util.Observer;

/**
 * Created by meronmks on 2015/09/19.
 * ネットワーク接続状態を取得するレシーバー
 */
public class NetworkInfoReceiver extends BroadcastReceiver {

    public NetworkInfoReceiver() {
        //コンストラクタ
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = cm.getActiveNetworkInfo();

        boolean isConnected = (networkInfo != null && networkInfo.isConnected());
        if (isConnected) {
            SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
            TwitterActionClass mtAction = new TwitterActionClass(context);
            CoreVariable.nowNetworkStatus = networkInfo.getType();
            Log.d("NetworkInfoReceiver","TypeID:" + CoreVariable.nowNetworkStatus);
            switch (CoreVariable.nowNetworkStatus){
                case ConnectivityManager.TYPE_WIFI:
                    Log.d("NetworkInfoReceiver","ConnectToWiFi");
                    TimeLineFragment.StreamingStart();
                    return;
                case ConnectivityManager.TYPE_MOBILE_DUN:
                    Log.d("NetworkInfoReceiver","ConnectToMobileDUN");
                    return;
                case ConnectivityManager.TYPE_MOBILE_HIPRI:
                    Log.d("NetworkInfoReceiver","ConnectToMobileHIPRI");
                    return;
                case ConnectivityManager.TYPE_MOBILE_MMS:
                    Log.d("NetworkInfoReceiver","ConnectToMobileMMS");
                    return;
                case ConnectivityManager.TYPE_MOBILE_SUPL:
                    Log.d("NetworkInfoReceiver","ConnectToMobileSUPL");
                    return;
                case ConnectivityManager.TYPE_MOBILE:
                    Log.d("NetworkInfoReceiver","ConnectToMobile");
                    if(sharedPreferences.getBoolean("Streeming_Wifi",false)) {
                        CoreActivity.showToast("WiFiから切断されました。\nストリーミングをOFFにします。");
                        TimeLineFragment.StreamingStop();
                    }else{
                        TimeLineFragment.StreamingStart();
                    }
                    return;
                case ConnectivityManager.TYPE_BLUETOOTH:
                    Log.d("NetworkInfoReceiver","ConnectToBLUETOOTH");
                    return;
                case ConnectivityManager.TYPE_ETHERNET:
                    Log.d("NetworkInfoReceiver","ConnectToEthernet");
                    return;
                case ConnectivityManager.TYPE_WIMAX:
                    Log.d("NetworkInfoReceiver","ConnectToWIMAX");
                    return;
            }
        } else {
            CoreActivity.showToast("ネットワークから切断されました");
            Log.d("NetworkInfoReceiver", "NetworkDisconnect");
            CoreVariable.nowNetworkStatus = -1;
            TimeLineFragment.StreamingStop();
            return;
        }
    }
}
