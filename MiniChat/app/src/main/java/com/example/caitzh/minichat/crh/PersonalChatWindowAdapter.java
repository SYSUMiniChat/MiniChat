package com.example.caitzh.minichat.crh;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.Looper;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.caitzh.minichat.DataManager;
import com.example.caitzh.minichat.ImageUtil;
import com.example.caitzh.minichat.MyCookieManager;
import com.example.caitzh.minichat.R;
import com.example.caitzh.minichat.User;
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

    @Override
    public View getView(int Index, View mView, ViewGroup mParent)
    {
        TextView Content;
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
                    setImage(receiveid, RECEIVER);
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
                User user = DataManager.getLatestData(mContext, id);
                if (user == null) {
                    Looper.prepare();
                    Toast.makeText(mContext, "没有可用网络", Toast.LENGTH_LONG).show();
                    Looper.loop();
                } else {
                    // 动态更新
                    Message msg = new Message();
                    msg.what = GET_IMAGE_OK;
                    if (type == SENDRR) {
                        msg.obj = senderBitmap = ImageUtil.openImage(user.getAvatar());
                    } else {
                        msg.obj = receiverBitmap = ImageUtil.openImage(user.getAvatar());
                    }
                    handler.sendMessage(msg);
                }
            }
        }).start();
    }
    private static final int GET_IMAGE_OK = 2;


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
