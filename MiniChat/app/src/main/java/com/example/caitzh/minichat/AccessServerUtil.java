package com.example.caitzh.minichat;

import android.util.Log;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by littlestar on 2017/6/25.
 */
public class AccessServerUtil {
    private static String ip = "http://119.29.238.202:8000";
    private static String addRequest = "/friend/addRequest";
    private static String answer = "/friend/answer";
    private static String send = "/send";
    private static String post = "POST";
    public final static int SEND_TYPE = 0;
    public final static int ADD_TYPE = 1;
    public final static int ANSWER_TYPE = 2;
    public static void sendMessage(int type, String id, String message) {
        HttpURLConnection connection = null;
        switch (type) {
            case SEND_TYPE: {
                Log.e("AccessServerUtil", "send");
                try {
                    connection = (HttpURLConnection) ((new URL(ip + send).openConnection()));
                    // 设置请求方式和响应时间
                    connection.setRequestMethod(post);
                    connection.setReadTimeout(8000);
                    connection.setConnectTimeout(8000);
                    // 数据写入
                    DataOutputStream outputStream = new DataOutputStream(connection.getOutputStream());
                    outputStream.writeBytes("receiver=" + id + "&message=" + message);
                    // 获取返回的数据
                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    if (connection != null) connection.disconnect();
                    break;
                }
            }
            case ADD_TYPE: {
                Log.e("AccessServerUtil", "add");
                try {
                    connection = (HttpURLConnection)((new URL(ip+addRequest).openConnection()));
                    // 设置请求方式和响应时间
                    MyCookieManager.setCookie(connection);
                    connection.setRequestMethod(post);
                    connection.setReadTimeout(8000);
                    connection.setConnectTimeout(8000);
                    Log.e("AccessServerUtil", "connect");
                    // 数据写入
                    DataOutputStream outputStream = new DataOutputStream(connection.getOutputStream());
                    outputStream.writeBytes("friend=" + id);
                    Log.e("AccessServerUtil", "writeData");
                    // 获取返回的数据
                    InputStream inputStream = connection.getInputStream();
                    BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
                    StringBuilder response = new StringBuilder();
                    String line;
                    Log.e("AccessServerUtil", "getData");
                    while ((line = reader.readLine()) != null) {
                        response.append(line);
                    }
                    // 从返回的JSON数据中提取关键信息
                    JSONObject result = new JSONObject(response.toString());
                    String code = result.getString("code");
                    String message_ = result.getString("message");
                    Log.e("Access Code:", code);
                    Log.e("Access message:", message_);
                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    if (connection != null) connection.disconnect();
                    break;
                }
            }
            case ANSWER_TYPE: {
                Log.e("AccessServerUtil", "answer");
                try {
                    connection = (HttpURLConnection)((new URL(ip+answer).openConnection()));
                    // 设置请求方式和响应时间
                    connection.setRequestMethod(post);
                    connection.setReadTimeout(8000);
                    connection.setConnectTimeout(8000);
                    // 数据写入
                    DataOutputStream outputStream = new DataOutputStream(connection.getOutputStream());
                    outputStream.writeBytes("friend=" + id+"&answer="+message);
                    // 获取返回的数据
                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    if (connection != null) connection.disconnect();
                    break;
                }
            }
            default: break;
        }
    }
}
