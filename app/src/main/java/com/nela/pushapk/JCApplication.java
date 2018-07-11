package com.nela.pushapk;

import android.app.Application;
import android.content.Context;

import com.nela.pushapk.Toos.Utils;
import com.xiaomi.mipush.sdk.MiPushClient;

public class JCApplication extends Application {

    private static final String MIPUSH_APP_ID = "2882303761517831413";
    private static final String MIPUSH_APP_KEY = "5271783186413";
    public static Context sContext;

    @Override
    public void onCreate() {
        super.onCreate();

        sContext = getApplicationContext();
        if (Utils.isNetworkAvailable(sContext)) {
            MiPushClient.registerPush(sContext, MIPUSH_APP_ID, MIPUSH_APP_KEY);
        } else {
            Utils.registerMobileNetWorkChangeBroadcast(sContext);
        }
        BinderManager.getInstance().start();
        //该进程无Activity
        // 1 开启自启动
        //启动小米模块
        //启动binder模块
        //小米模块
        // 1 有网络才开始去获取token
        // 2 无网络等待有网络后触发第一步
        // 3 token获取失败的逻辑处理
        // Binder模块
        // 1 连接Binder服务
        // 2 连接不上后定时进行重试
    }
}
