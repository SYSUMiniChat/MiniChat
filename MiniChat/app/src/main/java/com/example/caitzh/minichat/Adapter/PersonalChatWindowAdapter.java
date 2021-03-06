package com.example.caitzh.minichat.Adapter;

import android.content.Context;
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

import com.example.caitzh.minichat.Util.DataManager;
import com.example.caitzh.minichat.Util.ImageUtil;
import com.example.caitzh.minichat.Util.MyCookieManager;
import com.example.caitzh.minichat.R;
import com.example.caitzh.minichat.view.User;
import com.example.caitzh.minichat.Chat.MiniChatMessage;

import java.util.List;

/**
 * Created by Administrator on 2017/6/20 0020.
 */

public class PersonalChatWindowAdapter extends BaseAdapter{
    private Context mContext;
    private List<MiniChatMessage> mData;
    private String receiveid;
    private ImageView avader;
    private Bitmap receiverBitmap = null;
    private Bitmap senderBitmap = null;

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

    private static View tmView = null;
    @Override
    public View getView(int Index, View mView, ViewGroup mParent)
    {
        TextView Content;
        switch(mData.get(Index).getType())  // 判断是发送还是接收
        {
            case MiniChatMessage.MessageType_Send:
                mView=LayoutInflater.from(mContext).inflate(R.layout.activity_send_message, null);
                tmView = mView;
                Content=(TextView)mView.findViewById(R.id.Send_Content);
                avader = (ImageView)mView.findViewById(R.id.Send_Image);
                Content.setText(mData.get(Index).getContent());
                if (senderBitmap == null) {
                    Thread setAvatar = new Thread(new Runnable() {
                        @Override
                        public void run() {
                            setImage(MyCookieManager.getUserId(), SENDER);
                        }
                    });
                    setAvatar.start();
                    try {
                        setAvatar.join();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                } else {
                    avader.setImageBitmap(senderBitmap); // 头像设置
                }
                break;
            case MiniChatMessage.MessageType_Receive:
                mView=LayoutInflater.from(mContext).inflate(R.layout.activity_receive_message, null);
                Content=(TextView)mView.findViewById(R.id.Receive_Content);
                avader = (ImageView)mView.findViewById(R.id.Receive_Image);
                Content.setText(mData.get(Index).getContent());
                if (receiverBitmap == null) {
                    Thread setAvatar = new Thread(new Runnable() {
                        @Override
                        public void run() {
                            setImage(receiveid, RECEIVER);
                        }
                    });
                    setAvatar.start();
                    try {
                        setAvatar.join();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                } else {
                    avader.setImageBitmap(receiverBitmap); // 头像设置
                }
                break;
        }
        return mView;
    }

    private static final int SENDER = 0;
    private static final int RECEIVER = 1;

    private void setImage(final String id, final int type) {
        Thread set = new Thread(new Runnable() {
            @Override
            public void run() {
                User user = DataManager.getLatestData(mContext, id);
                if (user == null) {
                    Looper.prepare();
                    Toast.makeText(mContext, "没有可用网络", Toast.LENGTH_LONG).show();
                    Looper.loop();
                } else {
                    // 动态更新
                    Message msg = new Message();
                    msg.what = GET_IMAGE_OK;
                    if (type == SENDER) {
                        msg.arg1 = SENDER;
                        msg.obj = senderBitmap = ImageUtil.openImage(user.getAvatar());
                    } else {
                        msg.arg1 = RECEIVER;
                        msg.obj = receiverBitmap = ImageUtil.openImage(user.getAvatar());
                    }
                    handler.sendMessage(msg);
                }
            }
        });
        set.start();
        try {
            set.join();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    private static final int GET_IMAGE_OK = 2;


    // 利用Handler来更新UI
    private android.os.Handler handler = new android.os.Handler() {
        public void handleMessage(Message message) {
            switch (message.what) {
                case GET_IMAGE_OK:
                    try {
                        if (message.arg1 == RECEIVER) {
                        } else {
                            avader = (ImageView)tmView.findViewById(R.id.Send_Image);
                        }
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
