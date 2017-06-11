package com.example.caitzh.minichat.crh;

import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

import com.example.caitzh.minichat.R;

/**
 * Created by Administrator on 2017/6/10 0010.
 */

public class chatWindow extends AppCompatActivity{
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_window);

    }

    private void setChatWindowAdapter() {
        // TODO:从数据库读入数据并绑定适配器
    }
}
