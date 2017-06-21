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

public class forgetPassword extends AppCompatActivity {

    Button btn_getCode;
    EditText miniNumber, code;
    TextView nextStep;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forget_password);

        btn_getCode = (Button) findViewById(R.id.getCode);
        code = (EditText) findViewById(R.id.code);
        miniNumber = (EditText) findViewById(R.id.miniNumber);
        nextStep = (TextView) findViewById(R.id.nextStep);

        // 给AppCompatActivity的标题栏上加上返回按钮
        android.support.v7.app.ActionBar actionBar = getSupportActionBar();
        if(actionBar != null){
            actionBar.setHomeButtonEnabled(true);
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        // 点击 获取验证码 按钮
        btn_getCode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                btn_getCode.setText("发送中...");
                if (checkHasNet(getApplicationContext())) {
                     sendRequestWithHttpConnection(url_getcode, "GET");
                } else {
                     Toast.makeText(getApplicationContext(), "当前没有可用网络", Toast.LENGTH_LONG).show();
                }
            }
        });

        // 点击下一步
        nextStep.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 判断验证码是否正确
                if (checkHasNet(getApplicationContext())) {
                    sendRequestWithHttpConnection(url_verifycode, "POST");
                } else {
                    Toast.makeText(getApplicationContext(), "当前没有可用网络", Toast.LENGTH_LONG).show();
                }
            }
        });

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

    private static final String url_getcode = "http://119.29.238.202:8000/getVerifycode/";
    private static final String url_verifycode = "http://119.29.238.202:8000/verifyCode";

    private void sendRequestWithHttpConnection(final String url, final String method) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                HttpURLConnection connection = null;
                try {
                    Log.i("key", "Begin the connection");
                    String id = miniNumber.getText().toString();
                    // 获取一个HttpURLConnection实例化对象
                    if (url.equals(url_getcode))
                        connection = (HttpURLConnection) ((new URL(url + id).openConnection()));
                    else if (url.equals(url_verifycode))
                        connection = (HttpURLConnection) ((new URL(url).openConnection()));
                    // 设置请求方式和响应时间
                    connection.setRequestMethod(method);
                    connection.setReadTimeout(8000);
                    connection.setConnectTimeout(8000);
                    if (url.equals(url_verifycode)) {
                        String code_ = code.getText().toString();
                        DataOutputStream outputStream = new DataOutputStream(connection.getOutputStream());
                        outputStream.writeBytes("id=" + id + "&code=" + code_);
                    }
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
                    if (code.equals("0")) {
                        if (url.equals(url_verifycode)) {
                            // 验证码正确，跳转到修改密码页面
                            finish();
                            Intent intent = new Intent(forgetPassword.this, changePassword.class);
                            intent.putExtra("miniNumber", miniNumber.getText().toString());
                            startActivity(intent);
                        } else if (url.equals(url_getcode)) {
                            btn_getCode.setText("发送成功!");
                        }
                    }
                    // 弹出提示消息
                    Looper.prepare();
                    Toast.makeText(forgetPassword.this, message, Toast.LENGTH_LONG).show();
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
