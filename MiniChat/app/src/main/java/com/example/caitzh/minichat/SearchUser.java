package com.example.caitzh.minichat;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.caitzh.minichat.middlewares.Check;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.concurrent.CountDownLatch;

/**
 * Created by littlestar on 2017/6/24.
 */
public class SearchUser extends AppCompatActivity {
    EditText editText;
    Button btn_search;
    Boolean exist = false;

    private CountDownLatch countDownLatch;
    private static final String queryInfo = "http://119.29.238.202:8000/query/";
    private static String inputId = "";
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_user);
        editText = (EditText)findViewById(R.id.search_id);
        btn_search = (Button) findViewById(R.id.thesearch_button);

        // 点击搜索按钮时
        btn_search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                inputId = editText.getText().toString();
                if (inputId.isEmpty()) {
                    Toast.makeText(getApplicationContext(), "请输入您要搜索的账号", Toast.LENGTH_LONG).show();
                }else if (Check.checkHasNet(getApplicationContext())) {
                    //  搜索
                    try {
                        countDownLatch = new CountDownLatch(1);
                        queryUser(inputId);
                        countDownLatch.await();
                        if (exist == true) {
                            // 页面跳转
                            Bundle bundle = new Bundle();
                            bundle.putString("id", inputId);
                            Intent intent = new Intent(SearchUser.this, AddFriendActivity.class);
                            intent.putExtras(bundle);
                            startActivity(intent);
                        } else {
                            Toast.makeText(getApplicationContext(), "当前账号不存在", Toast.LENGTH_LONG).show();
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
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

    private void queryUser(final String id) {
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
                        Log.e("jinru","code=0");
                        exist = true;
                    } else {
                        exist = false;
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    if (connection != null) connection.disconnect();
                    countDownLatch.countDown();
                }
            }
        }).start();

    }

}
