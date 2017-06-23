package com.example.caitzh.minichat.crh;

import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.PagerTabStrip;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.example.caitzh.minichat.MyCookieManager;
import com.example.caitzh.minichat.R;
import com.example.caitzh.minichat.changeAddress;
import com.example.caitzh.minichat.changeName;
import com.example.caitzh.minichat.changePassword;
import com.example.caitzh.minichat.changeSignature;
import com.example.caitzh.minichat.personalInformation;
import com.example.caitzh.minichat.signIn;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 实现页面滑动
 */
// TODO:加入图片和tab名称
public class MainSlider extends AppCompatActivity implements View.OnClickListener {
    private ViewPager mViewPager;
    private List<View> mViews = new ArrayList<>();//保存微信，朋友，通讯录，设置4个界面的view
    //底部的四个tab按钮，微信，朋友，通讯录，设置
    private LinearLayout mTabChat;
    private LinearLayout mTabMailList;
    private LinearLayout mTabPersonInformation;

    private ImageButton mChatImg;
    private ImageButton mMailListImg;
    private ImageButton mPersonInformationImg;

    private View tab01, tab02, tab03;

    // 以下为个人信息页面的控件
    String[] names = new String[] {"昵称","Mini号","性别","地区","Mini签名", "修改密码", "退出登录"};
    String[] details;   // 存储个人信息页面每一栏的具体内容

