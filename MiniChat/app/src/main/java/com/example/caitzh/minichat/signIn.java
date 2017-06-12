package com.example.caitzh.minichat;

import android.content.Intent;
import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class signIn extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);

        final View view1 = findViewById(R.id.view1);
        final View view2 = findViewById(R.id.view2);

        // 这里只是为了实现在输入账号和密码的时候，下面的下划线颜色会由灰变绿的效果
        // TODO 这里颜色变化有bug
        EditText miniNumber = (EditText) findViewById(R.id.miniNumber);
        miniNumber.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                view1.setBackgroundResource(R.color.colorLightGreen);
                view2.setBackgroundResource(R.color.colorGray);
            }
        });
        EditText password = (EditText) findViewById(R.id.password);
        password.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                view1.setBackgroundResource(R.color.colorGray);
                view2.setBackgroundResource(R.color.colorLightGreen);
            }
        });

        // 点击登录按钮
        final Button signin_btn = (Button) findViewById(R.id.signIn);
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
        final TextView forgetPw = (TextView) findViewById(R.id.forgetPassword);
        forgetPw.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(signIn.this, forgetPassword.class);
                startActivity(intent);
                Toast.makeText(signIn.this, "不要急哟~静待海涛完善该页面", Toast.LENGTH_LONG).show();
            }
        });

        // 点击 "前往注册"，跳转到注册页面
        TextView gotoRegister = (TextView) findViewById(R.id.gotoRegister);
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
