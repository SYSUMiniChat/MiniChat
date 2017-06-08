package com.example.caitzh.minichat;

import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.SimpleAdapter;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class personalInformation extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_personal_information);

        String[] names = new String[] {"昵称","Mini号","性别","地区","Mini签名"};
        String[] details = new String[] {"海tiu~","cht1012536506","男","广东广州","最喜欢你啦"};

        // 如果是从修改页面跳转过来的，则更新listView的内容并显示
        Bundle bundle = this.getIntent().getExtras();
        if (bundle != null) {
            if (bundle.getString("name") != null) {
                details[0] = bundle.getString("name");
            }
            if (bundle.getString("signature") != null) {
                details[4] = bundle.getString("signature");
            }
        }

        final List<Map<String, String>> list = new ArrayList<>();
        for (int i = 0; i < 5; ++i) {
            Map<String, String> listItem = new HashMap<>();
            listItem.put("name", names[i]);
            listItem.put("detail", details[i]);
            list.add(listItem);
        }

        // SimpleAdapter
        final ListView listView = (ListView) findViewById(R.id.listView);
        final SimpleAdapter simpleAdapter = new SimpleAdapter(this, list, R.layout.personal_information_item,
                new String[] {"name", "detail"}, new int[] {R.id.name, R.id.detail});
        listView.setAdapter(simpleAdapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, final int position, long id) {
                Log.i("position:", position+"");
                if (position == 0) { // 点击"昵称"，跳转到更改名字页面
                    Intent intent = new Intent(personalInformation.this, changeName.class);
                    // 获取当前页面的昵称，并通过Bundle传递参数
                    String cur_name = list.get(position).get("detail");
                    Bundle bundle = new Bundle();
                    bundle.putString("name", cur_name);
                    intent.putExtras(bundle);
                    startActivity(intent);
                } else if (position == 1) { // 点击"mini号"，提示一旦注册后不可修改
                    Toast.makeText(personalInformation.this, "Mini号不可修改喔~", Toast.LENGTH_LONG).show();
                } else if (position == 2) { // 点击"性别"，弹出可供选择的对话框
                    // 自定义对话框
                    LayoutInflater inflater = LayoutInflater.from(personalInformation.this);
                    final View newView = inflater.inflate(R.layout.choose_sex_layout, null);
                    AlertDialog.Builder builder = new AlertDialog.Builder(personalInformation.this);
                    builder.setView(newView);
                    // 根据listView内容初始化单选按钮
                    final RadioButton radioButton_man = (RadioButton) newView.findViewById(R.id.man);
                    final RadioButton radioButton_woman = (RadioButton) newView.findViewById(R.id.woman);
                    String curSex = list.get(position).get("detail");
                    if (curSex.equals("男")) {
                        radioButton_man.setChecked(true);
                    } else {
                        radioButton_woman.setChecked(true);
                    }
                    // 设置点击按钮对应的后续操作
                    builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            // 获取所选性别
                            String newSex = "男";
                            if (!radioButton_man.isChecked()) newSex = "女";
                            // 更新listView内容显示
                            list.get(position).put("detail", newSex);
                            simpleAdapter.notifyDataSetChanged();
                            Toast.makeText(personalInformation.this, "成功修改性别为" + newSex, Toast.LENGTH_SHORT).show();
                        }
                    }).setNegativeButton("取消", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            Toast.makeText(personalInformation.this, "取消更改性别", Toast.LENGTH_SHORT).show();
                        }
                    }).create().show();
                } else if (position == 3) { // 修改地区
                    Toast.makeText(personalInformation.this, "定位功能暂未开启", Toast.LENGTH_LONG).show();
                } else if (position == 4) { // 修改Mini签名
                    Intent intent = new Intent(personalInformation.this, changeSignature.class);
                    String cur_signature = list.get(position).get("detail");
                    Bundle bundle = new Bundle();
                    bundle.putString("signature", cur_signature);
                    intent.putExtras(bundle);
                    startActivity(intent);
                }
            }
        });
    }
}


















