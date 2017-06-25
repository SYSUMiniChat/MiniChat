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

import com.example.caitzh.minichat.MyDB.userDB;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;

import static com.example.caitzh.minichat.middlewares.Check.checkHasNet;


public class changeName extends AppCompatActivity {
    EditText editText;
    Button btn_save;
    String parameter, detail;
    TextView goodName;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_name);

        editText = (EditText)findViewById(R.id.edit_change_name);
        btn_save = (Button) findViewById(R.id.save_name);
        goodName = (TextView) findViewById(R.id.goodName);

        Bundle bundle = this.getIntent().getExtras();
        if (bundle != null) {
            parameter = bundle.getString("parameter");  // 获取的参数有：昵称、地址、签名
            detail = bundle.getString("detail");        // 传递的文字内容
            editText.setText(detail);  // 默认内容为个人信息
        }
        editText.setSelection(editText.length());  // 设置光标在最后

        // 修改ActionBar标题和文本内容
        if (parameter.equals("address")) {
            setTitle("更改地区");
            goodName.setText("地址可以让你的朋友更容易找到你。");
        } else if (parameter.equals("signature")) {
            setTitle("Mini签名");
            goodName.setText("好签名可以让你的朋友更容易记住你。");
        }

        // 点击保存按钮
        btn_save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (checkHasNet(getApplicationContext())) {
                    sendRequestWithHttpConnection();
                } else {
                    Toast.makeText(getApplicationContext(), "当前没有可用网络", Toast.LENGTH_LONG).show();
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

    private static final String url = "http://119.29.238.202:8000/updateUser";
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
                    String input = editText.getText().toString();
                    DataOutputStream outputStream = new DataOutputStream(connection.getOutputStream());
                    input = URLEncoder.encode(input, "utf-8");
                    SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
                    String date = simpleDateFormat.format(new java.util.Date());
                    switch (parameter) {
                        case "name":
                            outputStream.writeBytes("nickname=" + input + "&timestamp=" + date); break;
                        case "address":
                            outputStream.writeBytes("city=" + input + "&timestamp=" + date); break;
                        case "signature":
                            outputStream.writeBytes("signature=" + input + "&timestamp=" + date); break;
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
                    if (code.equals("0")) {  // 修改成功
                        Intent intent = new Intent(changeName.this, personalInformation.class);
                        intent.putExtra("value", editText.getText().toString());  // 传递修改后的内容
                        // 同时修改本地数据库
                        userDB db = new userDB(getBaseContext());
                        switch (parameter) {
                            case "name":
                                db.updateInfo(MyCookieManager.getUserId(), "nickname", editText.getText().toString(), date);
                                intent.putExtra("index", 0);
                                break;
                            case "address":
                                db.updateInfo(MyCookieManager.getUserId(), "city", editText.getText().toString(), date);
                                intent.putExtra("index", 3);
                                break;
                            case "signature":
                                db.updateInfo(MyCookieManager.getUserId(), "signature", editText.getText().toString(), date);
                                intent.putExtra("index", 4);
                                break;
                        }
                        setResult(RESULT_FIRST_USER, intent);
                        finish();  // 结束当前activity
                    }
                    Looper.prepare();
                    Toast.makeText(changeName.this, message, Toast.LENGTH_LONG).show();
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
