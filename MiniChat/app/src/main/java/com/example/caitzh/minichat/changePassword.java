package com.example.caitzh.minichat;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

public class changePassword extends AppCompatActivity {

    TextView miniNumber;
    Button btn_save;
    EditText password, confirmPassword;
    ImageView password_visible, confirm_visible;
    boolean visible = false, confirmVisible = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_password);

        miniNumber = (TextView) findViewById(R.id.miniNumber);
        btn_save = (Button) findViewById(R.id.save_password);
        password = (EditText) findViewById(R.id.password);
        confirmPassword = (EditText) findViewById(R.id.confirmPassword);
        password_visible = (ImageView) findViewById(R.id.visible);
        confirm_visible = (ImageView) findViewById(R.id.confirmVisible);

        Intent intent = this.getIntent();
        miniNumber.setText(intent.getStringExtra("miniNumber"));  // 获取跳转页面时传递的参数:mini账号

        // 点击密码可见按钮
        password_visible.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!visible) {
                    password.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                    password_visible.setImageDrawable(getResources().getDrawable(R.mipmap.visible));
                } else {
                    password.setTransformationMethod(PasswordTransformationMethod.getInstance());
                    password_visible.setImageDrawable(getResources().getDrawable(R.mipmap.invisible));
                }
                visible = !visible;
                password.postInvalidate();
                password.setSelection(password.length());
            }
        });
        confirm_visible.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!confirmVisible) {
                    confirmPassword.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                    confirm_visible.setImageDrawable(getResources().getDrawable(R.mipmap.visible));
                } else {
                    confirmPassword.setTransformationMethod(PasswordTransformationMethod.getInstance());
                    confirm_visible.setImageDrawable(getResources().getDrawable(R.mipmap.invisible));
                }
                confirmVisible = !confirmVisible;
                confirmPassword.postInvalidate();
                confirmPassword.setSelection(confirmPassword.length());
            }
        });


        // 点击保存按钮
        btn_save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String text_password = password.getText().toString();
                String text_confirm = confirmPassword.getText().toString();
                if (text_password.equals(text_confirm)) {  // 密码前后一致
                    finish();  // 结束当前activity
                    Intent intent = new Intent(changePassword.this, personalInformation.class);
                    startActivity(intent);
                } else {
                    Toast.makeText(changePassword.this, "密码前后不一致，请重新输入", Toast.LENGTH_LONG).show();
                    confirmPassword.setText("");
                }
            }
        });
        // 给AppCompatActivity的标题栏上加上返回按钮
        android.support.v7.app.ActionBar actionBar = getSupportActionBar();
        if(actionBar != null){
            actionBar.setHomeButtonEnabled(true);
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

    }

    // 返回按钮
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                this.finish(); // back button
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

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
}
