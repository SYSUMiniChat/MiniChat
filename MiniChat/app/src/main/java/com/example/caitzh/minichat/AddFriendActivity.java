package com.example.caitzh.minichat;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.GestureDetector;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.SimpleAdapter;

import java.util.List;
import java.util.Map;

/**
 * Created by littlestar on 2017/6/23.
 */
public class AddFriendActivity extends AppCompatActivity {

    private ListView listView;
    private Button button;
    List<Map<String, String>> list;
    SimpleAdapter simpleAdapter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_friend);
        listView = (ListView) findViewById(R.id.addFriendListView);
        button = (Button) findViewById(R.id.add_friend_add_button);

    }
}
