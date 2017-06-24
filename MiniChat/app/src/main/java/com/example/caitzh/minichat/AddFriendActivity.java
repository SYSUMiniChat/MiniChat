package com.example.caitzh.minichat;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.GestureDetector;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Toast;

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
    private ImageView the_avatar;
    List<Map<String, String>> list;
    SimpleAdapter simpleAdapter;
    String[] names = new String[] {"昵称","Mini号","性别","地区","Mini签名"};
    String[] details;   // 存储个人信息页面每一栏的具体内容

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_friend);
        listView = (ListView) findViewById(R.id.addFriendListView);
        button = (Button) findViewById(R.id.add_friend_add_button);
        the_avatar = (ImageView) findViewById(R.id.add_friend_avatar);

        // 获取传递过来的id
        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();
        String id = bundle.getString("id");

        try {
            if (Check.checkHasNet(getBaseContext())) {
                mDownLatch = new CountDownLatch(3);
                getInfo(id);
                isFriend(id);
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
        }
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
                    mDownLatch.countDown();
                    getImage(avatar);
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
                        tips = "发送消息";
                    } else {
                        tips = "添加到好友列表";
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
    // 获取路径下的图片
    private void getImage(final String path) {
        Thread thread = new Thread() {
            @Override
            public void run() {
                HttpURLConnection conn = null;
                try {
                    // 获取服务器图片
                    URL url_getAvatar = new URL("http://119.29.238.202:8000" + path);
                    conn = (HttpURLConnection) url_getAvatar.openConnection();
                    conn.setRequestMethod("GET");
                    conn.setConnectTimeout(8000);
                    conn.setReadTimeout(8000);
                    conn.connect();
                    if (conn.getResponseCode() == 200) {
                        //获取服务器响应头中的流
                        InputStream is = conn.getInputStream();
                        //读取流里的数据，构建成bitmap位图
                        bm = BitmapFactory.decodeStream(is);

                    } else {
                        Log.i("获取服务器图片失败", "");
                    }
                } catch (Exception e) {
                    Log.e("Error", "getImage");
                    e.printStackTrace();
                } finally {
                    if (conn != null) conn.disconnect();
                    mDownLatch.countDown();
                }
            }
        };
        thread.start();

    }
}
