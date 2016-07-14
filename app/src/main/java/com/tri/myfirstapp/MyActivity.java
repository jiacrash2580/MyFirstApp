package com.tri.myfirstapp;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

public class MyActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my);
    }

    public void btnClick(View view) {
        Intent intent = new Intent(this, DisplayMessageActivity.class);
        EditText editText = (EditText) findViewById(R.id.text_id);
        String msg = editText.getText().toString();
        intent.putExtra("url", "http://210.74.194.118:8082/gw-web-manager/gws/fileExpressList");
        intent.putExtra("token", "eXVud2VpfDc2RDgwMjI0NjExRkM5MTlBNUQ1NEYwRkY5RkJBNDQ2fDE0NjY2NjQ1ODUwMzI");
        intent.putExtra("pageNum", "1");
        intent.putExtra("pageSize", "-1");
        intent.putExtra("status", "1");
        startActivity(intent);

        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
    }
}
