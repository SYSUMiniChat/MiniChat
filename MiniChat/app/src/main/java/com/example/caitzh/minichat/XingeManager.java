package com.example.caitzh.minichat;

import android.content.Context;

import com.tencent.android.tpush.XGPushManager;

/**
 * Created by Administrator on 2017/6/22.
 */
public class XingeManager {
    // 参数中的 context 为 getApplicationContext()的结果

    // 对应用注册，在应用启动时调用一次即可
    public static void registerApplication(Context context) {
        XGPushManager.registerPush(context);
    }

    //在用户登录后执行，account为用户id
    public static void registerApplication(Context context, String account) {
        XGPushManager.registerPush(context, account);
    }

    //在用户注销(logout)后执行
    public static void unregister(Context context) {
        XGPushManager.registerPush(context, "*");
    }
}
