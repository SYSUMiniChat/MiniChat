package com.example.caitzh.minichat.Chat;

/**
 * 消息类：封装聊天消息
 * 包括消息类型（时间、发送消息或者接受消息），以及消息内容
 */

public class MiniChatMessage {
    // 发送的消息为0，接受的消息为1
    public static final int MessageType_Send=0;
    public static final int MessageType_Receive=1;

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
