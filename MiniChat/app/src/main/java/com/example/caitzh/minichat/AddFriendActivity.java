package com.example.caitzh.minichat;

import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
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
        // 先获取本地数据
        userDB userDB = new userDB(getApplicationContext());
        Cursor localUser = userDB.findOneByNumber(id);
        isupdate = true;
        if (localUser.moveToFirst()) {
            localTimeStamp = localUser.getString(localUser.getColumnIndex("finalDate"));
            if (!Check.hasUpdate(id, localTimeStamp)) {
                isupdate = false;
                avatar = localUser.getString(localUser.getColumnIndex("avatar"));
                nickname = localUser.getString(localUser.getColumnIndex("nickname"));
                sex = localUser.getString(localUser.getColumnIndex("sex"));
                city = localUser.getString(localUser.getColumnIndex("city"));
                signature = localUser.getString(localUser.getColumnIndex("signature"));
                details = new String[] {nickname, id, sex, city, signature};
                Log.e("Local avatar path is ", avatar);
                bm = ImageUtil.openImage(avatar);
            }
        }
        // 从服务器获取数据
        try {
            if (Check.checkHasNet(getBaseContext())) {
                if (isupdate == false) {
                    if (type == 0) {
                        mDownLatch = new CountDownLatch(1);
                        isFriend(id);
                    }
                    else mDownLatch = new CountDownLatch(0);
                    Log.e("GET Info from:", "localDB");
                } else {
                    if (type == 0) {
                        mDownLatch = new CountDownLatch(3);
                        getInfo(id);
                        isFriend(id);
                    } else {
                        mDownLatch = new CountDownLatch(2);
                        getInfo(id);
                    }
                    Log.e("GET Info from:", "Server");

                }
                mDownLatch.await();
                // 写入ListView的信息
                list = new ArrayList<>();
                for (int i = 0; i < 5; ++i) {
                    Map<String, String> listItem = new HashMap<>();
                    listItem.put("name", names[i]);
                    listItem.put("detail", details[i]);
                    list.add(listItem);
                }
            } else {
                Toast.makeText(AddFriendActivity.this, "当前无可用网络" , Toast.LENGTH_LONG).show();
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            button.setText(tips);
            simpleAdapter = new SimpleAdapter(getApplicationContext(), list, R.layout.personal_information_item,
                    new String[] {"name", "detail"}, new int[] {R.id.name, R.id.detail});
            if (listView == null) Log.e("listView is", " null");
            if (simpleAdapter == null) Log.e("simpleAdapter is", "null");
            listView.setAdapter(simpleAdapter);
            the_avatar.setImageBitmap(bm);
            button.setText(tips);
            if (type == 0) {
                // 隐藏拒绝同意
                twoButon.setVisibility(View.GONE);
            } else {
                // 隐藏发送消息
                button.setVisibility(View.GONE);
            }
        }

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
    private static final String queryInfo = "http://119.29.238.202:8000/query/";
    private static final String queryIsFriend = "http://119.29.238.202:8000/isFriend";
    private static  String avatar, city, nickname, sex, signature, tips = "test";
    private CountDownLatch mDownLatch;
    private void getInfo(final String id) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                HttpURLConnection connection = null;
                try {
                    connection = (HttpURLConnection) ((new URL(queryInfo+id).openConnection()));
                    // 设置请求方式和响应时间
                    connection.setRequestMethod("GET");
                    connection.setReadTimeout(8000);
                    connection.setConnectTimeout(8000);

                    // 取回的数据
                    InputStream inputStream = connection.getInputStream();
                    BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
                    StringBuilder response = new StringBuilder();
                    String line;
                    while ((line = reader.readLine()) != null) {
                        response.append(line);
                    }
                    // 从返回的json中提取信息
                    JSONObject result = new JSONObject(response.toString());
                    String code = result.getString("code");
                    String message = result.getString("message");
                    if (code.equals("0")) {
                        JSONObject information = new JSONObject(message);
                        localTimeStamp = information.getString("timestamp");
                        avatar = information.getString("avatar");
                        city = information.getString("city");
                        nickname = information.getString("nickname");
                        sex = information.getString("sex");
                        signature = information.getString("signature");
                        details = new String[] {nickname, id, sex, city, signature};
                    } else {
                        // 输出错误提示
                    }
                } catch (Exception e) {
                    Log.e("Error", "getInfo");
                    e.printStackTrace();
                } finally {
                    if (connection != null) connection.disconnect();
                    try {
                        bm = ImageUtil.getImage(avatar);
                    } catch (Exception e) {
                        e.printStackTrace();
                    } finally {
                        mDownLatch.countDown();
                    }
                    userDB db = new userDB(getApplicationContext());
                    // 删除后重新插入
                    db.deleteUser(id);
                    db.insert2Table(id, nickname, sex, city,signature,avatar,localTimeStamp);
                    mDownLatch.countDown();
                }
            }
        }).start();
    }
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
                } catch (Exception e) {
                    Log.e("Error", "idFriend");
                    e.printStackTrace();
                } finally {
                    if (connection != null) connection.disconnect();
                    mDownLatch.countDown();
                }
            }
        }).start();
    }

    private static Bitmap bm;

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
