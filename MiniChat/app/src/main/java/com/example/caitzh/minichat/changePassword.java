package com.example.caitzh.minichat;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

public class changePassword extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_password);

        Intent intent = this.getIntent();
        TextView miniNumber = (TextView) findViewById(R.id.miniNumber);
        miniNumber.setText(intent.getStringExtra("miniNumber"));

        // 点击保存按钮
        Button btn_save = (Button) findViewById(R.id.save_password);
        btn_save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EditText password = (EditText) findViewById(R.id.password);
                EditText comfirm_password = (EditText) findViewById(R.id.confirmPassword);
                String text_password = password.getText().toString();
                String text_comfirm = comfirm_password.getText().toString();
                if (text_password.equals(text_comfirm)) {  // 密码前后一致
                    finish();  // 结束当前activity
                    Intent intent = new Intent(changePassword.this, personalInformation.class);
                    // TODO 更新数据库内容 密码
                    startActivity(intent);
                } else {
                    Toast.makeText(changePassword.this, "密码前后不一致，请重新输入", Toast.LENGTH_LONG).show();
                    comfirm_password.setText("");
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
}
