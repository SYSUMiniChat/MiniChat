package com.example.caitzh.minichat;

import android.content.Intent;
import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class signIn extends AppCompatActivity {

    EditText miniNumber, password;
    View view1, view2;
    ImageView password_visible;
    boolean visible = false;
    Button signin_btn;
    TextView forgetPw,gotoRegister;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);

        miniNumber = (EditText) findViewById(R.id.miniNumber);
        password = (EditText) findViewById(R.id.password);
        view1 = findViewById(R.id.view1);
        view2 = findViewById(R.id.view2);
        password_visible = (ImageView) findViewById(R.id.visible);
        signin_btn = (Button) findViewById(R.id.signIn);
        forgetPw = (TextView) findViewById(R.id.forgetPassword);
        gotoRegister = (TextView) findViewById(R.id.gotoRegister);

        // 点击密码可见图片
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

        // 这里只是为了实现在输入账号和密码的时候，下面的下划线颜色会由灰变绿的效果
        // TODO 这里颜色变化有bug
        miniNumber.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                view1.setBackgroundResource(R.color.colorLightGreen);
                view2.setBackgroundResource(R.color.colorGray);
            }
        });
        password.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                view1.setBackgroundResource(R.color.colorGray);
                view2.setBackgroundResource(R.color.colorLightGreen);
            }
        });

        // 点击登录按钮
        signin_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();  // 结束当前activity
                // 登录成功后跳转到用户信息页面(后面应该改成用户页面)
                Intent intent = new Intent(signIn.this, personalInformation.class);
                startActivity(intent);
                Toast.makeText(signIn.this, "登录成功", Toast.LENGTH_LONG).show();
            }
        });

        // 点击 "忘记密码",跳转到忘记密码页面
        forgetPw.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(signIn.this, forgetPassword.class);
                startActivity(intent);
            }
        });

        // 点击 "前往注册"，跳转到注册页面
        gotoRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();  // 结束当前activity
                Intent intent = new Intent(signIn.this, register.class);
                startActivity(intent);
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
}
