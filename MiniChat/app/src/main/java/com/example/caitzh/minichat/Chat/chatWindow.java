package com.example.caitzh.minichat.Chat;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.GestureDetector;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.example.caitzh.minichat.Adapter.ChatWindowAdapter;
import com.example.caitzh.minichat.Util.DataManager;
import com.example.caitzh.minichat.MessageReceiver;
import com.example.caitzh.minichat.Util.MyCookieManager;
import com.example.caitzh.minichat.R;
import com.example.caitzh.minichat.Friends.friendsList;
import com.example.caitzh.minichat.MyDB.recentListDB;
import com.example.caitzh.minichat.MyDB.recordDB;
import com.example.caitzh.minichat.Personal.personalInformation;
import com.example.caitzh.minichat.view.ChatWindowItemInformation;

import java.util.LinkedList;
import java.util.List;

/**
 * 登陆后的第一个页面：聊天窗口
 */

public class chatWindow extends AppCompatActivity implements View.OnTouchListener,
        GestureDetector.OnGestureListener {
    private recordDB myRecordDB;
    private recentListDB myRecentListDB;
    private ListView chatWindowListView;

    private LinearLayout linearLayout;
    private GestureDetector gestureDetector;
    // data用于保存所有最近聊天记录数据
    private List<ChatWindowItemInformation> data = new LinkedList<>();
    // 底部的按钮切换
    private LinearLayout friendsListLinearLayout;
    private LinearLayout personalInformationLinearLayout;
    private ChatWindowAdapter chatWindowAdapter;
    // 底部的按钮
    private ImageButton chat_img, maillist_img, information_img;
    private static final int UPDATE_LIST_VIEW = 1;
    private boolean sync = false; // 用于防止多线程修改data

    // 按返回键时不销毁当前Activity
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if(keyCode == KeyEvent.KEYCODE_BACK){
            moveTaskToBack(true);
            Intent backHome = new Intent(Intent.ACTION_MAIN);
            backHome.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            backHome.addCategory(Intent.CATEGORY_HOME);
            startActivity(backHome);
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_window);
        // Log.e("当前Activity是", getRunningActivityName());
        myRecordDB = new recordDB(chatWindow.this);
        myRecentListDB = new recentListDB(chatWindow.this);
        setView();
//        setChatWindowAdapter();
        IntentFilter filter = new IntentFilter(MessageReceiver.CHATWINDOWUPDATE);
        registerReceiver(broadcastReceiver, filter);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (!sync) setChatWindowAdapter();
    }

    public void setChatWindowAdapter() {
        sync = true;
        final String senderID = MyCookieManager.getUserId() ;
        Cursor recendCursor = myRecentListDB.getItems(senderID);
        int resultCounts = recendCursor.getCount();
        final String[] recentListIDs = new String[resultCounts];
        if(resultCounts != 0 && recendCursor.moveToFirst()){
            // 解析数据库得到最近联系人ID数组
            for(int i = 0; i < resultCounts; i++) {
                recentListIDs[i] = recendCursor.getString(recendCursor.getColumnIndex("receiver"));
                recendCursor.moveToNext();
            }
        }
        data.clear();
        new Thread(new Runnable() {
            @Override
            public void run() {
                for (int i = 0; i < recentListIDs.length; i++) {
                    // 获取最后一条聊天信息和相应聊天时间
                    Cursor lastItemCursor = myRecordDB.getLastItem(senderID, recentListIDs[i]);
                    String recendChatInformation = "";
                    String recendChatTime = "";
                    boolean isReaded = true;
                    int resultCounts = lastItemCursor.getCount();
                    if (resultCounts != 0 && lastItemCursor.moveToLast()) {
                        recendChatInformation = lastItemCursor.getString(lastItemCursor.getColumnIndex("content"));
                        recendChatTime = lastItemCursor.getString(lastItemCursor.getColumnIndex("time"));
                        isReaded = lastItemCursor.getInt(lastItemCursor.getColumnIndex("status"))>0;
                        Log.e("最后一条聊天消息内容", recendChatInformation);
                        Log.e("最后一条聊天是否已读", recendChatInformation);
                    }
                    // 获取用户名
                    String recendChatNickName = "";
                    recendChatNickName = DataManager.getLatestData(getApplicationContext(),recentListIDs[i]).getNickname();
                    ChatWindowItemInformation temp = new ChatWindowItemInformation(recentListIDs[i],
                            recendChatNickName, recendChatInformation, recendChatTime, isReaded);
                    data.add(0, temp);
                }
                Looper.prepare();
                chatWindowAdapter = new ChatWindowAdapter(data, chatWindow.this);
                Message message_ = new Message();
                message_.what = UPDATE_LIST_VIEW;
                handler.sendMessage(message_);
                sync = false;
            }
        }).start();
    }

    private void setView() {
        chat_img = (ImageButton) findViewById(R.id.id_tab_chat_img);
        maillist_img = (ImageButton) findViewById(R.id.id_tab_mail_list_img);
        information_img = (ImageButton) findViewById(R.id.id_tab_personal_information_img);
        // 在当前页面 聊天图标黑色，其他图标浅色
        chat_img.setImageDrawable(getResources().getDrawable(R.mipmap.chat_black));
        maillist_img.setImageDrawable(getResources().getDrawable(R.mipmap.maillist));
        information_img.setImageDrawable(getResources().getDrawable(R.mipmap.person));
        // 设置触摸的布局
        chatWindowListView = (ListView)findViewById(R.id.chat_window_list_view);
        friendsListLinearLayout = (LinearLayout)findViewById(R.id.id_tab_mail_list);
        personalInformationLinearLayout = (LinearLayout)findViewById(R.id.id_tab_personal_information);
        linearLayout = (LinearLayout)findViewById(R.id.chat_window_linear_layout);
        linearLayout.setOnTouchListener(this);
        linearLayout.setLongClickable(true);
        gestureDetector=new GestureDetector((GestureDetector.OnGestureListener)this);
        chatWindowListView.setOnTouchListener(this);
        chatWindowListView.setLongClickable(true);
        friendsListLinearLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(chatWindow.this,friendsList.class);
                startActivity(intent);
                overridePendingTransition(R.anim.finish_immediately, R.anim.finish_immediately);
            }
        });
        personalInformationLinearLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(chatWindow.this, personalInformation.class);
                startActivity(intent);
                overridePendingTransition(R.anim.finish_immediately, R.anim.finish_immediately);
            }
        });

        // 设置listview与相应的监听器
        chatWindowListView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, final int position, long id) {
                android.app.AlertDialog.Builder alertdialogbuilder =
                        new android.app.AlertDialog.Builder(chatWindow.this);
                alertdialogbuilder.setTitle("删除聊天记录");
                String delete_message = "确定删除该聊天记录?";
                alertdialogbuilder.setMessage(delete_message);
                alertdialogbuilder.setNegativeButton("取消", null);
                // 从数据库删除表项, 并更新ListView
                alertdialogbuilder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Log.e("从数据库删除的当前用户在list里的位置", Integer.toString(position));
                        Log.e("从数据库删除的当前用户的id", MyCookieManager.getUserId());
                        Log.e("从数据库删除的最近联系人的id", data.get(position).getUserID());
                        myRecentListDB.deleteItem(MyCookieManager.getUserId(), data.get(position).getUserID());
                        myRecordDB.deleteItems(MyCookieManager.getUserId(), data.get(position).getUserID());
                        data.remove(position);
                        chatWindowAdapter.notifyDataSetChanged();
                        /**
                         * todo 
                         */
                    }
                });
                android.app.AlertDialog alertDialog = alertdialogbuilder.create();
                alertDialog.show();
                // setChatWindowAdapter();
                return true;
            }
        });
        chatWindowListView.setOnItemClickListener(new AdapterView.OnItemClickListener(){
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(chatWindow.this, PersonalChatWindow.class);
                Bundle bundle = new Bundle();
                TextView userid = (TextView)view.findViewById(R.id.chat_window_listview_userid);
                TextView username = (TextView)view.findViewById(R.id.chat_window_listview_username);
                bundle.putString("receiveid", userid.getText().toString());
                bundle.putString("receivenickname", username.getText().toString());
                intent.putExtras(bundle);
                startActivity(intent);
            }
        });
    }

    // 以下为页面触碰以及点击接口所需实现的方法
    @Override
    public boolean onDown(MotionEvent e) {
        return false;
    }

    @Override
    public void onShowPress(MotionEvent e) {

    }

    @Override
    public boolean onSingleTapUp(MotionEvent e) {
        return false;
    }

    @Override
    public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
        return false;
    }

    @Override
    public void onLongPress(MotionEvent e) {

    }

    @Override
    public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
        final int FLING_MIN_DISTANCE=180;
        final int FLING_MIN_VELOCITY=200;

        //左
        if(e1.getX() - e2.getX() > FLING_MIN_DISTANCE && Math.abs(velocityX) > FLING_MIN_VELOCITY){
            Intent intent = new Intent(chatWindow.this,friendsList.class);
            startActivity(intent);
            overridePendingTransition(R.anim.slide_right_in, R.anim.slide_left_out);
        }

        return false;
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        return gestureDetector.onTouchEvent(event);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1) {
            setChatWindowAdapter();
        }
    }
    // 接收到新消息的广播接收器
    BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {
            Log.e("收到的消息", intent.getStringExtra("content"));
            setChatWindowAdapter();
        }
    };

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(broadcastReceiver);
    }

    // 利用Handler来更新UI
    private Handler handler = new Handler() {
        public void handleMessage(Message message) {
            switch (message.what) {
                case UPDATE_LIST_VIEW:
                    try {
                        chatWindowListView.setAdapter(chatWindowAdapter);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    break;
                default: break;
            }
        }
    };
}
