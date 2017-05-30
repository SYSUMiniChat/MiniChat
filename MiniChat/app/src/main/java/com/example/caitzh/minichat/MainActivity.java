package com.example.caitzh.minichat;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.tencent.android.tpush.XGPushConfig;
import com.tencent.android.tpush.XGPushManager;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;

public class MainActivity extends AppCompatActivity {
    private static MainActivity instance = null;
    private final String url = "http://119.29.238.202:5000/";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        instance = this;

        // 开启logcat输出，方便debug，发布时请关闭
        XGPushConfig.enableDebug(this, true);
        // 如果需要知道注册是否成功，请使用registerPush(getApplicationContext(), XGIOperateCallback)带callback版本
        // 如果需要绑定账号，请使用registerPush(getApplicationContext(),account)版本
        // 具体可参考详细的开发指南
        // 传递的参数为ApplicationContext
        Context context = getApplicationContext();
        XGPushManager.registerPush(context);

        // 2.36（不包括）之前的版本需要调用以下2行代码
//        Intent service = new Intent(context, XGPushService.class);
//        context.startService(service);

        // 其它常用的API：
        // 绑定账号（别名）注册：
        XGPushManager.registerPush(context, "18819253615");
        //或registerPush(context,account, XGIOperateCallback)，其中account为APP账号，可以为任意字符串（qq、openid或任意第三方），业务方一定要注意终端与后台保持一致。
        // 取消绑定账号（别名）：registerPush(context,"*")，即account="*"为取消绑定，解绑后，该针对该账号的推送将失效
        // 反注册（不再接收消息）：unregisterPush(context)
        // 设置标签：setTag(context, tagName)
        // 删除标签：deleteTag(context, tagName)
        final TextView textView = (TextView)findViewById(R.id.message);
//        textView.setText("");
        if (savedInstanceState != null)
            Log.v("TEST", savedInstanceState.toString());

        Button send = (Button)findViewById(R.id.send);
        send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ConnectivityManager connMgr = (ConnectivityManager)
                        getSystemService(Context.CONNECTIVITY_SERVICE);
                NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
                InputMethodManager im = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                im.hideSoftInputFromWindow(((EditText) findViewById(R.id.sendMsg)).getWindowToken(), 0);
                if (networkInfo != null && networkInfo.isConnected()) {
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            final EditText msg = (EditText) findViewById(R.id.sendMsg);
                            String message = msg.getText().toString();

                            HttpURLConnection connection = null;
                            try {
                                String request = url + "?msg=" + URLEncoder.encode(message, "utf-8") + "&id=18819253615";
                                Log.v("TEST", "request: " + request);
                                connection = (HttpURLConnection) (new URL(request)).openConnection();
                                connection.setRequestMethod("GET");
                                connection.setReadTimeout(8000);
                                connection.setConnectTimeout(8000);

                                connection.connect();

                                InputStream in = connection.getInputStream();
                                BufferedReader reader = new BufferedReader(new InputStreamReader(in));
                                StringBuilder response = new StringBuilder();
                                String line;

                                while ((line = reader.readLine()) != null) {
                                    response.append(line);
                                }
                                Log.v("TEST", response.toString());
                                Toast.makeText(MainActivity.this, "发送成功", Toast.LENGTH_SHORT).show();

                            } catch (Exception e) {
                                Log.v("TEST", e.toString());
                            } finally {
                                if (connection != null) {
                                    connection.disconnect();
                                }
                            }
                        }

                    }).start();
                    textView.setText(textView.getText().toString() + "\n[发送]:" +((EditText)findViewById(R.id.sendMsg)).getText().toString());
                } else {
                    Toast.makeText(MainActivity.this, "当前没有可用网络！", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }




    static MainActivity getInstance() {
        return instance;
    }

}
