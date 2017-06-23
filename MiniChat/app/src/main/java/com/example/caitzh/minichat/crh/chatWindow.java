package com.example.caitzh.minichat.crh;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import com.example.caitzh.minichat.MyCookieManager;
import com.example.caitzh.minichat.R;
import com.example.caitzh.minichat.recentListDB;
import com.example.caitzh.minichat.recordDB;
import com.example.caitzh.minichat.userDB;

import java.util.LinkedList;
import java.util.List;

/**
 * 微信登陆后的第一个页面：聊天窗口
 */

public class chatWindow extends AppCompatActivity{
    private recordDB myRecordDB;
    private recentListDB myRecentListDB;
    private userDB myUserDB;
    private ListView chatWindowListView;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_window);
        myRecordDB = new recordDB(chatWindow.this);
        myRecentListDB = new recentListDB(chatWindow.this);
        myUserDB = new userDB(chatWindow.this);
        chatWindowListView = (ListView)findViewById(R.id.chat_window_list_view);
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
        // TODO:获取当前用户ID并访问数据库
        String senderID = MyCookieManager.getUserId() ;
        Cursor recendCursor = myRecentListDB.getItems(senderID);
        int resultCounts =recendCursor.getCount();
        String[] recentListIDs = new String[resultCounts];
        if(resultCounts != 0 && recendCursor.moveToFirst()){
            // 解析数据库得到最近联系人ID数组
            for(int i = 0; i < resultCounts; i++) {
                recentListIDs[i] = recendCursor.getString(recendCursor.getColumnIndex("receiver"));
                recendCursor.moveToNext();
            }
        }
        // data用于保存所有最近聊天记录数据
        List<ChatWindowItemInformation> data = new LinkedList<>();
        for (int i = 0; i < recentListIDs.length; i++) {
            // TODO：获取当前用户ID并访问数据库
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
    }
}
