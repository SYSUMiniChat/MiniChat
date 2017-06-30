package com.example.caitzh.minichat.Util;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by littlestar on 2017/6/23.
 */
public class Check {
    public static boolean checkHasNet(Context context) {
        // 使用 ConnectivityManager 获取手机所有连接管理对象
        ConnectivityManager connectivityManager = (ConnectivityManager)
                context.getSystemService(context.CONNECTIVITY_SERVICE);
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

    public static boolean hasUpdate(String id, final String localTimeStamp) {
        final String url_getTimeStamp = "http://119.29.238.202:8000/getTimestamp/" + id;
        boolean hasUpdate = false;
        HttpURLConnection connection = null;
        try {
            Log.i("key", "Begin the connection");
            // 获取一个HttpURLConnection实例化对象
            connection = (HttpURLConnection) ((new URL(url_getTimeStamp).openConnection()));
            // 设置请求方式和响应时间
            connection.setRequestMethod("GET");
            connection.setReadTimeout(8000);
            connection.setConnectTimeout(8000);

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
            Log.i("Check update code:", code);
            Log.i("message", message);
            Log.e("localTimeStamp", localTimeStamp);
            Log.e("compareto", String.valueOf(message.compareTo(localTimeStamp)));
            if (code.equals("0") && message.compareTo(localTimeStamp) > 0) {
                Log.e("Check", "yes update");
                hasUpdate = true;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return hasUpdate;
    }
}
