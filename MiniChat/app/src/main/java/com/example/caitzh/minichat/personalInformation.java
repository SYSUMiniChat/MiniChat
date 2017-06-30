package com.example.caitzh.minichat;

import android.content.ContentResolver;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Looper;
import android.os.Message;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
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

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
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
import java.util.UUID;

import android.os.Handler;

import com.example.caitzh.minichat.MyDB.userDB;
import com.example.caitzh.minichat.crh.chatWindow;
import static com.example.caitzh.minichat.XingeManager.unregister;
import static com.example.caitzh.minichat.middlewares.Check.checkHasNet;
import static com.example.caitzh.minichat.middlewares.Check.hasUpdate;


public class personalInformation extends AppCompatActivity implements View.OnTouchListener,
        GestureDetector.OnGestureListener{
    private userDB db = new userDB(personalInformation.this);
    String[] names = new String[] {"昵称","Mini号","性别","地区","Mini签名", "修改密码", "退出登录"};
    String[] details;   // 存储个人信息页面每一栏的具体内容

    ListView listView;
    TextView test_avatar;
    ImageView avatar;
    SimpleAdapter simpleAdapter;
    List<Map<String, String>> list;

    private LinearLayout linearLayout;
    private GestureDetector gestureDetector;

    // 底部的按钮切换
    private LinearLayout chatWindowLinearLayout;
    private LinearLayout friendsListLinearLayout;
    // 底部的按钮
    private ImageButton chat_img, maillist_img, information_img;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_personal_information);

        chatWindowLinearLayout = (LinearLayout)findViewById(R.id.id_tab_chat);
        friendsListLinearLayout = (LinearLayout)findViewById(R.id.id_tab_mail_list);
        chat_img = (ImageButton) findViewById(R.id.id_tab_chat_img);
        maillist_img = (ImageButton) findViewById(R.id.id_tab_mail_list_img);
        information_img = (ImageButton) findViewById(R.id.id_tab_personal_information_img);
        // 在当前页面 信息图标黑色，其他图标浅色
        chat_img.setImageDrawable(getResources().getDrawable(R.mipmap.chat));
        maillist_img.setImageDrawable(getResources().getDrawable(R.mipmap.maillist));
        information_img.setImageDrawable(getResources().getDrawable(R.mipmap.person_black));

        // 点击聊天图标
        chatWindowLinearLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(personalInformation.this, chatWindow.class);
                startActivity(intent);
                overridePendingTransition(R.anim.finish_immediately, R.anim.finish_immediately);
            }
        });
        // 点击联系人图标
        friendsListLinearLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(personalInformation.this,friendsList.class);
                startActivity(intent);
                overridePendingTransition(R.anim.finish_immediately, R.anim.finish_immediately);
            }
        });

        listView = (ListView) findViewById(R.id.listView);
        test_avatar = (TextView) findViewById(R.id.test_avatar);
        avatar = (ImageView) findViewById(R.id.avatar);

        linearLayout = (LinearLayout)findViewById(R.id.personal_information_linear_layout);
        linearLayout.setOnTouchListener(this);
        linearLayout.setLongClickable(true);
        listView.setOnTouchListener(this);
        listView.setLongClickable(true);
        gestureDetector = new GestureDetector((GestureDetector.OnGestureListener)this);

        new Thread(new Runnable() {
            @Override
            public void run() {
                String id = MyCookieManager.getUserId();
                Cursor cursor = db.findOneByNumber(id);
                String timestamp = null;
                if (cursor.moveToFirst()) {
                    updateUIFromDB(cursor);
                    timestamp = cursor.getString(cursor.getColumnIndex("finalDate"));
                }
                if (checkHasNet(getApplicationContext())) {
                    if (timestamp == null || hasUpdate(MyCookieManager.getUserId(), timestamp)) {
                        sendRequestWithHttpConnection(url_getUserInfo, "GET", "", "");
                    }
                } else {
                    Looper.prepare();
                    Toast.makeText(getApplicationContext(), "没有可用网络", Toast.LENGTH_LONG).show();
                    Looper.loop();
                }
            }
        }).start();

        // 点击头像这一栏 选择本地相册图片
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
                // 自定义对话框，显示头像大图
                LayoutInflater inflater = LayoutInflater.from(personalInformation.this);
                View toshow_view = inflater.inflate(R.layout.show_avatar, null);
                AlertDialog.Builder builder = new AlertDialog.Builder(personalInformation.this);
                Bitmap bitmap =((BitmapDrawable) avatar.getDrawable()).getBitmap();
                ImageView toshow_avatar = (ImageView) toshow_view.findViewById(R.id.show_avatar);
                toshow_avatar.setImageBitmap(bitmap);
                builder.setView(toshow_view).create().show();
            }
        });

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, final int position, long id) {
                Log.i("position:", position+"");
                if (position == 0) { // 点击"昵称"，跳转到更改名字页面
                    gotoChangeInfo("name", position);
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
                                sendRequestWithHttpConnection(url_updateUser, "POST", "sex", newSex);
                            } else {
                                Toast.makeText(getApplicationContext(), "当前没有可用网络", Toast.LENGTH_LONG).show();
                            }
                        }
                    }).setNegativeButton("取消", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            Toast.makeText(personalInformation.this, "取消更改性别", Toast.LENGTH_SHORT).show();
                        }
                    }).create().show();
                } else if (position == 3) { // 修改地区
                    gotoChangeInfo("address", position);
                } else if (position == 4) { // 修改Mini签名
                    gotoChangeInfo("signature", position);
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
                                    if (checkHasNet(getApplicationContext())) {
                                        sendRequestWithHttpConnection(url_verifyOldPw, "POST", "password",input);
                                    } else {
                                        Toast.makeText(getApplicationContext(), "当前没有可用网络", Toast.LENGTH_LONG).show();
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
                } else if (position == 6) {  // 退出登录
                    // 退出登录之前询问是否确定退出
                    AlertDialog.Builder builder = new AlertDialog.Builder(personalInformation.this);
                    builder.setMessage("确定退出当前账号?")
                            .setPositiveButton("确认", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    if (checkHasNet(getApplicationContext())) {  // 判断当前是否有可用网络
                                        sendRequestWithHttpConnection(url_logout, "GET", "", "");  // 发送Http请求
                                    } else {
                                        Toast.makeText(getApplicationContext(), "当前没有可用网络", Toast.LENGTH_LONG).show();
                                    }
                                }
                            })
                            .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    Toast.makeText(getApplicationContext(), "您取消了退出登录", Toast.LENGTH_LONG).show();
                                }
                            })
                            .create().show();
                }
            }
        });
    }

    private void gotoChangeInfo(String parameter, int position) {
        Intent intent =  new Intent(personalInformation.this, changeName.class);
        // 获取当前页面的内容，并通过Bundle传递参数
        String detail = list.get(position).get("detail");
        Bundle bundle = new Bundle();
        bundle.putString("parameter", parameter);
        bundle.putString("detail", detail);
        intent.putExtras(bundle);
        startActivityForResult(intent, 2);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {  // 刷新头像
            Uri uri = data.getData();
            ContentResolver cr = this.getContentResolver();
            try {
                Cursor c = cr.query(uri, null, null, null, null);
                //这是获取的图片保存在sdcard中的位置
                c.moveToFirst();
                String ImageName = c.getString(c.getColumnIndex("_display_name"));
                Log.v("TEST", ImageName);
                Bitmap bitmap = BitmapFactory.decodeStream(cr.openInputStream(uri));
                uploadFile(bitmap, ImageName);
            } catch (FileNotFoundException e) {
                Log.e("Exception", e.getMessage(), e);
            }
        } else if (requestCode == 2) {  // 刷新其他用户信息
            if (data != null) {
                String value = data.getStringExtra("value");
                int index = data.getIntExtra("index", 0);  // 这个0只是默认值
                if (index != 5) {  // 不要把密码显示在用户信息页面
                    list.get(index).put("detail", value);
                    listView.setAdapter(simpleAdapter);
                    simpleAdapter.notifyDataSetChanged();
                }
            }
        }
    }

    private static final String url_logout = "http://119.29.238.202:8000/logout";
    private static final String url_getUserInfo = "http://119.29.238.202:8000/getUserInfo";
    private static final String url_updateUser = "http://119.29.238.202:8000/updateUser";
    private static final String url_verifyOldPw = "http://119.29.238.202:8000/verifyOldPassword";

    private static final int UPDATE_LISTVIEW = 0;
    private static final int UPDATE_SEX = 1;
    private static final int GET_IMAGE_OK = 2;

    // 带有参数的请求
    private void sendRequestWithHttpConnection(final String url, final String method, final String parameter, final String value) {
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
                    if (url.equals(url_verifyOldPw)) {  // 验证旧密码
                        DataOutputStream outputStream = new DataOutputStream(connection.getOutputStream());
                        outputStream.writeBytes("password=" + value);
                    } else if (url.equals(url_updateUser)) {  // 更新用户信息
                        String date = DataManager.getCurrentDate();
                        DataOutputStream outputStream = new DataOutputStream(connection.getOutputStream());
                        if (parameter.equals("sex")) {
                            String sex = URLEncoder.encode(value, "utf-8");
                            outputStream.writeBytes("sex=" + sex + "&timestamp=" + date);
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
                            Intent intent = new Intent(personalInformation.this, changePassword.class);
                            intent.putExtra("miniNumber", list.get(1).get("detail"));  // 传递参数: mini号
                            startActivityForResult(intent, 2);
                        } else if (url.equals(url_updateUser)) {  //  更改用户信息
                            if (parameter.equals("sex")) {
                                // 更换性别后更新页面UI
                                list.get(2).put("detail", value);
                                // 获取当前修改时间
                                String date = DataManager.getCurrentDate();
                                // 同时更新本地数据
                                userDB db = new userDB(getBaseContext());
                                db.updateInfo(MyCookieManager.getUserId(), "sex", value, date);
                                // 利用message传递信息给handler
                                Message message_ = new Message();
                                message_.what = UPDATE_SEX;
                                handler.sendMessage(message_);
                            }
                        } else if (url.equals(url_logout)) { // 退出成功
                            unregister(getApplicationContext());
                            MyCookieManager.deleteCookie();
                            finish();  // 结束当前activity
                            Intent intent = new Intent(personalInformation.this, signIn.class); // 跳转到登录页面
                            startActivity(intent);
                        } else if (url.equals(url_getUserInfo)) {  // 获取用户信息
                            Log.i("message: ", message);
                            JSONObject information = new JSONObject(message);
                            String avatars = information.getString("avatar");
                            String city = information.getString("city");
                            String id = information.getString("id");
                            String nickname = information.getString("nickname");
                            String sex = information.getString("sex");
                            String signature = information.getString("signature");
                            details = new String[] {nickname, id, sex, city, signature, "", ""};
                            db.insert2Table(id, nickname, sex, city, signature, avatars, DataManager.getCurrentDate());
                            getImage(avatars);  // 通过访问返回的图片路径去获取图片
                            // 利用message传递信息给handler
                            Message message_ = new Message();
                            message_.what = UPDATE_LISTVIEW;
                            handler.sendMessage(message_);
                        }
                    } else {
                        Looper.prepare();
                        Toast.makeText(personalInformation.this, message, Toast.LENGTH_LONG).show();
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
    // 获取路径下的图片
    private void getImage(final String path) {
        Thread thread = new Thread() {
            @Override
            public void run() {
                Bitmap bm = ImageUtil.getImage(path);
                if (bm != null) {
                    // 保存头像到本地
                    int start = path.lastIndexOf('/');
                    ImageUtil.saveImage(path.substring(start+1), bm);
                    //发生更新UI的消息
                    Message msg = handler.obtainMessage();
                    msg.obj = bm;
                    msg.what = GET_IMAGE_OK;
                    handler.sendMessage(msg);
                }
            }
        };
        thread.start();
    }

    // 利用Handler来更新UI
    private Handler handler = new Handler() {
        public void handleMessage(Message message) {
            switch (message.what) {
                case UPDATE_LISTVIEW:   // 更新listView内容显示
                    try {
                        list = new ArrayList<>();
                        for (int i = 0; i < 7; ++i) {   // 更新字符串数组的内容
                            Map<String, String> listItem = new HashMap<>();
                            listItem.put("name", names[i]);
                            listItem.put("detail", details[i]);
                            list.add(listItem);
                        }
                        simpleAdapter = new SimpleAdapter(getApplicationContext(), list, R.layout.personal_information_item,
                                    new String[] {"name", "detail"}, new int[] {R.id.name, R.id.detail});
                        listView.setAdapter(simpleAdapter);
                        simpleAdapter.notifyDataSetChanged();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    break;
                case UPDATE_SEX:  // 更改性别后刷新listView
                    try {
                        listView.setAdapter(simpleAdapter);
                        simpleAdapter.notifyDataSetChanged();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    break;
                case GET_IMAGE_OK:  // 设置头像
                    try {
                        avatar.setImageBitmap((Bitmap) message.obj);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    break;
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

        Log.e("水平距离3", Float.toString((e1.getX() - e2.getX())));
        Log.e("水平速度3", Float.toString(Math.abs(velocityX)));
        //右
        if(e1.getX() - e2.getX() < - FLING_MIN_DISTANCE && Math.abs(velocityX) > FLING_MIN_VELOCITY){
            Intent intent = new Intent(personalInformation.this, friendsList.class);
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


    private void uploadFile(Bitmap bitmap, final String name) {
        final String CHARSET = "utf-8";
        final String BOUNDARY =  UUID.randomUUID().toString();  //边界标识   随机生成
        final String PREFIX = "--" , LINE_END = "\r\n";
        final String CONTENT_TYPE = "multipart/form-data";   //内容类型
        // 显示进度框
        // showProgressDialog();
        Bitmap compress = Bitmap.createScaledBitmap(bitmap, 256, 256, true);
        String imageType = name.substring(name.lastIndexOf('.'));
        final String saveName = MyCookieManager.getUserId() + imageType;
        Log.v("TEST", saveName);
        ImageUtil.saveImage(saveName, compress);
        ImageView imageView = (ImageView) findViewById(R.id.avatar);
        imageView.setImageBitmap(compress);
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    URL url = new URL(url_updateUser);
                    HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                    MyCookieManager.setCookie(conn);
                    conn.setReadTimeout(8000);
                    conn.setConnectTimeout(8000);
                    conn.setDoInput(true);  //允许输入流
                    conn.setDoOutput(true); //允许输出流
                    conn.setUseCaches(false);  //不允许使用缓存
                    conn.setRequestMethod("POST");  //请求方式
                    conn.setRequestProperty("Charset", CHARSET);  //设置编码
                    conn.setRequestProperty("connection", "keep-alive");
                    conn.setRequestProperty("Content-Type", CONTENT_TYPE + "; boundary=" + BOUNDARY);

                    /**
                     * 当文件不为空，把文件包装并且上传
                     */
                    DataOutputStream dos = new DataOutputStream(conn.getOutputStream());
                    StringBuffer sb = new StringBuffer();
                    String date = DataManager.getCurrentDate();
                    sb.append(PREFIX).append(BOUNDARY).append(LINE_END);
                    sb.append("Content-Disposition: form-data; name=\"").append("timestamp").append("\"").append(LINE_END).append(LINE_END);
                    sb.append(date).append(LINE_END);
                    dos.write(sb.toString().getBytes());
                    /**
                     * 这里重点注意：
                     * name里面的值为服务器端需要key   只有这个key 才可以得到对应的文件
                     * filename是文件的名字，包含后缀名的   比如:abc.png
                     */
                    sb = new StringBuffer();
                    sb.append(PREFIX).append(BOUNDARY).append(LINE_END);
                    sb.append("Content-Disposition: form-data; name=\"avatar\";filename=\""+name+"\""+LINE_END);
                    sb.append("Content-Type: image/pjpeg; charset="+CHARSET+LINE_END);
                    sb.append(LINE_END);
                    dos.write(sb.toString().getBytes());
                    File file = new File(ImageUtil.dir + "/" + saveName);
                    InputStream is = new FileInputStream(file);
                    byte[] bytes = new byte[1024];
                    int len = 0;
                    while((len = is.read(bytes)) != -1){
                        dos.write(bytes, 0, len);
                    }
                    is.close();
                    dos.write(LINE_END.getBytes());
                    byte[] end_data = (PREFIX+BOUNDARY+PREFIX+LINE_END).getBytes();
                    dos.write(end_data);

                    dos.flush();

                    // 提交到的数据转化为字符串
                    InputStream inputStream = conn.getInputStream();
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
                    Looper.prepare();
                    Toast.makeText(personalInformation.this, message, Toast.LENGTH_LONG).show();
                    Looper.loop();

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    private void updateUIFromDB(Cursor cursor) {
        String id = MyCookieManager.getUserId();
        String nickname = cursor.getString(cursor.getColumnIndex("nickname"));
        String sex = cursor.getString(cursor.getColumnIndex("sex"));
        String city = cursor.getString(cursor.getColumnIndex("city"));
        String signature = cursor.getString(cursor.getColumnIndex("signature"));
        String path = cursor.getString(cursor.getColumnIndex("avatar"));
        details = new String[] {nickname, id, sex, city, signature, "", ""};
        Message message_ = new Message();
        message_.what = UPDATE_LISTVIEW;
        handler.sendMessage(message_);

        Message msg = new Message();
        msg.what = GET_IMAGE_OK;
        msg.obj = ImageUtil.openImage(path);
        handler.sendMessage(msg);
    }
}


















