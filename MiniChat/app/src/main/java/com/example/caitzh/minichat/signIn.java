package com.example.caitzh.minichat;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Looper;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

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

public class signIn extends AppCompatActivity {

    EditText miniNumber, password;
    View view1, view2;
    ImageView password_visible;
    boolean visible = false;
    Button signin_btn;
    TextView forgetPw,gotoRegister;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);

        miniNumber = (EditText) findViewById(R.id.miniNumber);
        password = (EditText) findViewById(R.id.password);
        view1 = findViewById(R.id.view1);
        view2 = findViewById(R.id.view2);
        password_visible = (ImageView) findViewById(R.id.visible);
        signin_btn = (Button) findViewById(R.id.signIn);
        forgetPw = (TextView) findViewById(R.id.forgetPassword);
        gotoRegister = (TextView) findViewById(R.id.gotoRegister);

        // 点击密码可见图片
        password_visible.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!visible) {
                    password.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                    password_visible.setImageDrawable(getResources().getDrawable(R.mipmap.visible));
                } else {
                    password.setTransformationMethod(PasswordTransformationMethod.getInstance());
                    password_visible.setImageDrawable(getResources().getDrawable(R.mipmap.invisible));
                }
                visible = !visible;
                password.postInvalidate();
                password.setSelection(password.length());
            }
        });

        // 这里只是为了实现在输入账号和密码的时候，下面的下划线颜色会由灰变绿的效果
        // TODO 这里颜色变化有bug
        miniNumber.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                view1.setBackgroundResource(R.color.colorLightGreen);
                view2.setBackgroundResource(R.color.colorGray);
            }
        });
        password.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                view1.setBackgroundResource(R.color.colorGray);
                view2.setBackgroundResource(R.color.colorLightGreen);
            }
        });

        // 点击登录按钮
        signin_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (checkHasNet(getApplicationContext())) {  // 判断当前是否有可用网络
                    sendRequestWithHttpConnection();  // 发送Http请求
                } else {
                    Toast.makeText(getApplicationContext(), "当前没有可用网络", Toast.LENGTH_LONG).show();
                }
            }
        });

        // 点击 "忘记密码",跳转到忘记密码页面
        forgetPw.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(signIn.this, forgetPassword.class);
                startActivity(intent);
            }
        });

        // 点击 "前往注册"，跳转到注册页面
        gotoRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();  // 结束当前activity
                Intent intent = new Intent(signIn.this, register.class);
                startActivity(intent);
            }
        });

        // 给AppCompatActivity的标题栏上加上返回按钮
        android.support.v7.app.ActionBar actionBar = getSupportActionBar();
        if(actionBar != null){
            actionBar.setHomeButtonEnabled(true);
            actionBar.setDisplayHomeAsUpEnabled(true);
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

    private static final String url = "http://119.29.238.202:8000/login";
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

                    // 获取登录时输入内容等参数，并将其以流的形式写入connection中
                    String id = miniNumber.getText().toString();
                    String password_ = password.getText().toString();
                    DataOutputStream outputStream = new DataOutputStream(connection.getOutputStream());
                    outputStream.writeBytes("id=" + id + "&password=" + password_);

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
                    if (code.equals("0")) {  // 登录成功
                        finish();  // 结束当前activity
                        // TODO 跳转到用户信息页面
                        Intent intent = new Intent(signIn.this, personalInformation.class);
                        intent.putExtra("state", "login");  // 表明状态是已登录
                        startActivity(intent);
                    }
                    Looper.prepare();
                    Toast.makeText(signIn.this, message, Toast.LENGTH_LONG).show();
                    Looper.loop();
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
