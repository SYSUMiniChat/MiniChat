package com.example.caitzh.minichat;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Looper;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;

public class register extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        try {
            // 点击 注册按钮
            Button btn_register = (Button) findViewById(R.id.register);
            btn_register.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // 判断输入密码前后是否一致
                    String password = ( (EditText) findViewById(R.id.password)).getText().toString();
                    String confirmPassword = ( (EditText) findViewById(R.id.confirmPassword)).getText().toString();
                    if (password.equals(confirmPassword)) {  // 密码前后一致
                        if (checkHasNet(getApplicationContext())) {  // 判断当前是否有可用网络
                            sendRequestWithHttpConnection();  // 发送Http请求
                        } else {
                            Toast.makeText(getApplicationContext(), "当前没有可用网络", Toast.LENGTH_LONG).show();
                        }
                    } else {
                        ((EditText) findViewById(R.id.confirmPassword)).setText(""); // 为方便重新用户输入密码，清空输入内容
                        Toast.makeText(register.this, "前后密码不一致，请重新填写喔~", Toast.LENGTH_LONG).show();
                    }
                }
            });

            // 点击"已注册，前往登录"
            TextView gotoSinIn = (TextView) findViewById(R.id.gotoSignIn);
            gotoSinIn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    finish();  // 结束当前activity
                    Intent intent = new Intent(register.this, signIn.class); // 跳转到登录页面
                    startActivity(intent);
                }
            });

            // 给AppCompatActivity的标题栏上加上返回按钮
            android.support.v7.app.ActionBar actionBar = getSupportActionBar();
            if (actionBar != null) {
                actionBar.setHomeButtonEnabled(true);
                actionBar.setDisplayHomeAsUpEnabled(true);
            }
        } catch (Exception e) {
            Log.e("error", e.toString());
        }
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

    // 判断是否有可用网络
    private boolean checkHasNet(Context context) {
        // 使用 ConnectivityManager 获取手机所有连接管理对象
        ConnectivityManager connectivityManager = (ConnectivityManager)
                getApplicationContext().getSystemService(context.CONNECTIVITY_SERVICE);
        if (connectivityManager != null) {
            // 使用 manager 获取网络连接管理的NetworkInfo对象
            NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
            if (networkInfo == null || !networkInfo.isAvailable()) {  // 是否为空或为非连接状态
                return false;
            } else {
                return true;
            }
        }
        return false;
    }

    private static final String url = "http://119.29.238.202:8000/register";

    private void sendRequestWithHttpConnection() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                HttpURLConnection connection = null;
                try {
                    Log.i("key", "Begin the connection");
                    // 获取一个HttpURLConnection实例化对象
                    connection = (HttpURLConnection) ((new URL(url).openConnection()));
                    // 设置请求方式和响应时间
                    connection.setRequestMethod("POST");
                    connection.setReadTimeout(8000);
                    connection.setConnectTimeout(8000);
                    // 获取注册时输入内容等参数，并将其以流的形式写入connection中
                    String nickname = ((EditText) findViewById(R.id.nickname)).getText().toString();
                    String phone = ((EditText) findViewById(R.id.miniNumber)).getText().toString();
                    String password = ((EditText) findViewById(R.id.password)).getText().toString();
                    DataOutputStream outputStream = new DataOutputStream(connection.getOutputStream());
                    nickname = URLEncoder.encode(nickname, "utf-8");
                    outputStream.writeBytes("phone=" + phone + "&nickname=" + nickname + "&password=" + password);

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

                    Log.i("code:", code);
                    Log.i("message", message);

                    if (code.equals("0")) {  // 注册成功
                        finish();  // 结束当前activity
                        Intent intent = new Intent(register.this, signIn.class); // 跳转到登录页面
                        startActivity(intent);
                        // 在子线程中使用Looper弹出Toast内容
                        Looper.prepare();
                        Toast.makeText(register.this, "注册成功，赶快登录吧~", Toast.LENGTH_LONG).show();
                        Looper.loop();
                    } else {  // 注册失败
                        Looper.prepare();
                        Toast.makeText(register.this, message, Toast.LENGTH_LONG).show();  // 弹出注册失败原因
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
}
