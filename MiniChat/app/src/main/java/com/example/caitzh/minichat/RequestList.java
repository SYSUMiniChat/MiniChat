package com.example.caitzh.minichat;


import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.example.caitzh.minichat.MyDB.addRequestDB;
import com.example.caitzh.minichat.R;
import com.example.caitzh.minichat.middlewares.Check;
import com.example.caitzh.minichat.view.SortModel;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by littlestar on 2017/6/30.
 */
public class RequestList extends AppCompatActivity {
    private RequestListAdapter adapter;
    private ListView listView = null;
    private List<SortModel> data = new ArrayList<>();
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_request_list);
        listView = (ListView) findViewById(R.id.FriendListRequest);
        adapter = new RequestListAdapter(getApplicationContext(), data);
        init();
        listView.setAdapter(adapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (Check.checkHasNet(getApplicationContext())) {
                    String id_ = ((SortModel) adapter.getItem(position)).getId();
                    String status = ((SortModel) adapter.getItem(position)).getSortLetters();
                    Bundle bundle = new Bundle();
                    bundle.putString("id", id_);
                    // 添加请求type为1
                    if (status.equals("去看看")) bundle.putInt("type", 1);
                    else bundle.putInt("type", 0);
                    Intent intent1 = new Intent(RequestList.this, AddFriendActivity.class);
                    intent1.putExtras(bundle);
                    finish();
                    startActivity(intent1);
                } else {
                    Toast.makeText(getApplicationContext(), "网络不可用", Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    private void init() {
        addRequestDB db = new addRequestDB(getApplicationContext());
        Cursor cursor =  db.getItems(MyCookieManager.getUserId());
        //  将读取到的数据写入listview
        if (cursor != null) {
            int count = cursor.getCount();
            Log.e("添加请求", String.valueOf(count));
            for (int i = 0; i < count; ++i) {
                getAndSetInfo(getApplicationContext(), cursor.getString(cursor.getColumnIndex("sender")),
                        cursor.getInt(cursor.getColumnIndex("status")));
                cursor.moveToNext();
            }
        }
    }

    private void getAndSetInfo(final Context context, final String id_, final int status) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                User user = DataManager.getLatestData(context, id_);
                if (user != null) {
                    SortModel temp = new SortModel();
                    temp.setName(user.getNickname());
                    temp.setId(id_);
                    if (status == 0) {
                        temp.setSortLetters("去看看");
                    } else {
                        temp.setSortLetters("已添加");
                    }
                    temp.setBm(ImageUtil.openImage(user.getAvatar()));
                    // 数据同步写入
                    Message message = new Message();
                    message.what = INIT_CODE;
                    message.obj = temp;
                    handler.sendMessage(message);
                } else {
                    Looper.prepare();
                    Toast.makeText(getApplicationContext(), "网络不可用", Toast.LENGTH_LONG).show();
                    Looper.loop();
                }
            }
        }).start();
    }
    private final int INIT_CODE = 0;
    private final int DELETE_CODE = 1;
    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case INIT_CODE: {
                    synchronized (this) {
                        //  插入数据
                        data.add((SortModel)msg.obj);
                        adapter.notifyDataSetChanged();
                    }
                    break;
                }
                case DELETE_CODE: {
                    // shanchu
                    break;
                }
                default:break;
            }
        }
    };
}
