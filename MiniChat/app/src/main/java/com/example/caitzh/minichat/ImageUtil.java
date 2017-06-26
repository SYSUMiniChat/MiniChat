package com.example.caitzh.minichat;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Message;
import android.util.Log;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by Administrator on 2017/6/24.
 */
public class ImageUtil {
    public static String dir = "/sdcard/MiniChat";
    // 保存头像到本地
    public static void saveImage(String name, Bitmap bitmap) {

        File dirFirstFolder = new File(dir);
        //如果该文件夹不存在，则进行创建
        if(!dirFirstFolder.exists()) {
            dirFirstFolder.mkdirs();//创建文件夹
        }
        try {
            FileOutputStream out = new FileOutputStream(dir + "/" + name);
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, out);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // 获取路径下的图片
    public static Bitmap getImage(final String path) {
        try {
            // 获取服务器图片
            URL url_getAvatar = new URL("http://119.29.238.202:8000" + path);
            HttpURLConnection conn = (HttpURLConnection) url_getAvatar.openConnection();
            conn.setRequestMethod("GET");
            conn.setConnectTimeout(8000);
            conn.setReadTimeout(8000);
            conn.connect();
            if (conn.getResponseCode() == 200) {
                //获取服务器响应头中的流
                InputStream is = conn.getInputStream();
                //读取流里的数据，构建成bitmap位图
                Bitmap bm = BitmapFactory.decodeStream(is);
                saveImage(path.substring(path.lastIndexOf('/')+1),bm);
                return bm;

            } else {
                Log.i("获取服务器图片失败", "");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static Bitmap openImage(String path) {
        String localPath = dir+path.substring(path.lastIndexOf('/'));
        try {
            FileInputStream stream = new FileInputStream(localPath);
            Bitmap bitmap = BitmapFactory.decodeStream(stream);
            return bitmap;
        } catch (Exception e) {
            e.printStackTrace();
            // 如果本地图片不存在，则获取
            Bitmap bm = getImage(path);
            return bm;
        }
    }

}
