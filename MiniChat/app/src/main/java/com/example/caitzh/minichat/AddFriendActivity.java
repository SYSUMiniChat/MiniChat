package com.example.caitzh.minichat;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.GestureDetector;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Toast;

import com.example.caitzh.minichat.MyDB.recentListDB;
import com.example.caitzh.minichat.MyDB.userDB;
import com.example.caitzh.minichat.crh.PersonalChatWindow;
import com.example.caitzh.minichat.middlewares.Check;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CountDownLatch;

/**
 * Created by littlestar on 2017/6/23.
 */
public class AddFriendActivity extends AppCompatActivity {

    private ListView listView;
    private Button button;
    private Button refuse, agree;
    private ImageView the_avatar;
    private String localTimeStamp;
    private LinearLayout twoButon;
    private int type; // type = 1时为添加请求 type=0时为其他
    List<Map<String, String>> list;
    SimpleAdapter simpleAdapter;
    String[] names = new String[] {"昵称","Mini号","性别","地区","Mini签名"};
    String[] details;   // 存储个人信息页面每一栏的具体内容

    private static boolean isupdate = false;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_friend);
        listView = (ListView) findViewById(R.id.addFriendListView);
        button = (Button) findViewById(R.id.add_friend_add_button);
        the_avatar = (ImageView) findViewById(R.id.add_friend_avatar);
        refuse = (Button) findViewById(R.id.addRefuse);
        agree = (Button) findViewById(R.id.addAgree);
        twoButon = (LinearLayout) findViewById(R.id.addTwoButton);

        // 获取传递过来的id
        Intent intent = getIntent();
        final Bundle bundle = intent.getExtras();
        final String id = bundle.getString("id");
        type = bundle.getInt("type"); // type = 1时为添加请求 type=0时为其他
        // 设置哪个按钮不可见
        if (type == 0) {
            twoButon.setVisibility(View.GONE);
            isFriend(id);
        } else {
            button.setVisibility(View.GONE);
        }

        setListView(id);
        // 设置点击事件
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (Check.checkHasNet(getApplicationContext())) {
                    if (button.getText().toString().equals("发送消息")) {
                        // 跳转到聊天界面
                        Bundle bundle = new Bundle();
                        bundle.putString("receiveid", id);
                        bundle.putString("receivenickname", nickname);
                        Intent intent1 = new Intent(AddFriendActivity.this, PersonalChatWindow.class);
                        intent1.putExtras(bundle);
                        recentListDB db = new recentListDB(getBaseContext());
                        db.insertOne(MyCookieManager.getUserId(), id);
                        finish();
                        startActivity(intent1);
                    } else {
                        // 发送请求
                        if (Check.checkHasNet(getApplicationContext())) {
                            Toast.makeText(AddFriendActivity.this, "正在发送请求", Toast.LENGTH_LONG).show();
                            sendRequire(id);
                        } else {
                            Toast.makeText(AddFriendActivity.this, "当前无可用网络", Toast.LENGTH_LONG).show();
                        }
                    }
                } else {
                    Toast.makeText(AddFriendActivity.this, "当前无可用网络" , Toast.LENGTH_LONG).show();
                }
            }
        });

        refuse.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(AddFriendActivity.this, "点击了拒绝" , Toast.LENGTH_LONG).show();
                sendRefuse(id);
            }
        });

        agree.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(AddFriendActivity.this, "点击了同意" , Toast.LENGTH_LONG).show();
                sendAgree(id);
            }
        });
    }
    private static final String queryIsFriend = "http://119.29.238.202:8000/isFriend";
    private static  String avatar, city, nickname, sex, signature, tips = "test";
    private static final int USER_DATA = 1;
    private static final int USER_AVATAR = 2;
    private static final int CLICK_BUTTON = 3;
    private static User user;

    private void setListView(final String id_) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                user = DataManager.getLatestData(getApplicationContext(), id_); // 获取用户信息
                // 正常取得用户资料
                if (user != null) {
                    details = new String[]{user.getNickname(), id_, user.getSex(), user.getCity(), user.getSignature()};
                    // 写入ListView的信息
                    list = new ArrayList<>();
                    for (int i = 0; i < 5; ++i) {
                        Map<String, String> listItem = new HashMap<>();
                        listItem.put("name", names[i]);
                        listItem.put("detail", details[i]);
                        list.add(listItem);
                    }
                    Message message_ = new Message();
                    message_.what = USER_DATA;
                    handler.sendMessage(message_);

                } else {
                    Looper.prepare();
                    Toast.makeText(AddFriendActivity.this, "无法获取用户数据,请确保网络通畅", Toast.LENGTH_LONG).show();
                    Looper.loop();
                }
            }
        }).start();
    }

    // 利用Handler 更新数据
    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case USER_DATA:
                    Bitmap bitmap = ImageUtil.openImage(user.getAvatar());
                    Log.e("头像设置", "开始");
                    the_avatar.setImageBitmap(bitmap);
                    Log.e("头像设置", "结束");
                    simpleAdapter = new SimpleAdapter(getApplicationContext(), list, R.layout.personal_information_item,
                            new String[] {"name", "detail"}, new int[] {R.id.name, R.id.detail});
                    if (simpleAdapter == null) Log.e("simpleAdapter is", "null");
                    listView.setAdapter(simpleAdapter);
                    break;
                case CLICK_BUTTON:
                    button.setText(tips);
                    break;
                default: break;
            }
        }
    };
    private void isFriend(final String id) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                HttpURLConnection connection = null;
                try {
                    connection = (HttpURLConnection) ((new URL(queryIsFriend).openConnection()));
                    MyCookieManager.setCookie(connection);
                    // 设置请求方式和响应时间
                    connection.setRequestMethod("POST");
                    connection.setReadTimeout(8000);
                    connection.setConnectTimeout(8000);

                    DataOutputStream dataOutputStream = new DataOutputStream(connection.getOutputStream());
                    dataOutputStream.writeBytes("friend="+id);

                    // 取回的数据
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
                    // code == 0时 已经是好友
                    if (code.equals("0")) {
                        if (message.equals("yes")) tips = "发送消息";
                        else tips = "添加到好友列表";
                    } else {
                        tips = "您未登录";
                    }
                    // 发送消息使更新button显示的内容
                    Message message1 = new Message();
                    message1.what = CLICK_BUTTON;
                    handler.sendMessage(message1);
                } catch (Exception e) {
                    Log.e("Error", "idFriend");
                    e.printStackTrace();
                } finally {
                    if (connection != null) connection.disconnect();
                }
            }
        }).start();
    }


    private void sendRequire(final String id) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                AccessServerUtil.sendMessage(AccessServerUtil.ADD_TYPE, id, "");
            }
        }).start();
    }
    private void sendAgree(final String id) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                AccessServerUtil.sendMessage(AccessServerUtil.ANSWER_TYPE, id, "yes");
            }
        }).start();
    }
    private void sendRefuse(final String id) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                AccessServerUtil.sendMessage(AccessServerUtil.ANSWER_TYPE, id, "no");
            }
        }).start();
    }

}
