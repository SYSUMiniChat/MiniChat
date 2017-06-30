package com.example.caitzh.minichat;

import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;

import com.example.caitzh.minichat.MyDB.recentListDB;
import com.example.caitzh.minichat.MyDB.recordDB;
import com.example.caitzh.minichat.Util.DataManager;
import com.example.caitzh.minichat.Util.ImageUtil;
import com.example.caitzh.minichat.Util.MyCookieManager;
import com.tencent.android.tpush.XGPushBaseReceiver;
import com.tencent.android.tpush.XGPushClickedResult;
import com.tencent.android.tpush.XGPushRegisterResult;
import com.tencent.android.tpush.XGPushShowedResult;
import com.tencent.android.tpush.XGPushTextMessage;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;


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
    public void onNotifactionClickedResult(final Context context, XGPushClickedResult message) {
        if (context == null || message == null) {
            return;
        }
        if (message.getActionType() == XGPushClickedResult.NOTIFACTION_CLICKED_TYPE) {
            // 通知在通知栏被点击啦。。。。。
            // APP自己处理点击的相关动作
            Log.v("TEST", "通知被点击 :" + message);

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
    public static final String PERSONALCHATWINDOWUPDATE = "jason.broadcast.action1";
    public static final String CHATWINDOWUPDATE = "jason.broadcast.action2";
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
            myRecentListDB.insertOne(MyCookieManager.getUserId(), sender, DataManager.getCurrentDate());
            myRecordDB = new recordDB(context);
            myRecordDB.insertOne(1, MyCookieManager.getUserId(), sender,
                    message.getContent(), time, false);
            Log.e("聊天窗口是否在前台", context.getClass().getName());
            String pkg = "com.example.caitzh.minichat";
            // 根据当前的Activity发送相应广播进行相应更新
            if (isActivityAtRunningTop(pkg, pkg+".crh.chatWindow", context)) {
                // 当前在最近联系人界面
                Log.e("当前Activity", "最近联系人窗口");
                Intent intent = new Intent(CHATWINDOWUPDATE);
                intent.putExtra("content", message.getContent());
                intent.putExtra("time", time);
                context.sendBroadcast(intent);
            } else if (isActivityAtRunningTop(pkg, pkg+".crh.PersonalChatWindow", context)) {
                // 当前在聊天界面
                Log.e("当前Activity", "聊天窗口");
                Intent intent = new Intent(PERSONALCHATWINDOWUPDATE);
                intent.putExtra("content", message.getContent());
                intent.putExtra("time", time);
                intent.putExtra("receiver", sender);
                context.sendBroadcast(intent);
            }
        } else if (type == 1) {
//            Intent intent = new Intent(context, AddFriendActivity.class);
//            Bundle bundle = new Bundle();
//            bundle.putString("id", sender);
//            bundle.putInt("type", 1);
//            intent.putExtras(bundle);
//            context.startActivity(intent);
        } else if (type == 2) {

        } else {

        }
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
    // 判断某个Activity是否在前台
    public static boolean isActivityAtRunningTop(String pkg, String cls, Context context) {

        ActivityManager am =(ActivityManager)context.getSystemService(Context.ACTIVITY_SERVICE);

        List<ActivityManager.RunningTaskInfo> tasks = am.getRunningTasks(1);

        ActivityManager.RunningTaskInfo task = tasks.get(0);

        if (task != null) {
            return TextUtils.equals(task.topActivity.getPackageName(), pkg) && TextUtils.equals(task.topActivity.getClassName(), cls);
        }

        return false;

    }

}
