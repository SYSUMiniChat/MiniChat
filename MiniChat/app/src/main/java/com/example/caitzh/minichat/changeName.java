package com.example.caitzh.minichat;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

public class changeName extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_name);

        final EditText editText = (EditText)findViewById(R.id.edit_change_name);
        Bundle bundle = this.getIntent().getExtras();
        if (bundle != null) {
            editText.setText(bundle.getString("name"));  // 默认内容为个人信息
        }
        editText.setSelection(editText.length());  // 设置光标在最后

        // 点击返回箭头
        ImageView btn_back = (ImageView) findViewById(R.id.backToInformation);
        btn_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(changeName.this, personalInformation.class);
                startActivity(intent);
            }
        });

        // 点击保存按钮
        Button btn_save = (Button) findViewById(R.id.save_name);
        btn_save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(changeName.this, personalInformation.class);
                // 传递修改后的文字内容给个人信息页面
                Bundle bundle1 = new Bundle();
                bundle1.putString("name", editText.getText().toString());
                intent.putExtras(bundle1);
                startActivity(intent);
            }
        });
    }
}
