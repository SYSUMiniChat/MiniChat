package com.example.caitzh.minichat;

import android.content.Context;
import android.content.Intent;
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
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;

public class changePassword extends AppCompatActivity {

    TextView miniNumber;
    Button btn_save;
    EditText password, confirmPassword;
    ImageView password_visible, confirm_visible;
    boolean visible = false, confirmVisible = false;
    String id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_password);

        miniNumber = (TextView) findViewById(R.id.miniNumber);
        btn_save = (Button) findViewById(R.id.save_password);
        password = (EditText) findViewById(R.id.password);
        confirmPassword = (EditText) findViewById(R.id.confirmPassword);
        password_visible = (ImageView) findViewById(R.id.visible);
        confirm_visible = (ImageView) findViewById(R.id.confirmVisible);

        Intent intent = this.getIntent();
        id = intent.getStringExtra("miniNumber");   // 获取跳转页面时传递的参数:mini账号
        miniNumber.setText(id);

        // 点击密码可见按钮
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


        // 点击保存按钮
        btn_save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String text_password = password.getText().toString();
                String text_confirm = confirmPassword.getText().toString();
                if (text_password.equals(text_confirm)) {  // 密码前后一致
                    if (checkHasNet(getApplicationContext())) {
                        sendRequestWithHttpConnection();
                    } else {
                        Toast.makeText(getApplicationContext(), "当前没有可用网络", Toast.LENGTH_LONG).show();
                    }
                } else {
                    Toast.makeText(changePassword.this, "密码前后不一致，请重新输入", Toast.LENGTH_LONG).show();
                    confirmPassword.setText("");
                }
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

    private static final String url = "http://119.29.238.202:8000/resetPassword";
    private void sendRequestWithHttpConnection() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                HttpURLConnection connection = null;
                try {
                    Log.i("key", "Begin the connection");
                    // 获取一个HttpURLConnection实例化对象
                    connection = (HttpURLConnection) ((new URL(url).openConnection()));
                    // 需要登录的操作在连接之前设置好cookie
                    MyCookieManager.setCookie(connection);
                    // 设置请求方式和响应时间
                    connection.setRequestMethod("POST");
                    connection.setReadTimeout(8000);
                    connection.setConnectTimeout(8000);

                    // 获取登录时输入内容等参数，并将其以流的形式写入connection中
                    String password_ = password.getText().toString();
                    DataOutputStream outputStream = new DataOutputStream(connection.getOutputStream());
                    SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
                    String date = simpleDateFormat.format(new java.util.Date());
                    outputStream.writeBytes("id=" + id + "&password=" + password_ + "&timestamp=" + date);

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
                    if (code.equals("0")) {  // 修改成功
                        Intent intent = new Intent(changePassword.this, personalInformation.class);
                        intent.putExtra("value", password.getText().toString());  // 传递修改后的内容
                        intent.putExtra("index", 5);
                        setResult(RESULT_FIRST_USER, intent);  // 返回code为修改密码对应的list下标
                        finish();  // 结束当前activity
                    }
                    Looper.prepare();
                    Toast.makeText(changePassword.this, message, Toast.LENGTH_LONG).show();
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