    ListView listView;
    TextView test_avatar;
    ImageView avatar;
    SimpleAdapter simpleAdapter;
    List<Map<String, String>> list;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_slider);
        initView();
        initEvents();
    }

    private void initEvents() {
        //给底部的4个LinearLayout即4个导航控件添加点击事件
        mTabChat.setOnClickListener(this);
        mTabMailList.setOnClickListener(this);
        mTabPersonInformation.setOnClickListener(this);
        //viewpager滑动事件
        mViewPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {

            @Override
            public void onPageSelected(int arg0) {//当viewpager滑动时，对应的底部导航按钮的图片要变化
                int currentItem = mViewPager.getCurrentItem();
                resetImg();
                switch (currentItem) {
                    case 0:
                        mChatImg.setImageResource(R.mipmap.chat_icon);
                        break;
                    case 1:
                        mMailListImg.setImageResource(R.mipmap.mail_list_icon);
                        break;
                    case 2:
                        mPersonInformationImg.setImageResource(R.mipmap.personal_information_icon);
                        break;
                    default:
                        break;
                }
            }

            @Override
            public void onPageScrolled(int arg0, float arg1, int arg2) {
                // TODO Auto-generated method stub

            }

            @Override
            public void onPageScrollStateChanged(int arg0) {
                // TODO Auto-generated method stub

            }
        });
    }

    private void initView() {//初始化所有的view
        mViewPager = (ViewPager)findViewById(R.id.viewPager);
        //tabs
        mTabChat = (LinearLayout)findViewById(R.id.id_tab_chat);
        mTabMailList = (LinearLayout)findViewById(R.id.id_tab_mail_list);
        mTabPersonInformation = (LinearLayout)findViewById(R.id.id_tab_personal_information);
        //imagebutton
        mChatImg = (ImageButton)findViewById(R.id.id_tab_chat_img);
        mMailListImg = (ImageButton)findViewById(R.id.id_tab_personal_information_img);
        mPersonInformationImg = (ImageButton)findViewById(R.id.id_tab_personal_information_img);
        //通过LayoutInflater引入布局，并将布局转化为view
        LayoutInflater mInflater = LayoutInflater.from(this);//创建一个LayoutInflater对象
        tab01 = mInflater.inflate(R.layout.activity_chat_window, null);//通过inflate方法动态加载一个布局文件
        // TODO: 更改通讯录以及个人信息的Layout的id
        tab02 = mInflater.inflate(R.layout.activity_friends_list, null);
        tab03 = mInflater.inflate(R.layout.activity_personal_information, null);
        setTab03();
        mViews.add(tab01);
        mViews.add(tab02);
        mViews.add(tab03);
        //为ViewPager设置adapter
        mViewPager.setAdapter(new ViewPagerAdapter(mViews));
    }
    @Override
    public void onClick(View v) {
        resetImg();//点击哪个tab,对应的颜色要变亮，因此，在点击具体的tab之前先将所有的图片都重置为未点击的状态，即暗色图片
        switch (v.getId()) {
            case R.id.id_tab_chat:
                mViewPager.setCurrentItem(0);//如果点击的是微信，就将viewpager的当前item设置为0，及切换到微信聊天的viewpager界面
                mChatImg.setImageResource(R.mipmap.chat_icon);//并将按钮颜色点亮
                break;
            case R.id.id_tab_mail_list:
                mViewPager.setCurrentItem(1);
                mMailListImg.setImageResource(R.mipmap.mail_list_icon);
                break;
            case R.id.id_tab_personal_information:
                mViewPager.setCurrentItem(2);
                mPersonInformationImg.setImageResource(R.mipmap.personal_information_icon);
                break;
            default:
                break;
        }

    }
    private void resetImg() {
        // TODO:添加滑动页面可以更改的icon，并进行对应滑动修改
        /*mChatImg.setImageResource(R.drawable.tab_Chat_normal);
        mMailListImg.setImageResource(R.drawable.tab_find_MailList_normal);
        mAddressImg.setImageResource(R.drawable.tab_address_normal);
        mSettingImg.setImageResource(R.drawable.tab_settings_normal);*/
    }


    /*@Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }*/

    // 一下为第三个页面的相关代码
    private void setTab03() {
        listView = (ListView)tab03.findViewById(R.id.listView);
        test_avatar = (TextView)tab03.findViewById(R.id.test_avatar);
        avatar = (ImageView)tab03.findViewById(R.id.avatar);

        Log.i("status", "登录后获取用户信息");
        if (checkHasNet(getApplicationContext())) {
            sendRequestWithHttpConnection(url_getUserInfo, "GET");
        } else {
            Toast.makeText(getApplicationContext(), "没有可用网络", Toast.LENGTH_LONG).show();
        }

        // 点击头像这一栏 选择本地相册图片 暂未实现拍摄功能
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
        avatar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 方法一：自定义对话框，显示头像大图
                LayoutInflater inflater = LayoutInflater.from(MainSlider.this);
                View toshow_view = inflater.inflate(R.layout.show_avatar, null);
                AlertDialog.Builder builder = new AlertDialog.Builder(MainSlider.this);
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

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, final int position, long id) {
                Log.i("position:", position+"");
                if (position == 0) { // 点击"昵称"，跳转到更改名字页面
                    Intent intent = new Intent(MainSlider.this, changeName.class);
                    // 获取当前页面的昵称，并通过Bundle传递参数
                    String cur_name = list.get(position).get("detail");
                    Bundle bundle = new Bundle();
                    bundle.putString("name", cur_name);
                    intent.putExtras(bundle);
                    startActivityForResult(intent, 2);
                } else if (position == 1) { // 点击"mini号"，提示一旦注册后不可修改
                    Toast.makeText(MainSlider.this, "Mini号不可修改喔~", Toast.LENGTH_LONG).show();
                } else if (position == 2) { // 点击"性别"，弹出可供选择的对话框
                    // 自定义对话框
                    LayoutInflater inflater = LayoutInflater.from(MainSlider.this);
                    final View newView = inflater.inflate(R.layout.choose_sex_layout, null);
                    AlertDialog.Builder builder = new AlertDialog.Builder(MainSlider.this);
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
                    Log.i("curSex:", curSex);
                    // 设置点击按钮对应的后续操作
                    builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            // 获取所选性别
                            String newSex = "男";
                            if (!radioButton_man.isChecked()) newSex = "女";
                            Log.i("newSex:", newSex);
                            if (checkHasNet(getApplicationContext())) {
                                sendRequestWithParameter(url_updateUser, "POST", "sex", newSex);
                            } else {
                                Toast.makeText(getApplicationContext(), "当前没有可用网络", Toast.LENGTH_LONG).show();
                            }
                        }
                    }).setNegativeButton("取消", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            Toast.makeText(MainSlider.this, "取消更改性别", Toast.LENGTH_SHORT).show();
                        }
                    }).create().show();
                } else if (position == 3) { // 修改地区
                    Intent intent = new Intent(MainSlider.this, changeAddress.class);
                    String cur_address = list.get(position).get("detail");
                    Bundle bundle = new Bundle();
                    bundle.putString("address", cur_address);
                    intent.putExtras(bundle);
                    startActivityForResult(intent, 2);
                } else if (position == 4) { // 修改Mini签名
                    Intent intent = new Intent(MainSlider.this, changeSignature.class);
                    String cur_signature = list.get(position).get("detail");
                    Bundle bundle = new Bundle();
                    bundle.putString("signature", cur_signature);
                    intent.putExtras(bundle);
                    startActivityForResult(intent, 2);
                } else if (position == 5) {  // 修改密码
                    // 先弹出对话框，输入原密码
                    LayoutInflater inflater = LayoutInflater.from(MainSlider.this);
                    final View newView = inflater.inflate(R.layout.comfirm_password, null);
                    AlertDialog.Builder builder = new AlertDialog.Builder(MainSlider.this);
                    builder.setView(newView)
                            .setPositiveButton("确认", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    // 原密码输入正确，可跳转到修改密码页面,否则提示密码错误
                                    EditText editText = (EditText) newView.findViewById(R.id.originPassword);
                                    String input = editText.getText().toString();
                                    if (checkHasNet(getApplicationContext())) {
                                        sendRequestWithParameter(url_verifyOldPw, "POST", "password",input);
                                    } else {
                                        Toast.makeText(getApplicationContext(), "当前没有可用网络", Toast.LENGTH_LONG).show();
                                    }
                                }
                            })
                            .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    Toast.makeText(MainSlider.this, "取消修改密码", Toast.LENGTH_LONG).show();
                                }
                            })
                            .create().show();
                } else if (position == 6) {  // 退出登录
                    if (checkHasNet(getApplicationContext())) {  // 判断当前是否有可用网络
                        sendRequestWithHttpConnection(url_logout, "GET");  // 发送Http请求
                    } else {
                        Toast.makeText(getApplicationContext(), "当前没有可用网络", Toast.LENGTH_LONG).show();
                    }
                }
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {  // 刷新头像
            Uri uri = data.getData();
            ContentResolver cr = this.getContentResolver();
            try {
                Bitmap bitmap = BitmapFactory.decodeStream(cr.openInputStream(uri));
                ImageView imageView = (ImageView) findViewById(R.id.avatar);
                imageView.setImageBitmap(bitmap);
            } catch (FileNotFoundException e) {
                Log.e("Exception", e.getMessage(), e);
            }
        } else if (requestCode == 2) {  // 刷新其他用户信息
            if (data != null) {
                String value = data.getStringExtra("value");
                int index = data.getIntExtra("index", 0);  // 这个0只是默认值
                if (index != 5) {  // 不要把密码显示在用户信息页面
                    list.get(index).put("detail", value);
                    simpleAdapter.notifyDataSetChanged();
                }
            }
        }
    }

    // 更新字符串数组的内容
    private void updateListView(String[] names, String[] details) {
        list = new ArrayList<>();
        for (int i = 0; i < 7; ++i) {
            Map<String, String> listItem = new HashMap<>();
            listItem.put("name", names[i]);
            listItem.put("detail", details[i]);
            list.add(listItem);
        }
    }

    // 判断是否有可用网络
    private boolean checkHasNet(Context context) {
        // 使用 ConnectivityManager 获取手机所有连接管理对象
        ConnectivityManager connectivityManager = (ConnectivityManager)
                getApplicationContext().getSystemService(context.CONNECTIVITY_SERVICE);
        if (connectivityManager != null) {
            // 使用 manager 获取网络连接管理的NetworkInfo对象
            NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
            if (networkInfo == null || !networkInfo.isAvailable()) {  // 是否为空或为非连接状态
                return false;
            } else {
                return true;
            }
        }
        return false;
    }

    private static final String url_logout = "http://119.29.238.202:8000/logout";
    private static final String url_getUserInfo = "http://119.29.238.202:8000/getUserInfo";
    private static final String url_updateUser = "http://119.29.238.202:8000/updateUser";
    private static final String url_verifyOldPw = "http://119.29.238.202:8000/verifyOldPassword";
    private static final int UPDATE_LISTVIEW = 0;
    private static final int UPDATE_SEX = 1;

    // 不带参数的请求
    private void sendRequestWithHttpConnection(final String url, final String method) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                HttpURLConnection connection = null;
                try {
                    Log.i("key", "Begin the connection");
                    // 获取一个HttpURLConnection实例化对象
                    connection = (HttpURLConnection) ((new URL(url).openConnection()));
                    // 需要登录的操作在连接之前设置好cookie
                    MyCookieManager.setCookie(connection);
                    // 设置请求方式和响应时间
                    connection.setRequestMethod(method);
                    connection.setReadTimeout(8000);
                    connection.setConnectTimeout(8000);
                    // 提交到的数据转化为字符串
                    InputStream inputStream = connection.getInputStream();
                    BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
                    StringBuilder response = new StringBuilder();
                    String line;
                    while ((line = reader.readLine()) != null) {
                        response.append(line);
                    }
                    // 从返回的JSON数据中提取关键信息
                    JSONObject result = new JSONObject(response.toString());
                    String code = result.getString("code");
                    String message = result.getString("message");
                    // test
                    Log.i("code: ", code + " message: " + message);
                    if (code.equals("0")) {
                        if (url.equals(url_logout)) { // 退出成功
                            finish();  // 结束当前activity
                            Intent intent = new Intent(MainSlider.this, signIn.class); // 跳转到登录页面
                            startActivity(intent);
                        } else if (url.equals(url_getUserInfo)) {  // 获取用户信息
                            Log.i("message: ", message);
                            JSONObject information = new JSONObject(message);
                            String avatar = information.getString("avatar");
                            String city = information.getString("city");
                            String id = information.getString("id");
                            String nickname = information.getString("nickname");
                            String sex = information.getString("sex");
                            String signature = information.getString("signature");
                            details = new String[] {nickname, id, sex, city, signature, "", ""};
                            // 利用message传递信息给handler
                            Message message_ = new Message();
                            message_.what = UPDATE_LISTVIEW;
                            message_.obj = avatar;
                            handler.sendMessage(message_);
                        }
                    } else {
                        Looper.prepare();
                        Toast.makeText(MainSlider.this, message, Toast.LENGTH_LONG).show();
                        Looper.loop();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                } finally {  // 关闭connection
                    if (connection != null)
                        connection.disconnect();
                }
            }
        }).start();
    }

    // 带有参数的请求
    private void sendRequestWithParameter(final String url, final String method, final String parameter, final String value) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                HttpURLConnection connection = null;
                try {
                    Log.i("key", "Begin the connection");
                    // 获取一个HttpURLConnection实例化对象
                    connection = (HttpURLConnection) ((new URL(url).openConnection()));
                    // 需要登录的操作在连接之前设置好cookie
                    MyCookieManager.setCookie(connection);
                    // 设置请求方式和响应时间
                    connection.setRequestMethod(method);
                    connection.setReadTimeout(8000);
                    connection.setConnectTimeout(8000);
                    DataOutputStream outputStream = new DataOutputStream(connection.getOutputStream());
                    if (url.equals(url_verifyOldPw)) {
                        outputStream.writeBytes("password=" + value);
                    } else if (url.equals(url_updateUser)) {
                        if (parameter.equals("sex")) {
                            String sex = URLEncoder.encode(value, "utf-8");
                            Log.i("parameter: ", sex);
                            outputStream.writeBytes("sex=" + sex);
                        } else if (parameter.equals("avatar")) {
                            outputStream.writeBytes("avatar=" + value);
                        }
                    }
                    // 提交到的数据转化为字符串
                    InputStream inputStream = connection.getInputStream();
                    BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
                    StringBuilder response = new StringBuilder();
                    String line;
                    while ((line = reader.readLine()) != null) {
                        response.append(line);
                    }
                    // 从返回的JSON数据中提取关键信息
                    JSONObject result = new JSONObject(response.toString());
                    String code = result.getString("code");
                    String message = result.getString("message");
                    Log.i("code:", code + " message: " + message);
                    if (code.equals("0")) {
                        if (url.equals(url_verifyOldPw)) {  // 密码验证正确
                            Intent intent = new Intent(MainSlider.this, changePassword.class);
                            intent.putExtra("miniNumber", list.get(1).get("detail"));  // 传递参数: mini号
                            startActivityForResult(intent, 2);
                        } else if (url.equals(url_updateUser)) {  //  更改用户信息
                            if (parameter.equals("sex")) {
                                Log.i("更改性别:", value);
                                // 更换性别后更新页面UI
                                list.get(2).put("detail", value);
                                // 利用message传递信息给handler
                                Message message_ = new Message();
                                message_.what = UPDATE_SEX;
                                handler.sendMessage(message_);
                            }
                        }
                    }
                    Looper.prepare();
                    Toast.makeText(MainSlider.this, message, Toast.LENGTH_LONG).show();
                    Looper.loop();
                } catch (Exception e) {
                    e.printStackTrace();
                } finally {  // 关闭connection
                    if (connection != null)
                        connection.disconnect();
                }
            }
        }).start();
    }


    // 利用Handler来更新UI
    private Handler handler = new Handler() {
        public void handleMessage(Message message) {
            switch (message.what) {
                case UPDATE_LISTVIEW:
                    try {
                        updateListView(names, details);
                        if (simpleAdapter == null) {
                            simpleAdapter = new SimpleAdapter(getApplicationContext(), list, R.layout.personal_information_item,
                                    new String[] {"name", "detail"}, new int[] {R.id.name, R.id.detail});
                        }
                        listView.setAdapter(simpleAdapter);
                        simpleAdapter.notifyDataSetChanged();  // 更新listView内容显示
                        // TODO 头像设置
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    break;
                case UPDATE_SEX:
                    try {
                        listView.setAdapter(simpleAdapter);
                        simpleAdapter.notifyDataSetChanged();  // 更新listView内容显示
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                default: break;
            }
        }
    };

}
