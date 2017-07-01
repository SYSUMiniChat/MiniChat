package com.example.caitzh.minichat;

import android.content.Intent;
import android.os.Looper;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
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
import java.text.SimpleDateFormat;

import static com.example.caitzh.minichat.Util.XingeManager.registerApplication;
import static com.example.caitzh.minichat.Util.Check.checkHasNet;

public class register extends AppCompatActivity {

    EditText nickname, miniNumber, password, confirmPassword, verifyCode;
    ImageView password_visible, confirm_visible;
    boolean visible = false;
    boolean confirmVisible = false;
    Button getCode;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        nickname = (EditText) findViewById(R.id.nickname);
        miniNumber = (EditText) findViewById(R.id.miniNumber);
        password = (EditText) findViewById(R.id.password);
        confirmPassword = (EditText) findViewById(R.id.confirmPassword);
        password_visible = (ImageView) findViewById(R.id.passwordVisible);
        confirm_visible = (ImageView) findViewById(R.id.confirmPasswordVisible);
        verifyCode = (EditText) findViewById(R.id.verificationCode);

        registerApplication(getApplicationContext());

        try {
            // 点击密码可见按钮，切换输入框密码是否可见
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
            confirm_visible.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (!confirmVisible) {
                        confirmPassword.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                        confirm_visible.setImageDrawable(getResources().getDrawable(R.mipmap.visible));
                    } else {
                        confirmPassword.setTransformationMethod(PasswordTransformationMethod.getInstance());
                        confirm_visible.setImageDrawable(getResources().getDrawable(R.mipmap.invisible));
                    }
                    confirmVisible = !confirmVisible;
                    confirmPassword.postInvalidate();
                    confirmPassword.setSelection(confirmPassword.length());
                }
            });

            getCode = (Button) findViewById(R.id.getCode);
            // 点击 获取验证码 按钮
            getCode.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    getCode.setText("发送中...");
                    Toast.makeText(getApplicationContext(), "验证码已发送", Toast.LENGTH_LONG).show();
                    if (checkHasNet(getApplicationContext())) {   // 判断当前是否有可用网络
                        sendRequestWithHttpConnection(url_getCode, "GET");  // 发送get请求
                    } else {
                        Toast.makeText(getApplicationContext(), "当前没有可用网络", Toast.LENGTH_LONG).show();
                    }
                }
            });

            // 点击 注册按钮
            Button btn_register = (Button) findViewById(R.id.register);
            btn_register.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // 判断输入密码前后是否一致
                    Toast.makeText(getApplicationContext(), "点击注册按钮", Toast.LENGTH_LONG).show();
                    String password_ = password.getText().toString();
                    String confirmPassword_ = confirmPassword.getText().toString();
                    if (password_.equals(confirmPassword_)) {  // 密码前后一致
                        if (checkHasNet(getApplicationContext())) {  // 判断当前是否有可用网络

                            sendRequestWithHttpConnection(url_register, "POST");  // 发送post请求

                        } else {
                            Toast.makeText(getApplicationContext(), "当前没有可用网络", Toast.LENGTH_LONG).show();
                        }
                    } else {
                        confirmPassword.setText(""); // 为方便重新用户输入密码，清空输入内容
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
                }
            });

        } catch (Exception e) {
            Log.e("error", e.toString());
        }
    }

    private static final String url_register = "http://119.29.238.202:8000/register";
    private static final String url_getCode = "http://119.29.238.202:8000/getVerifycode/";

    private void sendRequestWithHttpConnection(final String url, final String method) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                HttpURLConnection connection = null;
                try {
                    Log.i("key", "Begin the connection");
                    // 获取一个HttpURLConnection实例化对象
                    String id = miniNumber.getText().toString();
                    if (url.equals(url_register)) {
                        Log.i("cur url:", url);
                        connection = (HttpURLConnection) ((new URL(url).openConnection()));
                    } else if (url.equals(url_getCode)) {
                        Log.i("cur url:", url+id);
                        connection = (HttpURLConnection) ((new URL(url+id).openConnection()));
                    }

                    // 设置请求方式和响应时间
                    connection.setRequestMethod(method);
                    connection.setReadTimeout(8000);
                    connection.setConnectTimeout(8000);
                    String nickname_="", password_="", verifyCode_="", date="";
                    if (url.equals(url_register)) {
                        // 获取注册时输入内容等参数，并将其以流的形式写入connection中
                        nickname_ = nickname.getText().toString();
                        password_ = password.getText().toString();
                        verifyCode_ = verifyCode.getText().toString();
                        DataOutputStream outputStream = new DataOutputStream(connection.getOutputStream());
                        nickname_ = URLEncoder.encode(nickname_, "utf-8");
                        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                        date = simpleDateFormat.format(new java.util.Date());
                        outputStream.writeBytes("id=" + id + "&nickname=" + nickname_ + "&password=" + password_ + "&code=" + verifyCode_  + "&timestamp=" + date);
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
                        if (url.equals(url_register)) {   // 注册成功
                            finish();  // 结束当前activity
                        } else {
                            getCode.setText("发送成功!");
                        }
                    }
                    // 在子线程中弹出Toast内容需使用Looper
                    Looper.prepare();
                    Toast.makeText(register.this, message, Toast.LENGTH_LONG).show();
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


