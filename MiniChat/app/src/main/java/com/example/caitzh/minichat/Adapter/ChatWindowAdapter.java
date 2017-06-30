package com.example.caitzh.minichat.Adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.Looper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.caitzh.minichat.Util.DataManager;
import com.example.caitzh.minichat.Util.ImageUtil;
import com.example.caitzh.minichat.R;
import com.example.caitzh.minichat.view.User;
import com.example.caitzh.minichat.view.ChatWindowItemInformation;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

/**
 * 聊天窗口每个item的适配器
 */

public class ChatWindowAdapter extends BaseAdapter{
    private List<ChatWindowItemInformation> list;
    private Context context;
    private ImageView avatar;

    public ChatWindowAdapter(List<ChatWindowItemInformation> inputList, Context inputContext) {
        this.list = inputList;
        this.context = inputContext;
    }

    @Override
    public int getCount() {
        if (this.list == null) {
            return 0;
        }
        return list.size();
    }

    @Override
    public Object getItem(int i) {
        if (this.list == null) {
            return null;
        }
        return this.list.get(i);
    }

    @Override
    public  long getItemId(int i) {
        return i;
    }


    @Override
    public View getView(final int i, View view, ViewGroup viewGroup) {
        if (view == null) {
            view = LayoutInflater.from(context).inflate(R.layout.activity_chat_window_listview, null);
        }
        avatar = (ImageView)view.findViewById(R.id.chat_window_listview_icon);
        Thread setAvatar = new Thread(new Runnable() {
            @Override
            public void run() {
                User user = DataManager.getLatestData(context, list.get(i).getUserID());
                if (user == null) {
                    Looper.prepare();
                    Toast.makeText(context, "没有可用网络", Toast.LENGTH_LONG).show();
                    Looper.loop();
                } else {
                    Bitmap bm = ImageUtil.openImage(user.getAvatar());
                    avatar.setImageBitmap(bm);
                }
            }
        });
        setAvatar.start();
        try {
            setAvatar.join();
        } catch (Exception e) {
            e.printStackTrace();
        }
        TextView information = (TextView)view.findViewById(R.id.chat_window_listview_information);
        TextView time = (TextView)view.findViewById(R.id.chat_window_listview_time);
        TextView username = (TextView)view.findViewById(R.id.chat_window_listview_username);
        TextView userid = (TextView)view.findViewById(R.id.chat_window_listview_userid);
        information.setText(list.get(i).getInformation());
        time.setText(timeFormat(list.get(i).getTime()));
        username.setText(list.get(i).getUsername());
        userid.setText(list.get(i).getUserID());
        TextView radius_text = (TextView)view.findViewById(R.id.radius_text);
        if (!list.get(i).getReaded()) {
            radius_text.setVisibility(View.VISIBLE);
        }
        return view;
    }

    private String timeFormat(String time) {
        if (time.equals("")) {
            return "";
        }
        String returnTime = "";
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date curDate =  new Date(System.currentTimeMillis());
        String curTime = formatter.format(curDate);
        Log.e("小时", time.substring(11, 13));
        int hour = Integer.valueOf(time.substring(11, 13));
        if (time.substring(0, 10).equals(curTime.substring(0, 10))) {
            // 日期相同
            if (hour >= 0 && hour < 6) {
                returnTime = "凌晨" + time.substring(11, 16);
            } else if (hour >= 6 && hour < 12) {
                returnTime = "早上" + time.substring(11, 16);
            } else  if (hour >= 12 && hour < 14) {
                returnTime = "中午" + Integer.toString(hour-12) + time.substring(13, 16);
            } else if (hour >= 14 && hour < 18) {
                returnTime = "下午" + Integer.toString(hour-12) + time.substring(13, 16);
            } else {
                returnTime = "晚上" + Integer.toString(hour-12) + time.substring(13, 16);
            }
        } else {
            returnTime = time.substring(5, 7) + "月" +
                    time.substring(8, 10) + "日";
        }
        return returnTime;
    }
}
