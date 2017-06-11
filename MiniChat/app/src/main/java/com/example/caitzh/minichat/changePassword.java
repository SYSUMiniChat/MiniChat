package com.example.caitzh.minichat;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
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

        // 点击返回箭头
        ImageView btn_back = (ImageView) findViewById(R.id.backToInformation);
        btn_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(changePassword.this, personalInformation.class);
                startActivity(intent);
            }
        });

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
                    Intent intent = new Intent(changePassword.this, personalInformation.class);
                    // TODO 更新数据库内容 密码
                    startActivity(intent);
                } else {
                    Toast.makeText(changePassword.this, "密码前后不一致，请重新输入", Toast.LENGTH_LONG).show();
                    comfirm_password.setText("");
                }
            }
        });
    }
}
