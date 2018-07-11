package com.nela.pushapk;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiManager;
import android.os.Parcelable;
import android.util.Log;

import com.xiaomi.mipush.sdk.MiPushClient;

public class MobileNetWorkReceiver extends BroadcastReceiver {

    private static final String TAG = "PushApkNetworkReceiver";
    private static final String MIPUSH_APP_ID = "2882303761517831413";
    private static final String MIPUSH_APP_KEY = "5271783186413";
    private String TAG1 = "MobileNetWorkReceiver";

    //todo 判断是否含有token
    // 2 无网络等待有网络后触发第一步
    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();

        if (WifiManager.NETWORK_STATE_CHANGED_ACTION.equals(intent.getAction())) {
            Parcelable parcelableExtra = intent
                    .getParcelableExtra(WifiManager.EXTRA_NETWORK_INFO);
            if (null != parcelableExtra) {
                NetworkInfo networkInfo = (NetworkInfo) parcelableExtra;
                NetworkInfo.State state = networkInfo.getState();
                boolean isConnected = state == NetworkInfo.State.CONNECTED;// 当然，这边可以更精确的确定状态
                if (isConnected) {
                    MiPushClient.registerPush(context, MIPUSH_APP_ID, MIPUSH_APP_KEY);
                } else {
                    Log.d(TAG, "no_connected");
                }
            }
        }

        if (ConnectivityManager.CONNECTIVITY_ACTION.equals(intent.getAction())) {
            ConnectivityManager manager = (ConnectivityManager) context
                    .getSystemService(Context.CONNECTIVITY_SERVICE);
            Log.i(TAG1, "CONNECTIVITY_ACTION");

            NetworkInfo activeNetwork = manager.getActiveNetworkInfo();
            if (activeNetwork != null) { // connected to the internet
                if (activeNetwork.isConnected()) {
                    if (activeNetwork.getType() == ConnectivityManager.TYPE_WIFI) {
                        Log.e(TAG, "当前WiFi连接可用 ");
                        Log.d(TAG, "connected");
                        MiPushClient.registerPush(context, MIPUSH_APP_ID, MIPUSH_APP_KEY);

                    } else if (activeNetwork.getType() == ConnectivityManager.TYPE_MOBILE) {
                        MiPushClient.registerPush(context, MIPUSH_APP_ID, MIPUSH_APP_KEY);
                    } else {
                        Log.d(TAG, "register Token !=null ");
                    }
                    Log.e(TAG, "当前移动网络连接可用 ");
                }
            } else {
                Log.e(TAG, "当前没有网络连接，请确保你已经打开网络 ");
            }

        } else {   // not connected to the internet
            Log.e(TAG, "当前没有网络连接，请确保你已经打开网络 ");
        }
    }
}
