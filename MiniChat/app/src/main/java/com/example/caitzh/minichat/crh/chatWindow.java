package com.example.caitzh.minichat.crh;

import android.app.ActivityManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.caitzh.minichat.DataManager;
import com.example.caitzh.minichat.MyCookieManager;
import com.example.caitzh.minichat.R;
import com.example.caitzh.minichat.friendsList;
import com.example.caitzh.minichat.MyDB.recentListDB;
import com.example.caitzh.minichat.MyDB.recordDB;
import com.example.caitzh.minichat.MyDB.userDB;
import com.example.caitzh.minichat.personalInformation;

import java.util.LinkedList;
import java.util.List;

/**
 * 登陆后的第一个页面：聊天窗口
 */

public class chatWindow extends AppCompatActivity implements View.OnTouchListener,
        GestureDetector.OnGestureListener {
    private recordDB myRecordDB;
    private recentListDB myRecentListDB;
    private userDB myUserDB;
    private ListView chatWindowListView;

    private LinearLayout linearLayout;
    private GestureDetector gestureDetector;
    // data用于保存所有最近聊天记录数据
    private List<ChatWindowItemInformation> data = new LinkedList<>();
    // 底部的按钮切换
    private LinearLayout friendsListLinearLayout;
    private LinearLayout personalInformationLinearLayout;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_window);

        // Log.e("当前Activity是", getRunningActivityName());
        myRecordDB = new recordDB(chatWindow.this);
        myRecentListDB = new recentListDB(chatWindow.this);
        myUserDB = new userDB(chatWindow.this);
        setView();
        setChatWindowAdapter();
    }

    @Override
    protected void onResume() {
        super.onResume();
        setChatWindowAdapter();
    }

    private void setChatWindowAdapter() {
        String senderID = MyCookieManager.getUserId() ;
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
        for (int i = 0; i < recentListIDs.length; i++) {
            // 获取最后一条聊天信息和相应聊天时间
            Cursor lastItemCursor = myRecordDB.getLastItem(senderID, recentListIDs[i]);
            String recendChatInformation = "";
            String recendChatTime = "";
            resultCounts = lastItemCursor.getCount();
            if (resultCounts != 0 && lastItemCursor.moveToLast()) {
                recendChatInformation = lastItemCursor.getString(lastItemCursor.getColumnIndex("content"));
                recendChatTime = lastItemCursor.getString(lastItemCursor.getColumnIndex("time"));
                Log.e("最后一条聊天消息内容", recendChatInformation);
            }
            // 获取用户名
            String recendChatNickName = "";
            recendChatNickName = DataManager.getLatestData(getApplicationContext(),recentListIDs[i]).getNickname();
            ChatWindowItemInformation temp = new ChatWindowItemInformation(recentListIDs[i],
                    recendChatNickName, recendChatInformation, recendChatTime);
            data.add(temp);
        }
        chatWindowListView.setAdapter(new ChatWindowAdapter(data, chatWindow.this));
    }

    private void setView() {
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
                        Log.e("从数据库删除的当前用户的id", MyCookieManager.getUserId());
                        Log.e("从数据库删除的最近联系人的id", data.get(position).getUserID());
                        myRecentListDB.deleteItem(MyCookieManager.getUserId(), data.get(position).getUserID());
                        myRecordDB.deleteItems(MyCookieManager.getUserId(), data.get(position).getUserID());
                    }
                });
                android.app.AlertDialog alertDialog = alertdialogbuilder.create();
                alertDialog.show();
                setChatWindowAdapter();
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


        Log.e("水平距离1", Float.toString((e1.getX() - e2.getX())));
        Log.e("水平速度1", Float.toString(Math.abs(velocityX)));
        //左
        if(e1.getX() - e2.getX() > FLING_MIN_DISTANCE && Math.abs(velocityX) > FLING_MIN_VELOCITY){
            Intent intent = new Intent(chatWindow.this,friendsList.class);
            startActivity(intent);
            finish();
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
    private String getRunningActivityName() {

        ActivityManager activityManager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        String runningActivity = activityManager.getRunningTasks(1).get(0).topActivity
                .getClassName();
        return runningActivity;
    }
}
