package com.example.caitzh.minichat.crh;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.widget.ListView;

import com.example.caitzh.minichat.R;

import java.util.ArrayList;
import java.util.List;

/**
 * 具体和某个人的聊天界面
 */

public class PersonalChatWindow extends AppCompatActivity {

    private ListView listView;
    private List<MiniChatMessage> mData;
    private PersonalChatWindowAdapter personalChatWindowAdapter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_personal_chat_window);
        initViews();
    }

    private void initViews(){
        listView = (ListView)findViewById(R.id.PersonalChatWindowListView);
        mData = LoadData();
        personalChatWindowAdapter = new PersonalChatWindowAdapter(getBaseContext(), mData);
        listView.setAdapter(personalChatWindowAdapter);
    }

    private List<MiniChatMessage> LoadData() {
        List<MiniChatMessage> Messages=new ArrayList<MiniChatMessage>();
        MiniChatMessage Message=new MiniChatMessage(MiniChatMessage.MessageType_From,"山重水复疑无路");
        Messages.add(Message);

        Message=new MiniChatMessage(MiniChatMessage.MessageType_To,"柳暗花明又一村");
        Messages.add(Message);

        Message=new MiniChatMessage(MiniChatMessage.MessageType_From,"青青子衿，悠悠我心");
        Messages.add(Message);

        Message=new MiniChatMessage(MiniChatMessage.MessageType_To,"但为君故，沉吟至今");
        Messages.add(Message);


        Message=new MiniChatMessage(MiniChatMessage.MessageType_From,"这是你做的Android程序吗？");
        Messages.add(Message);

        Message=new MiniChatMessage(MiniChatMessage.MessageType_To,"是的，这是一个仿微信的聊天界面");
        Messages.add(Message);

        Message=new MiniChatMessage(MiniChatMessage.MessageType_From,"为什么下面的消息发送不了呢");
        Messages.add(Message);

        Message=new MiniChatMessage(MiniChatMessage.MessageType_To,"呵呵，我会告诉你那是直接拿图片做的么");
        Messages.add(Message);

        Message=new MiniChatMessage(MiniChatMessage.MessageType_From,"哦哦，呵呵，你又在偷懒了");
        Messages.add(Message);

        Message=new MiniChatMessage(MiniChatMessage.MessageType_To,"因为这一部分不是今天的重点啊");
        Messages.add(Message);

        Message=new MiniChatMessage(MiniChatMessage.MessageType_From,"好吧，可是怎么发图片啊");
        Messages.add(Message);

        Message=new MiniChatMessage(MiniChatMessage.MessageType_To,"很简单啊，你继续定义一种布局类型，然后再写一个布局就可以了");
        Messages.add(Message);
        return Messages;
    }
}
