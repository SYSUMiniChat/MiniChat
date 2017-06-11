package com.example.caitzh.minichat;

import android.content.ContentResolver;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;

public class personalInformation extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_personal_information);

        // TODO：引入数据库后修改默认数据
        String[] names = new String[] {"昵称","Mini号","性别","地区","Mini签名", "修改密码"};
        String[] details = new String[] {"海tiu~","cht1012536506","男","广东广州","最喜欢你啦", ""};

        // 如果是从修改页面跳转过来的，则更新listView的内容并显示
        Bundle bundle = this.getIntent().getExtras();
        if (bundle != null) {
            if (bundle.getString("name") != null) {
                details[0] = bundle.getString("name");
            }
            if (bundle.getString("signature") != null) {
                details[4] = bundle.getString("signature");
            }
            if (bundle.getString("address") != null) {
                details[3] = bundle.getString("address");
            }
        }

        // 点击头像这一栏 选择本地相册图片 暂未实现拍摄功能
        TextView test_avatar = (TextView) findViewById(R.id.test_avatar);
        test_avatar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(intent, 1);
            }
        });

        // 点击头像放大
        final ImageView avatar = (ImageView) findViewById(R.id.avatar);
        avatar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 方法一：自定义对话框，显示头像大图
                LayoutInflater inflater = LayoutInflater.from(personalInformation.this);
                View toshow_view = inflater.inflate(R.layout.show_avatar, null);
                AlertDialog.Builder builder = new AlertDialog.Builder(personalInformation.this);
                Bitmap bitmap =((BitmapDrawable) avatar.getDrawable()).getBitmap();
                ImageView toshow_avatar = (ImageView) toshow_view.findViewById(R.id.show_avatar);
                toshow_avatar.setImageBitmap(bitmap);
                builder.setView(toshow_view).create().show();

                // 方法二：跳转到新的页面
//                Intent intent = new Intent(personalInformation.this, show_avatar.class);
//                Bundle bundle1 = new Bundle();
//                bundle1.putParcelable("bitmap", bitmap);
//                intent.putExtras(bundle1)
//                intent.putExtra("bitmap", bitmap);

//                ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
//                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, outputStream);
//                byte[] bitmapByte = outputStream.toByteArray();
//                intent.putExtra("bitmap", bitmapByte);
//                startActivity(intent);

            }
        });


        final List<Map<String, String>> list = new ArrayList<>();
        for (int i = 0; i < 6; ++i) {
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
                    Intent intent = new Intent(personalInformation.this, changeAddress.class);
                    String cur_address = list.get(position).get("detail");
                    Bundle bundle = new Bundle();
                    bundle.putString("address", cur_address);
                    intent.putExtras(bundle);
                    startActivity(intent);
                } else if (position == 4) { // 修改Mini签名
                    Intent intent = new Intent(personalInformation.this, changeSignature.class);
                    String cur_signature = list.get(position).get("detail");
                    Bundle bundle = new Bundle();
                    bundle.putString("signature", cur_signature);
                    intent.putExtras(bundle);
                    startActivity(intent);
                } else if (position == 5) {  // 修改密码
                    // 先弹出对话框，输入原密码
                    LayoutInflater inflater = LayoutInflater.from(personalInformation.this);
                    final View newView = inflater.inflate(R.layout.comfirm_password, null);
                    AlertDialog.Builder builder = new AlertDialog.Builder(personalInformation.this);
                    builder.setView(newView)
                            .setPositiveButton("确认", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    // 原密码输入正确，可跳转到修改密码页面,否则提示密码错误
                                    EditText editText = (EditText) newView.findViewById(R.id.originPassword);
                                    String input = editText.getText().toString();
                                    if (input.equals("123456")) {  // TODO: 这里先用123456测试，引入数据库后再修改
                                        Intent intent = new Intent(personalInformation.this, changePassword.class);
                                        intent.putExtra("miniNumber", list.get(1).get("detail"));  // 传递参数: mini号
                                        startActivity(intent);
                                    } else {
                                        Toast.makeText(personalInformation.this, "密码错误，请重新输入", Toast.LENGTH_LONG).show();
                                    }
                                }
                            })
                            .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    Toast.makeText(personalInformation.this, "取消修改密码", Toast.LENGTH_LONG).show();
                                }
                            })
                            .create().show();
                }
            }
        });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            Uri uri = data.getData();
            Log.i("uri", uri.toString());
            ContentResolver cr = this.getContentResolver();
            try {
                Bitmap bitmap = BitmapFactory.decodeStream(cr.openInputStream(uri));
                ImageView imageView = (ImageView) findViewById(R.id.avatar);
                imageView.setImageBitmap(bitmap);
            } catch (FileNotFoundException e) {
                Log.e("Exception", e.getMessage(), e);
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }


}


















