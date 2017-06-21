package com.example.caitzh.minichat.crh;

/**
 * 消息类：封装聊天消息
 * 包括消息类型（时间、发送消息或者接受消息），以及消息内容
 */

public class MiniChatMessage {
    // 时间类型用0，发送的消息为1，接受的消息为2
    public static final int MessageType_Time=0;
    public static final int MessageType_From=1;
    public static final int MessageType_To=2;

    public MiniChatMessage(int Type,String Content)
    {
        this.mType=Type;
        this.mContent=Content;
    }

    //消息类型
    private int mType;
    //消息内容
    private String mContent;
    //获取类型
    public int getType() {
        return mType;
    }
    //设置类型
    public void setType(int mType) {
        this.mType = mType;
    }
    //获取内容
    public String getContent() {
        return mContent;
    }
    //设置内容
    public void setContent(String mContent) {
        this.mContent = mContent;
    }
}
