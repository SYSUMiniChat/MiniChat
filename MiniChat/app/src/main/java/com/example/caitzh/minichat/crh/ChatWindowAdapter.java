package com.example.caitzh.minichat.crh;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import android.widget.ToggleButton;

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
        // TODO: 更改ChatWindowItemInformation类并把相应信息给予对应内容
        return view;
    }
}
