package com.example.caitzh.minichat;

import android.content.Context;
import android.database.Cursor;
import android.util.Log;

import com.example.caitzh.minichat.MyDB.userDB;
import com.example.caitzh.minichat.middlewares.Check;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;

/**
 * Created by littlestar on 2017/6/25.
 */
public class DataManager {
    private static final String queryInfo = "http://119.29.238.202:8000/query/";
    public static User getLatestData(Context context, String id_) {
        boolean isexist = false;
        userDB db = new userDB(context);
        User user = db.findUserById(id_);
        if (user != null) {
            isexist = true;
            String timeStamp = user.getFinalDate();
            if (!Check.hasUpdate(id_, timeStamp)) {
                Log.e("UserInfoDate", user.getFinalDate());
                return user;
            }
        }
        if (Check.checkHasNet(context)) {
            Log.e("UserInfo", "GET from server");
            try {
                HttpURLConnection connection = null;
                connection = (HttpURLConnection) ((new URL(queryInfo+id_).openConnection()));
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
                    JSONObject information = new JSONObject(message);
                    user = new User();
                    user.setId(id_);
                    user.setFinalDate(information.getString("timestamp"));
                    user.setAvatar(information.getString("avatar"));
                    user.setCity(information.getString("city"));
                    user.setNickname(information.getString("nickname"));
                    user.setSex(information.getString("sex"));
                    user.setSignature(information.getString("signature"));
                    if (isexist) {
                        // 更新
                        db.updateUser(user);
                    } else {
                        // 插入
                        db.insertUser(user);
                    }
                    ImageUtil.getImage(user.getAvatar());
                } else {
                    // 输出错误提示
                }
                connection.disconnect();
                // 获取头像并保存
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                return user;
            }
        } else {
            return null;
        }
    }
    public static String getCurrentDate() {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        return simpleDateFormat.format(new java.util.Date());
    }
}
