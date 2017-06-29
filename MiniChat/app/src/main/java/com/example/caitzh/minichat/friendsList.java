package com.example.caitzh.minichat;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.database.Cursor;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.GestureDetector;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.caitzh.minichat.MyDB.recentListDB;
import com.example.caitzh.minichat.middlewares.Check;
import com.example.caitzh.minichat.crh.chatWindow;
import com.example.caitzh.minichat.view.EditTextWithDel;
import com.example.caitzh.minichat.view.PinyinComparator;
import com.example.caitzh.minichat.view.PinyinUtils;
import com.example.caitzh.minichat.view.SideBar;
import com.example.caitzh.minichat.view.SortModel;
import org.json.JSONArray;
import org.json.JSONObject;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CountDownLatch;

/**
 * Created by littlestar on 2017/6/21.
 */
public class friendsList extends AppCompatActivity implements View.OnTouchListener,
        GestureDetector.OnGestureListener {
    private ListView sortListView;
    private SideBar sideBar;
    private TextView dialog;
    private SortAdapter adapter;
    private EditTextWithDel mEtSearchName;
    private List<SortModel> SourceDateList = new ArrayList<SortModel>();
    private ArrayList<String> data = new ArrayList<String>();


    private LinearLayout linearLayout;
    private GestureDetector gestureDetector;

    // 底部的按钮切换
    private LinearLayout chatWindowLinearLayout;
    private LinearLayout personalInformationLinearLayout;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_friends_list);
        adapter = new SortAdapter(getApplicationContext(), SourceDateList);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        chatWindowLinearLayout = (LinearLayout)findViewById(R.id.id_tab_chat);
        personalInformationLinearLayout = (LinearLayout)findViewById(R.id.id_tab_personal_information);
        chatWindowLinearLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(friendsList.this,chatWindow.class);
                startActivity(intent);
                overridePendingTransition(R.anim.finish_immediately, R.anim.finish_immediately);
            }
        });
        personalInformationLinearLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(friendsList.this, personalInformation.class);
                startActivity(intent);
                overridePendingTransition(R.anim.finish_immediately, R.anim.finish_immediately);
            }
        });

        linearLayout = (LinearLayout)findViewById(R.id.friends_list_linear_layout);
        linearLayout.setOnTouchListener(this);
        linearLayout.setLongClickable(true);
        gestureDetector = new GestureDetector((GestureDetector.OnGestureListener)this);
        initViews();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Intent intent = new Intent(friendsList.this, SearchUser.class);
        startActivity(intent);
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.e("onResume", "begin");
        SourceDateList.clear();
        data.clear();
        setAdapter();
    }

    private void initViews() {
        mEtSearchName = (EditTextWithDel) findViewById(R.id.et_search);
        sideBar = (SideBar) findViewById(R.id.sidrbar);
        dialog = (TextView) findViewById(R.id.dialog);
        sortListView = (ListView) findViewById(R.id.lv_contact);
        sortListView.setOnTouchListener(this);
        sortListView.setLongClickable(true);
        initDatas();
        initEvents();
    }

    /**
     * 从数据库添加数据
     */
    private void setAdapter() {
        Log.e("Info", "进入setAdapter");
        sortListView.setAdapter(adapter);
        if (Check.checkHasNet(getApplicationContext())) {
            getFriendListFromServer(getApplicationContext());
        } else {
            Toast.makeText(getApplication(), "当前网络不可用", Toast.LENGTH_SHORT).show();
        }
    }

    private void initEvents() {
        //设置右侧触摸监听
        sideBar.setOnTouchingLetterChangedListener(new SideBar.OnTouchingLetterChangedListener() {
            @Override
            public void onTouchingLetterChanged(String s) {
                //该字母首次出现的位置
                int position = adapter.getPositionForSection(s.charAt(0));
                if (position != -1) {
                    sortListView.setSelection(position + 1);
                }
            }
        });

        //ListView的点击事件
        sortListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {

                if (Check.checkHasNet(getApplicationContext())) {
                    String Id = ((SortModel) adapter.getItem(position)).getId();
                    Bundle bundle = new Bundle();
                    bundle.putString("id", Id);
                    bundle.putInt("type", 0);
                    Intent intent1 = new Intent(friendsList.this, AddFriendActivity.class);
                    intent1.putExtras(bundle);
                    finish();
                    startActivity(intent1);
                } else {
                    Toast.makeText(getApplication(), ((SortModel) adapter.getItem(position)).getName(), Toast.LENGTH_SHORT).show();
                }
            }
        });
        // 设置长按删除好友
        sortListView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, final int position, long id) {
                AlertDialog.Builder builder = new AlertDialog.Builder(friendsList.this);
                builder.setTitle("删除好友");
                builder.setMessage("确定删除好友?");
                builder.setNegativeButton("取消", null);
                builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        deleteFriend(position);
                    }
                });
                AlertDialog alertDialog = builder.create();
                alertDialog.show();
                return true;
            }
        });

        //根据输入框输入值的改变来过滤搜索
        mEtSearchName.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                //当输入框里面的值为空，更新为原来的列表，否则为过滤数据列表
                filterData(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }

    private void initDatas() {
        sideBar.setTextView(dialog);
    }

    /**
     * 根据输入框中的值来过滤数据并更新ListView
     *
     * @param filterStr
     */
    private void filterData(String filterStr) {
        List<SortModel> mSortList = new ArrayList<>();
        if (TextUtils.isEmpty(filterStr)) {
            mSortList = SourceDateList;
        } else {
            mSortList.clear();
            for (SortModel sortModel : SourceDateList) {
                String name = sortModel.getName();
                if (name.toUpperCase().indexOf(filterStr.toString().toUpperCase()) != -1 || PinyinUtils.getPingYin(name).toUpperCase().startsWith(filterStr.toString().toUpperCase())) {
                    mSortList.add(sortModel);
                }
            }
        }
        // 根据a-z进行排序
        Collections.sort(mSortList, new PinyinComparator());
        adapter.updateListView(mSortList);
    }
    private static final String getFriendsUrl = "http://119.29.238.202:8000/getFriends";

     // 获取好友列表
    private void getFriendListFromServer(final Context context) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                HttpURLConnection connection = null;
                try {
                    connection = (HttpURLConnection) ((new URL(getFriendsUrl).openConnection()));
                    // 设置cookie
                    MyCookieManager.setCookie(connection);
                    // 设置请求方式和响应时间
                    connection.setRequestMethod("GET");
                    connection.setReadTimeout(8000);
                    connection.setConnectTimeout(8000);

                    InputStream inputStream = connection.getInputStream();
                    BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
                    StringBuilder response = new StringBuilder();
                    String line;
                    while ((line = reader.readLine()) != null) {
                        response.append(line);
                    }
                    // 从返回的json中提取信息
                    JSONObject result = new JSONObject(response.toString());
                    int code = result.getInt("code");
                    Log.e("List Code:", String.valueOf(code));
                    JSONArray resultList = result.getJSONArray("message");
                    ArrayList<String> Flist = new ArrayList<String>();
                    for (int i = 0; i < resultList.length(); ++i) {
                        Flist.add(resultList.getString(i));
                        Log.e("list:", resultList.getString(i));
                    }
                    data = Flist;
                    for (int i = 0; i < data.size(); ++i) {
                        getAndSetUserInfo(context, data.get(i));
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    if (connection != null) connection.disconnect();
                }
            }
        }).start();
    }
    private void getAndSetUserInfo(final Context context,final String id) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                User user = DataManager.getLatestData(context, id);
                if (user != null) {
                    SortModel sortModel = new SortModel();
                    sortModel.setName(user.getNickname());
                    sortModel.setId(id);
                    sortModel.setBm(ImageUtil.openImage(user.getAvatar()));
                    String pinyin = PinyinUtils.getPingYin(user.getNickname());
                    String sortString = pinyin.substring(0, 1).toUpperCase();
                    if (sortString.matches("[A-Z]")) {
                        sortModel.setSortLetters(sortString.toUpperCase());
                    } else {
                        sortModel.setSortLetters("#");
                    }
                    // 数据同步
                    {
                        Message message = new Message();
                        message.what = INIT_CODE;
                        message.obj = sortModel;
                        handler.sendMessage(message);
                    }
                } else {
                    Log.e("好友列表", "用户为空");
                }
            }
        }).start();
    }
    private static final int INIT_CODE = 0;
    private static final int DELETE_CODE =1;
    private void deleteFriend(final int position) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                if (AccessServerUtil.deleteFriendFromServer(SourceDateList.get(position).getId())) {
                    Message message = new Message();
                    message.what = DELETE_CODE;
                    message.arg1 = position;
                    handler.sendMessage(message);
                } else {
                    Looper.prepare();
                    Toast.makeText(getApplicationContext(), "删除失败", Toast.LENGTH_LONG).show();
                    Looper.loop();
                }
            }
        }).start();
    }

    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case INIT_CODE: {
                    synchronized (this) {
                        SourceDateList.add((SortModel) msg.obj);
                        Collections.sort(SourceDateList, new PinyinComparator());
                        adapter.updateListView(SourceDateList);
                    }
                    break;
                }
                case DELETE_CODE: {
                    int position = msg.arg1;
                    SourceDateList.remove(position);
                    adapter.updateListView(SourceDateList);
                    break;
                }
                default: break;
            }
        }
    };
    @Override
    public boolean onDown(MotionEvent e) {
        return false;
    }

    @Override
    public void onShowPress(MotionEvent e) {

    }

    @Override
    public boolean onSingleTapUp(MotionEvent e) {
        return false;
    }

    @Override
    public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
        return false;
    }

    @Override
    public void onLongPress(MotionEvent e) {

    }

    @Override
    public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
        final int FLING_MIN_DISTANCE=180;
        final int FLING_MIN_VELOCITY=200;

        Log.e("水平距离2", Float.toString((e1.getX() - e2.getX())));
        Log.e("水平速度2", Float.toString(Math.abs(velocityX)));

        //左
        if(e1.getX() - e2.getX() > FLING_MIN_DISTANCE && Math.abs(velocityX) > FLING_MIN_VELOCITY){
            Intent intent = new Intent(friendsList.this,personalInformation.class);
            startActivity(intent);
            finish();
            overridePendingTransition(R.anim.slide_right_in, R.anim.slide_left_out);
        }

        //右
        if(e1.getX() - e2.getX() < -FLING_MIN_DISTANCE && Math.abs(velocityX) > FLING_MIN_VELOCITY){
            Intent intent = new Intent(friendsList.this,chatWindow.class);
            startActivity(intent);
            finish();
            overridePendingTransition(R.anim.slide_left_in, R.anim.slide_right_out);
        }

        return false;
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        return gestureDetector.onTouchEvent(event);
    }
}
