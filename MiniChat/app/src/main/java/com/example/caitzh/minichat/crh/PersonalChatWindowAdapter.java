package com.example.caitzh.minichat.crh;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.example.caitzh.minichat.R;

import java.util.List;

/**
 * Created by Administrator on 2017/6/20 0020.
 */

public class PersonalChatWindowAdapter extends BaseAdapter{
    private Context mContext;
    private List<MiniChatMessage> mData;

    public PersonalChatWindowAdapter(Context context,List<MiniChatMessage> data)
    {
        this.mContext=context;
        this.mData=data;
    }

    public void Refresh()
    {
        this.notifyDataSetChanged();
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
        switch(mData.get(Index).getType())
        {
            case MiniChatMessage.MessageType_Send:
                mView=LayoutInflater.from(mContext).inflate(R.layout.activity_send_message, null);
                Content=(TextView)mView.findViewById(R.id.Send_Content);
                Content.setText(mData.get(Index).getContent());
                break;
            case MiniChatMessage.MessageType_Receive:
                mView=LayoutInflater.from(mContext).inflate(R.layout.activity_receive_message, null);
                Content=(TextView)mView.findViewById(R.id.Receive_Content);
                Content.setText(mData.get(Index).getContent());
                break;
        }
        return mView;
    }
}
