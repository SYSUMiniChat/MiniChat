package com.example.caitzh.minichat;

import android.app.ActivityManager;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import com.example.caitzh.minichat.MyDB.recentListDB;
import com.example.caitzh.minichat.MyDB.recordDB;
import com.example.caitzh.minichat.crh.PersonalChatWindow;
import com.example.caitzh.minichat.crh.chatWindow;
import com.tencent.android.tpush.XGPushBaseReceiver;
import com.tencent.android.tpush.XGPushClickedResult;
import com.tencent.android.tpush.XGPushRegisterResult;
import com.tencent.android.tpush.XGPushShowedResult;
import com.tencent.android.tpush.XGPushTextMessage;
import com.tencent.android.tpush.data.RegisterEntity;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

import static android.app.Activity.RESULT_FIRST_USER;

/**
 * Created by Administrator on 2017/5/16.
 */
public class MessageReceiver extends XGPushBaseReceiver {

    private recentListDB myRecentListDB;
    private recordDB myRecordDB;

    @Override
    public void onRegisterResult(Context context, int i, XGPushRegisterResult xgPushRegisterResult) {

    }

    @Override
    public void onUnregisterResult(Context context, int i) {

    }

    @Override
    public void onSetTagResult(Context context, int i, String s) {

    }

    @Override
    public void onDeleteTagResult(Context context, int i, String s) {

    }

    @Override
    public void onTextMessage(Context context, XGPushTextMessage xgPushTextMessage) {
    }

    // 通知被点击时调用
    @Override
    public void onNotifactionClickedResult(Context context, XGPushClickedResult message) {
        if (context == null || message == null) {
            return;
        }
        if (message.getActionType() == XGPushClickedResult.NOTIFACTION_CLICKED_TYPE) {
            // 通知在通知栏被点击啦。。。。。
            // APP自己处理点击的相关动作
            Log.v("TEST", "通知被点击 :" + message);

            // message.getContent() 通知内容
            // message.getTitle() 通知的标题


            // 获取自定义key-value
            // 这是一个Json字符串, 格式如下：{"type": 1, "sender": "1234567892@qq.com"}
            String customContent = message.getCustomContent();
            Integer type = 0;  // 0表示聊天消息，1表示添加好友请求，2表示对方同意你的好友请求，3表示对方拒绝你的好友请求
            String sender = ""; // 发送者 id
            String time = "";
            if (customContent != null && customContent.length() != 0) {
                try {
                    JSONObject obj = new JSONObject(customContent);
                    if (!obj.isNull("type")) {
                        type = obj.getInt("type");
                        Log.v("TEST", "type value:" + type);
                    }
                    if (!obj.isNull("sender")) {
                        sender = obj.getString("sender");
                        Log.v("TEST", "sender value:" + sender);
                    }
                    if (!obj.isNull("time")) {
                        time = obj.getString("time");
                        Log.v("TEST", "time value:" + time);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
            // 通知被点击时启动对应的activity, 比如聊天界面，或者添加好友界面
            //Intent intent = new Intent(context, XXXActivity.class);
            //context.startActivity(intent);
        } else if (message.getActionType() == XGPushClickedResult.NOTIFACTION_DELETED_TYPE) {
            // 通知被清除啦。。。。
            // APP自己处理通知被清除后的相关动作
            Log.v("TEST", "通知被清除 :" + message);
        }
    }

    // 当通知出现(即收到消息时)被调用
    @Override
    public void onNotifactionShowedResult(Context context, XGPushShowedResult message) {
        Log.v("TEST", message.getContent());
        // 此时应该把消息内容写进数据库
        // 若此时正处于聊天界面且聊天对象与 message的sender一致，直接更新UI，把message.getContent()加到聊天界面
        String customContent = message.getCustomContent();
        Integer type = 0;  // 0表示聊天消息，1表示添加好友请求，2表示对方同意你的好友请求，3表示对方拒绝你的好友请求
        String sender = ""; // 发送者 id
        String time = ""; // 发送时间
        if (customContent != null && customContent.length() != 0) {
            try {
                JSONObject obj = new JSONObject(customContent);
                if (!obj.isNull("type")) {
                    type = obj.getInt("type");
                    Log.v("TEST", "type value:" + type);
                }
                if (!obj.isNull("sender")) {
                    sender = obj.getString("sender");
                    Log.v("TEST", "sender value:" + sender);
                }
                if (!obj.isNull("time")) {
                    time = obj.getString("time");
                    Log.v("TEST", "time value:" + time);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
            // TODO 加入聊天记录数据库，最近聊天数据库
        }
        if (type == 0) {
            myRecentListDB = new recentListDB(context);
            myRecordDB = new recordDB(context);
            myRecentListDB.insertOne(MyCookieManager.getUserId(), sender);
            myRecordDB.insertOne(1, MyCookieManager.getUserId(), sender, message.getContent(), time);
            Log.e("聊天窗口是否在前台", context.getClass().getName());
            // 收到消息应该判断当前的activity并作出相应操作而不是直接跳转
            Intent intent = new Intent(context, PersonalChatWindow.class);
            Bundle bundle = new Bundle();
            bundle.putString("receiveid", sender);
            intent.putExtras(bundle);
            context.startActivity(intent);
        } else if (type == 1) {
            Intent intent = new Intent(context, AddFriendActivity.class);
            Bundle bundle = new Bundle();
            bundle.putString("id", sender);
            bundle.putInt("type", 1);
            intent.putExtras(bundle);
            context.startActivity(intent);
        } else if (type == 2) {

        } else {

        }
    }
    public static boolean isForeground(Context context, String className) {
        if (context == null || TextUtils.isEmpty(className))
            return false;
        ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningTaskInfo> list = am.getRunningTasks(1);
        if (list != null && list.size() > 0) {
            ComponentName cpn = list.get(0).topActivity;
            if (className.equals(cpn.getClassName()))
                return true;
        }
        return false;
    }

    private static final int GET_IMAGE_OK = 0;
    // 获取路径下的图片
    private void getImage(final String path) {
        Thread thread = new Thread() {
            @Override
            public void run() {
                Bitmap bm = ImageUtil.getImage(path);
                if (bm != null) {

                    // 保存头像到本地
                    int start = path.lastIndexOf('/');
                    ImageUtil.saveImage(path.substring(start+1), bm);

                    //发生更新UI的消息
                    Message msg = handler.obtainMessage();
                    msg.obj = bm;
                    msg.what = GET_IMAGE_OK;
                    handler.sendMessage(msg);
                }
            }
        };
        thread.start();

    }

    // 利用Handler来更新UI
    private Handler handler = new Handler() {
        public void handleMessage(Message message) {
            switch (message.what) {
                case GET_IMAGE_OK:
                    try {
                        //avatar.setImageBitmap((Bitmap) message.obj); // 头像设置
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    break;
                default: break;
            }
        }
    };
}
