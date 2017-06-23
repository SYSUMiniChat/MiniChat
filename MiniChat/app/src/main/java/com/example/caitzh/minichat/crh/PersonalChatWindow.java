package com.example.caitzh.minichat.crh;

import android.database.Cursor;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.widget.ListView;

import com.example.caitzh.minichat.MyCookieManager;
import com.example.caitzh.minichat.MyDB.recordDB;
import com.example.caitzh.minichat.R;

import java.util.ArrayList;
import java.util.List;

/**
 * 具体和某个人的聊天界面
 */

public class PersonalChatWindow extends AppCompatActivity {

    private ListView listView;
    private List<MiniChatMessage> mData;
    private PersonalChatWindowAdapter personalChatWindowAdapter;
    private recordDB myRecordDB;
    private String receiveid;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_personal_chat_window);
        Bundle bundle = this.getIntent().getExtras();
        receiveid = bundle.getString("receiveid");
        initViews();
    }

    private void initViews(){
        listView = (ListView)findViewById(R.id.PersonalChatWindowListView);
        mData = LoadData();
        personalChatWindowAdapter = new PersonalChatWindowAdapter(getBaseContext(), mData);
        listView.setAdapter(personalChatWindowAdapter);
    }

    // TODO 从数据库读入聊天记录
    private List<MiniChatMessage> LoadData() {
        List<MiniChatMessage> Messages=new ArrayList<MiniChatMessage>();
        myRecordDB = new recordDB(getBaseContext());
        Cursor cursor = myRecordDB.getItems(MyCookieManager.getUserId(), receiveid);
        int count = cursor.getCount();
        if (count != 0 && cursor.moveToFirst()) {
            int messageType = cursor.getInt(cursor.getColumnIndex("type"));
            String messageContent = cursor.getString(cursor.getColumnIndex("content"));
            MiniChatMessage Message = new MiniChatMessage(messageType, messageContent);
            Messages.add(Message);
        }
        return Messages;
    }

    // TODO 发送消息到服务器，并添加到本地数据库，以及更新UI
}
