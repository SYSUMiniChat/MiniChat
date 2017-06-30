package com.example.caitzh.minichat.crh;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.example.caitzh.minichat.DataManager;
import com.example.caitzh.minichat.MessageReceiver;
import com.example.caitzh.minichat.MyCookieManager;
import com.example.caitzh.minichat.MyDB.recentListDB;
import com.example.caitzh.minichat.MyDB.recordDB;
import com.example.caitzh.minichat.MyDB.userDB;
import com.example.caitzh.minichat.R;
import com.example.caitzh.minichat.User;
import com.example.caitzh.minichat.signIn;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import static com.example.caitzh.minichat.XingeManager.registerApplication;
import static com.example.caitzh.minichat.middlewares.Check.checkHasNet;

/**
 * 具体和某个人的聊天界面
 */

public class PersonalChatWindow extends AppCompatActivity {

    private ListView listView;
    private List<MiniChatMessage> mData;
    private PersonalChatWindowAdapter personalChatWindowAdapter;
    private EditText editText;
    private Button button;
    private String receiveid;
    private String messageContent;
    private recordDB myRecordDB;
    private recentListDB myRecentListDB;

    private static final int UPDATE_LIST_VIEW = 1;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_personal_chat_window);
        Bundle bundle = this.getIntent().getExtras();
        receiveid = bundle.getString("receiveid");
        myRecordDB = new recordDB(getBaseContext());
        myRecordDB.updateAllState(MyCookieManager.getUserId(), receiveid);
        setTitle(bundle.getString("receivenickname"));
        initViews();
        // 给AppCompatActivity的标题栏上加上返回按钮
        android.support.v7.app.ActionBar actionBar = getSupportActionBar();
        if(actionBar != null){
            actionBar.setHomeButtonEnabled(true);
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
        IntentFilter filter = new IntentFilter(MessageReceiver.PERSONALCHATWINDOWUPDATE);
        registerReceiver(broadcastReceiver, filter);
        myRecentListDB = new recentListDB(getApplicationContext());
    }

    // 返回按钮
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                this.finish(); // back button
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void initViews(){
        listView = (ListView)findViewById(R.id.PersonalChatWindowListView);
        editText = (EditText)findViewById(R.id.PersonalChatWindowInputBox);
        button = (Button)findViewById(R.id.PersonalChatWindowBtnSend);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                messageContent = editText.getText().toString();
                if (!messageContent.equals("")) {
                    if (checkHasNet(getApplicationContext())) {  // 判断当前是否有可用网络
                        editText.setText("");
                        sendRequestWithHttpConnection();  // 发送Http请求
                    } else {
                        Toast.makeText(getApplicationContext(), "当前没有可用网络", Toast.LENGTH_LONG).show();
                    }
                } else {
                    Toast.makeText(getApplicationContext(), "发送的消息不能为空", Toast.LENGTH_SHORT).show();
                }
            }
        });
        setAdapter();
    }

    private void setAdapter() {
        mData = LoadData();
        personalChatWindowAdapter = new PersonalChatWindowAdapter(getBaseContext(), mData, receiveid);
        listView.setAdapter(personalChatWindowAdapter);
        listView.setSelection(mData.size() - 1);
    }

    private List<MiniChatMessage> LoadData() {
        List<MiniChatMessage> Messages=new ArrayList<>();
        myRecordDB = new recordDB(getBaseContext());
        Cursor cursor = myRecordDB.getItems(MyCookieManager.getUserId(), receiveid);
        int count = cursor.getCount();
        if (count != 0 && cursor.moveToFirst()) {
            for (int i = 0; i < count; i++) {
                int messageType = cursor.getInt(cursor.getColumnIndex("type"));
                String messageContent = cursor.getString(cursor.getColumnIndex("content"));
                MiniChatMessage Message = new MiniChatMessage(messageType, messageContent);
                Messages.add(Message);
                cursor.moveToNext();
            }
        }
        return Messages;
    }

    private static final String url = "http://119.29.238.202:8000/send";
    private void sendRequestWithHttpConnection() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                HttpURLConnection connection = null;
                try {
                    Log.i("key", "Begin the connection");
                    // 获取一个HttpURLConnection实例化对象
                    connection = (HttpURLConnection)((new URL(url).openConnection()));
                    MyCookieManager.setCookie(connection);
                    // 设置请求方式和响应时间
                    connection.setRequestMethod("POST");
                    connection.setReadTimeout(8000);
                    connection.setConnectTimeout(8000);

                    // 获取登录时输入内容等参数，并将其以流的形式写入connection中
                    DataOutputStream outputStream = new DataOutputStream(connection.getOutputStream());
                    String messageUtf8 = URLEncoder.encode(messageContent, "utf-8");
                    outputStream.writeBytes("receiver=" + receiveid + "&message=" + messageUtf8);

                    // 提交到的数据转化为字符串
                    InputStream inputStream = connection.getInputStream();
                    BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
                    StringBuilder response = new StringBuilder();
                    String line;
                    while ((line = reader.readLine()) != null) {
                        response.append(line);
                    }
                    // 从返回的JSON数据中提取关键信息
                    JSONObject result = new JSONObject(response.toString());
                    String code = result.getString("code");
                    String message = result.getString("message");
                    // test
                    Log.i("code:", code);
                    Log.i("message", message);
                    if (code.equals("0")) {  // 发送成功
                        addTODBAndDataList();
                        Log.e("接收方的id", receiveid);
                        Message message_ = new Message();
                        message_.what = UPDATE_LIST_VIEW;
                        handler.sendMessage(message_);
                    } else {
                        Looper.prepare();
                        Toast.makeText(getApplicationContext(), "发送失败"+message, Toast.LENGTH_LONG).show();
                        Looper.loop();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                } finally {  // 关闭connection
                    if (connection != null)
                        connection.disconnect();
                }
            }
        }).start();
    }
    // 利用Handler来更新UI
    private Handler handler = new Handler() {
        public void handleMessage(Message message) {
            switch (message.what) {
                case UPDATE_LIST_VIEW:
                    try {
                        Log.e("setAdapter", "测试");
                        personalChatWindowAdapter.notifyDataSetChanged();
                        listView.setSelection(mData.size() - 1);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    break;
                default: break;
            }
        }
    };
    private void addTODBAndDataList() {
        String date = DataManager.getCurrentDate();
        myRecordDB.insertOne(0, MyCookieManager.getUserId(),
                receiveid, messageContent, date, true);
        myRecentListDB.insertOne(MyCookieManager.getUserId(), receiveid, DataManager.getCurrentDate());
        mData.add(new MiniChatMessage(0, messageContent));
    }

    BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {
            Log.e("收到的消息", intent.getStringExtra("content"));
            if (intent.getStringExtra("receiver").equals(receiveid)) {
                myRecordDB = new recordDB(getBaseContext());
                myRecordDB.updateAllState(MyCookieManager.getUserId(), receiveid);
                mData.add(new MiniChatMessage(1, intent.getStringExtra("content")));
                personalChatWindowAdapter.notifyDataSetChanged();
                listView.setSelection(mData.size() - 1);
            }
        }
    };


    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(broadcastReceiver);
    }
}
