package com.example.caitzh.minichat;

import android.app.Activity;
import android.database.Cursor;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.caitzh.minichat.MyDB.recentListDB;
import com.example.caitzh.minichat.MyDB.userDB;
import com.example.caitzh.minichat.crh.PersonalChatWindow;
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
import java.io.DataOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CountDownLatch;

/**
 * Created by littlestar on 2017/6/21.
 */
public class friendsList extends Activity implements View.OnTouchListener,
        GestureDetector.OnGestureListener {
    private ListView sortListView;
    private SideBar sideBar;
    private TextView dialog, mTvTitle;
    private SortAdapter adapter;
    private EditTextWithDel mEtSearchName;
    private List<SortModel> SourceDateList;
    private ArrayList<String> data = new ArrayList<String>();
    private ArrayList<String> nicknames = new ArrayList<String>();
    private ArrayList<String> ids = new ArrayList<String>();
    private static CountDownLatch mDownLatch;


    private LinearLayout linearLayout;
    private GestureDetector gestureDetector;

    // 底部的按钮切换
    private LinearLayout chatWindowLinearLayout;
    private LinearLayout personalInformationLinearLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_friends_list);
        //addTestData();
        addTestData();
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

    /**
     * 手动添加测试数据
     * 同时测试数据的DATETIME
     */
    private void addTestData() {
        userDB db = new userDB(getBaseContext());
        int id = 0;
        String nickname = "test";
        String date = "";
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
        for (int i = 0; i < 10; ++i, ++id) {
            date = simpleDateFormat.format(new java.util.Date());
            //db.insert2Table(String.valueOf(id), nickname+id, date);
        }
    }

    private void initViews() {
        mEtSearchName = (EditTextWithDel) findViewById(R.id.et_search);
        sideBar = (SideBar) findViewById(R.id.sidrbar);
        dialog = (TextView) findViewById(R.id.dialog);
        mTvTitle = (TextView) findViewById(R.id.tv_title);
        sortListView = (ListView) findViewById(R.id.lv_contact);
        sortListView.setOnTouchListener(this);
        sortListView.setLongClickable(true);
        initDatas();
        initEvents();
        setAdapter();
    }
    // 插入列表数据

    /**
     * 从数据库添加数据
     */
    private void setAdapter() {
        Log.e("Info", "进入setAdapter");
        getFriends();
        Log.e("Info", "取得好友列表成功");

        try {
            Log.e("Info", "开始setAdapter");
            mDownLatch = new CountDownLatch(data.size());
            for (int i = 0; i < data.size(); ++i) {
                getInfo(data.get(i));
            }
            mDownLatch.await(); // 等待多线程结束
            Log.e("Info", "多线程结束");
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            SourceDateList = filledData((String[])nicknames.toArray(new String[nicknames.size()]),
                    (String[])ids.toArray(new String[nicknames.size()]));
            Collections.sort(SourceDateList, new PinyinComparator());
            adapter = new SortAdapter(this, SourceDateList);
            sortListView.setAdapter(adapter);
        }

        SourceDateList = filledData((String[])nicknames.toArray(new String[nicknames.size()]),
                (String[])ids.toArray(new String[nicknames.size()]));
        Collections.sort(SourceDateList, new PinyinComparator());
        adapter = new SortAdapter(this, SourceDateList);
        sortListView.setAdapter(adapter);
        //SourceDateList = filledData(getResources().getStringArray(R.array.contacts));
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
                    bundle.putString("receiveid", Id);
                    Intent intent1 = new Intent(friendsList.this, PersonalChatWindow.class);
                    intent1.putExtras(bundle);
                    recentListDB db = new recentListDB(getBaseContext());
                    db.insertOne(MyCookieManager.getUserId(), Id);
                    finish();
                    startActivity(intent1);
                } else {
                    Toast.makeText(getApplication(), ((SortModel) adapter.getItem(position)).getName(), Toast.LENGTH_SHORT).show();
                }

                // mTvTitle.setText(((SortModel) adapter.getItem(position)).getName());
                 //Toast.makeText(getApplication(), ((SortModel) adapter.getItem(position)).getName(), Toast.LENGTH_SHORT).show();
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

    private List<SortModel> filledData(String[] date, String[] id) {
        List<SortModel> mSortList = new ArrayList<>();
        ArrayList<String> indexString = new ArrayList<>();

        for (int i = 0; i < date.length; i++) {
            SortModel sortModel = new SortModel();
            sortModel.setName(date[i]);
            sortModel.setId(id[i]);
            String pinyin = PinyinUtils.getPingYin(date[i]);
            String sortString = pinyin.substring(0, 1).toUpperCase();
            if (sortString.matches("[A-Z]")) {
                sortModel.setSortLetters(sortString.toUpperCase());
                if (!indexString.contains(sortString)) {
                    indexString.add(sortString);
                }
            } else {
                sortModel.setSortLetters("#");
            }
            mSortList.add(sortModel);
        }
        Collections.sort(indexString);
        // sideBar.setIndexText(indexString);
        return mSortList;
    }
    private static final String getFriendsUrl = "http://119.29.238.202:8000/getFriends";
    private static final String queryInfo = "http://119.29.238.202:8000/query/";
    private ArrayList<String> getFriends() {
        String userId = MyCookieManager.getUserId();
        ArrayList<String> data = new ArrayList<String>();
        if (Check.checkHasNet(getApplicationContext())) {
            try {
                // 发送请求获得好友列表
                mDownLatch = new CountDownLatch(1);
                getFriendListFromServer();
                mDownLatch.await();
                Log.e("线程状态:", "结束");
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            Toast.makeText(getApplicationContext(), "当前没有可用网络", Toast.LENGTH_LONG).show();
        }
        return data;
    }

    private void getFriendListFromServer() {
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
                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    if (connection != null) connection.disconnect();
                    mDownLatch.countDown();
                }
            }
        }).start();
    }
    // 获取好友信息
    private void getInfo(final String id) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                HttpURLConnection connection = null;
                try {
                    connection = (HttpURLConnection) ((new URL(queryInfo+id).openConnection()));
                    // 设置请求方式和响应时间
                    connection.setRequestMethod("GET");
                    connection.setReadTimeout(8000);
                    connection.setConnectTimeout(8000);

                    // 取回的数据
                    InputStream inputStream = connection.getInputStream();
                    BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
                    StringBuilder response = new StringBuilder();
                    String line;
                    while ((line = reader.readLine()) != null) {
                        response.append(line);
                    }
                    // 从返回的json中提取信息
                    JSONObject result = new JSONObject(response.toString());
                    String code = result.getString("code");
                    String message = result.getString("message");
                    if (code.equals("0")) {
                        JSONObject information = new JSONObject(message);
                        String avatar = information.getString("avatar");
                        String city = information.getString("city");
                        String id = information.getString("id");
                        String nickname = information.getString("nickname");
                        String sex = information.getString("sex");
                        String signature = information.getString("signature");
                        nicknames.add(nickname);
                        ids.add(id);
                    } else {
                        // 输出错误提示
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    if (connection != null) connection.disconnect();
                    mDownLatch.countDown();
                }
            }
        }).start();
    }
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
        final int FLING_MIN_DISTANCE=200;
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
