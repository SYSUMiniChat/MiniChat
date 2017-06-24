package com.example.caitzh.minichat.crh;

import android.content.Context;
import android.graphics.Bitmap;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.ToggleButton;

import com.example.caitzh.minichat.ImageUtil;
import com.example.caitzh.minichat.R;

import java.util.List;

/**
 * 聊天窗口每个item的适配器
 */

public class ChatWindowAdapter extends BaseAdapter{
    private List<ChatWindowItemInformation> list;
    private Context context;

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
        // TODO: 更改头像
        ImageView icon = (ImageView)view.findViewById(R.id.chat_window_listview_icon);
        TextView information = (TextView)view.findViewById(R.id.chat_window_listview_information);
        TextView time = (TextView)view.findViewById(R.id.chat_window_listview_time);
        TextView username = (TextView)view.findViewById(R.id.chat_window_listview_username);
        TextView userid = (TextView)view.findViewById(R.id.chat_window_listview_userid);
        information.setText(list.get(i).getInformation());
        time.setText(list.get(i).getTime());
        username.setText(list.get(i).getUsername());
        userid.setText(list.get(i).getUserID());
        return view;
    }
}
