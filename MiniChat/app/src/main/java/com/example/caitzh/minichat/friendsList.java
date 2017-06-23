package com.example.caitzh.minichat;

import android.app.Activity;
import android.database.Cursor;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.caitzh.minichat.MyDB.userDB;
import com.example.caitzh.minichat.middlewares.Check;
import com.example.caitzh.minichat.view.EditTextWithDel;
import com.example.caitzh.minichat.view.PinyinComparator;
import com.example.caitzh.minichat.view.PinyinUtils;
import com.example.caitzh.minichat.view.SideBar;
import com.example.caitzh.minichat.view.SortModel;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created by littlestar on 2017/6/21.
 */
public class friendsList extends Activity {
    private ListView sortListView;
    private SideBar sideBar;
    private TextView dialog, mTvTitle;
    private SortAdapter adapter;
    private EditTextWithDel mEtSearchName;
    private List<SortModel> SourceDateList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_friends_list);
        addTestData();
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
            db.insert2Table(String.valueOf(id), nickname+id, date);
        }
    }

    private void initViews() {
        mEtSearchName = (EditTextWithDel) findViewById(R.id.et_search);
        sideBar = (SideBar) findViewById(R.id.sidrbar);
        dialog = (TextView) findViewById(R.id.dialog);
        mTvTitle = (TextView) findViewById(R.id.tv_title);
        sortListView = (ListView) findViewById(R.id.lv_contact);
        initDatas();
        initEvents();
        setAdapter();
    }
    // 插入列表数据

    /**
     * 从数据库添加数据
     */
    private void setAdapter() {
        userDB db = new userDB(getBaseContext());
        int id = 0;
        ArrayList<String> data = new ArrayList<String>();
        for (int i = 0; i < 10; ++i, ++id) {
            Cursor cursor = db.findOneByNumber(String.valueOf(id));
            if (cursor.moveToFirst()) {
                data.add(cursor.getString(cursor.getColumnIndex("nickname")));
            } else {
                Log.e("Read result is null", ""+i);
            }
        }
        int size = data.size();
        //SourceDateList = filledData(getResources().getStringArray(R.array.contacts));
        SourceDateList = filledData((String[])data.toArray(new String[size]));
        Collections.sort(SourceDateList, new PinyinComparator());
        adapter = new SortAdapter(this, SourceDateList);
        sortListView.setAdapter(adapter);
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
                mTvTitle.setText(((SortModel) adapter.getItem(position - 1)).getName());
                Toast.makeText(getApplication(), ((SortModel) adapter.getItem(position)).getName(), Toast.LENGTH_SHORT).show();
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

    private List<SortModel> filledData(String[] date) {
        List<SortModel> mSortList = new ArrayList<>();
        ArrayList<String> indexString = new ArrayList<>();

        for (int i = 0; i < date.length; i++) {
            SortModel sortModel = new SortModel();
            sortModel.setName(date[i]);
            String pinyin = PinyinUtils.getPingYin(date[i]);
            String sortString = pinyin.substring(0, 1).toUpperCase();
            if (sortString.matches("[A-Z]")) {
                sortModel.setSortLetters(sortString.toUpperCase());
                if (!indexString.contains(sortString)) {
                    indexString.add(sortString);
                }
            }
            mSortList.add(sortModel);
        }
        Collections.sort(indexString);
        sideBar.setIndexText(indexString);
        return mSortList;
    }
    private static final String getFriendsUrl = "http://119.29.238.202:8000/getFriends";
    private ArrayList<String> getFriends() {
        String userId = MyCookieManager.getUserId();
        ArrayList<String> data = new ArrayList<String>();
        if (Check.checkHasNet(getApplicationContext())) {
            // 发送请求获得好友列表
        } else {
            Toast.makeText(getApplicationContext(), "当前没有可用网络", Toast.LENGTH_LONG).show();
        }
        return data;
    }
}
