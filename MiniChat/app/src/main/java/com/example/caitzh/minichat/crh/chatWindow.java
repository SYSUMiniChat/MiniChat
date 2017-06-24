package com.example.caitzh.minichat.crh;

import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

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
 * 微信登陆后的第一个页面：聊天窗口
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

        friendsListLinearLayout = (LinearLayout)findViewById(R.id.id_tab_mail_list);
        personalInformationLinearLayout = (LinearLayout)findViewById(R.id.id_tab_personal_information);
        friendsListLinearLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(chatWindow.this,friendsList.class);
                startActivity(intent);
            }
        });
        personalInformationLinearLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(chatWindow.this, personalInformation.class);
                startActivity(intent);
            }
        });

        linearLayout = (LinearLayout)findViewById(R.id.chat_window_linear_layout);
        linearLayout.setOnTouchListener(this);
        linearLayout.setLongClickable(true);
        gestureDetector=new GestureDetector((GestureDetector.OnGestureListener)this);

        myRecordDB = new recordDB(chatWindow.this);
        myRecentListDB = new recentListDB(chatWindow.this);
        myUserDB = new userDB(chatWindow.this);
        chatWindowListView = (ListView)findViewById(R.id.chat_window_list_view);
        chatWindowListView.setOnTouchListener(this);
        chatWindowListView.setLongClickable(true);
        setChatWindowAdapter();

        chatWindowListView.setOnItemClickListener(new AdapterView.OnItemClickListener(){
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(chatWindow.this, PersonalChatWindow.class);
                Bundle bundle = new Bundle();
                TextView userid = (TextView)view.findViewById(R.id.chat_window_listview_userid);
                TextView username = (TextView)view.findViewById(R.id.chat_window_listview_username);
                bundle.putString("userID", userid.getText().toString());
                bundle.putString("username", username.getText().toString());
                intent.putExtras(bundle);
                startActivity(intent);
            }
        });
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
        for (int i = 0; i < recentListIDs.length; i++) {
            // 获取最后一条聊天信息和相应聊天时间
            Cursor lastItemCursor = myRecordDB.getLastItem(senderID, recentListIDs[i]);
            String recendChatInformation = "";
            String recendChatTime = "";
            resultCounts = lastItemCursor.getCount();
            if (resultCounts != 0 && lastItemCursor.moveToFirst()) {
                recendChatInformation = lastItemCursor.getString(lastItemCursor.getColumnIndex("content"));
                recendChatTime = lastItemCursor.getString(lastItemCursor.getColumnIndex("time"));
            }
            // 获取用户名
            String recendChatNickName = "";
            Cursor userCursor = myUserDB.findOneByNumber(recentListIDs[i]);
            resultCounts = userCursor.getCount();
            if (resultCounts != 0 && userCursor.moveToFirst()) {
                recendChatNickName = userCursor.getString(userCursor.getColumnIndex("nickname"));
            }
            ChatWindowItemInformation temp = new ChatWindowItemInformation(senderID,
                    recendChatNickName, recendChatInformation, recendChatTime);
            data.add(temp);
        }
        chatWindowListView.setAdapter(new ChatWindowAdapter(data, chatWindow.this));
        chatWindowListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(chatWindow.this, PersonalChatWindow.class);
                Bundle bundle = new Bundle();
                bundle.putString("receiveid", data.get(position).getUserID());
                intent.putExtras(bundle);
                startActivityForResult(intent, 1);
            }
        });
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
    }

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
        final int FLING_MIN_DISTANCE=100;
        final int FLING_MIN_VELOCITY=200;


        //左
        if(e1.getX() - e2.getX() > FLING_MIN_DISTANCE && Math.abs(velocityX) > FLING_MIN_VELOCITY){
            Intent intent = new Intent(chatWindow.this,friendsList.class);
            startActivity(intent);
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
}
