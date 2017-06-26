package com.example.caitzh.minichat.crh;

import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.caitzh.minichat.ImageUtil;
import com.example.caitzh.minichat.MyCookieManager;
import com.example.caitzh.minichat.MyDB.userDB;
import com.example.caitzh.minichat.R;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.CookieManager;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;

import static com.example.caitzh.minichat.middlewares.Check.checkHasNet;
import static com.example.caitzh.minichat.middlewares.Check.hasUpdate;

/**
 * Created by Administrator on 2017/6/20 0020.
 */

public class PersonalChatWindowAdapter extends BaseAdapter{
    private Context mContext;
    private List<MiniChatMessage> mData;
    private String receiveid;
    private ImageView avader;
    private userDB myUserDB;
    private Bitmap receiverBitmap = null;
    private Bitmap senderBitmap = null;
    private String url1 = "";
    private String url2 = "";

    public PersonalChatWindowAdapter(Context context,List<MiniChatMessage> data, String inputid)
    {   this.receiveid = inputid;
        this.mContext=context;
        this.mData=data;
    }


    @Override
    public int getCount()
    {
        return mData.size();
    }

    @Override
    public Object getItem(int Index)
    {
        return mData.get(Index);
    }

    @Override
    public long getItemId(int Index)
    {
        return Index;
    }

    @Override
    public View getView(int Index, View mView, ViewGroup mParent)
    {
        TextView Content;
        myUserDB = new userDB(mContext);
        switch(mData.get(Index).getType())  // 判断是发送还是接收
        {
            case MiniChatMessage.MessageType_Send:
                mView=LayoutInflater.from(mContext).inflate(R.layout.activity_send_message, null);
                avader = (ImageView)mView.findViewById(R.id.Send_Image);
                Content=(TextView)mView.findViewById(R.id.Send_Content);
                Content.setText(mData.get(Index).getContent());
                if (senderBitmap == null) {
                    setImage(MyCookieManager.getUserId(), SENDRR);
                } else {
                    avader.setImageBitmap(senderBitmap); // 头像设置
                }
                break;
            case MiniChatMessage.MessageType_Receive:
                mView=LayoutInflater.from(mContext).inflate(R.layout.activity_receive_message, null);
                avader = (ImageView)mView.findViewById(R.id.Receive_Image);
                Content=(TextView)mView.findViewById(R.id.Receive_Content);
                Content.setText(mData.get(Index).getContent());
                if (receiverBitmap == null) {
                    setImage(MyCookieManager.getUserId(), SENDRR);
                } else {
                    avader.setImageBitmap(receiverBitmap); // 头像设置
                }
                break;
        }
        return mView;
    }

    private static final int SENDRR = 0;
    private static final int RECEIVER = 1;

    private void setImage(final String id, final int type) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                Cursor cursor = myUserDB.findOneByNumber(id);
                String timestamp = null;
                if (cursor.moveToFirst()) {
                    Log.e("读取头像", "本地");
                    updateAvaterFromDB(cursor, type);
                    timestamp = cursor.getString(cursor.getColumnIndex("finalDate"));
                }
                if (checkHasNet(mContext)) {
                    if (timestamp == null || hasUpdate(id, timestamp)) {
                        Log.e("读取头像", "服务器");
                        sendRequestWithHttpConnection(url_queryUserInfo+id, "GET", "", "");
                    }
                } else {
                    Looper.prepare();
                    Toast.makeText(mContext, "没有可用网络", Toast.LENGTH_LONG).show();
                    Looper.loop();
                }
            }
        }).start();
    }

    private void updateAvaterFromDB(Cursor cursor, int tyoe) {
        String path = cursor.getString(cursor.getColumnIndex("avatar"));
        Message msg = new Message();
        msg.what = GET_IMAGE_OK;
        if (tyoe == SENDRR) {
            msg.obj = senderBitmap = ImageUtil.openImage(path);
        } else {
            msg.obj = receiverBitmap = ImageUtil.openImage(path);
        }
        handler.sendMessage(msg);
    }

    private static final String url_queryUserInfo = "http://119.29.238.202:8000/query/";


    private static final int GET_IMAGE_OK = 2;
    public static String dir = "/sdcard/MiniChat";
    // 带有参数的请求
    private void sendRequestWithHttpConnection(final String url, final String method, final String parameter, final String value) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                HttpURLConnection connection = null;
                try {
                    // 获取一个HttpURLConnection实例化对象
                    connection = (HttpURLConnection) ((new URL(url).openConnection()));
                    // 需要登录的操作在连接之前设置好cookie
                    MyCookieManager.setCookie(connection);
                    // 设置请求方式和响应时间
                    connection.setRequestMethod(method);
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
                    Log.e("截断url", url.substring(0,33));
                    if (code.equals("0")) {
                        if (url.substring(0,33).equals(url_queryUserInfo.substring(0, 33))) {  // 获取用户信息
                            JSONObject information = new JSONObject(message);
                            String avatars = information.getString("avatar");
                            Log.e("头像url: ", avatars);
                            myUserDB.insert2Table(information.getString("id"), information.getString("nickname"),
                                    information.getString("sex"), information.getString("city"),
                                    information.getString("signature"), dir+avatars,
                                    information.getString("timestamp"));
                            getImage(avatars);
                        }
                    } else {
                        Looper.prepare();
                        Toast.makeText(mContext, message, Toast.LENGTH_LONG).show();
                        Looper.loop();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                } finally {  // 关闭connection
                    if (connection != null)
                        connection.disconnect();
                }
            }
        }).start();
    }

    // 获取路径下的图片
    private void getImage(final String path) {
        Thread thread = new Thread() {
            @Override
            public void run() {
                Bitmap bm = ImageUtil.getImage(path);
                if (bm != null) {

                    // 保存头像到本地
                    int start = path.lastIndexOf('/');
                    ImageUtil.saveImage(path.substring(start+1), bm);

                    //发生更新UI的消息
                    Message msg = handler.obtainMessage();
                    msg.obj = bm;
                    msg.what = GET_IMAGE_OK;
                    handler.sendMessage(msg);
                }
            }
        };
        thread.start();

    }

    // 利用Handler来更新UI
    private android.os.Handler handler = new android.os.Handler() {
        public void handleMessage(Message message) {
            switch (message.what) {
                case GET_IMAGE_OK:
                    try {
                        avader.setImageBitmap((Bitmap)message.obj); // 头像设置
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    break;
                default: break;
            }
        }
    };
}
